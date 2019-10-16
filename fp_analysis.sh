#!/usr/bin/env bash
CURR_DIR=$(cd "$(dirname $0)"; pwd)


#获取系统当前时间
date=`date +%Y-%m-%d-%H-%M-%S`
#获取分区变量
partition=`date +%Y%m`
day=`date +%Y-%m-%d`
mkdir ${CURR_DIR}/result/fp_${day} 1>/dev/null 2>/dev/null

one(){
#获取以下相关信息（在同一行的）：
#ThriftBinaryCLIService is stopped——FP停止
#Lost executor——Executor丢失
#] Could not open client transport with JDBC Uri: jdbc:hive2://localhost:10009/default——10009无法连接
#Running query 'create  function cl_init_start as 'cn.lucene.plugins.service.udf.CL_INIT_START''——FP启动
#INFO] {.*SN CHECK FAIL——授权失效
#invoking getApplicationReport——RM主备切换
#executor_error——其他executor错误
#Suppressed.*could only be replicated——DataNode错误-上海jxh-executor_error-0624-Canceled
#Name node is in——NameNode安全模式
#EXEC_CHECK——Executor数量不够
#closing properly——Beeline异常退出（Ctrl+C）-Canceled
#validate localhost——本地Beeline登录-Canceled

grep -E "ThriftBinaryCLIService is stopped|Lost executor|] Could not open client transport with JDBC Uri: jdbc:hive2://localhost:10009/default|Running query 'create  function cl_init_start as 'cn.lucene.plugins.service.udf.CL_INIT_START'' |INFO] {.*SN CHECK FAIL|invoking getApplicationReport|executor_error|Name node is in|EXEC_CHECK" ${cl_dir} |awk -v pjname="${pjname}"  -v pjlocation="${pjlocation}" -v capture_time="${capture_time}" '{
if (index($0,"Lost executor")) 
{
    split($10,a,":");
    host=a[1];
    if(!$21)
    {
        $21="null";
    }
    if($12=="heartbeat")
    {
        msg="[ERRO-EXEC-2301]Executor "$8"丢失！位于"host",心跳"$16"毫秒超时！";
		msg2="2301"
    }
    else if(index($0,"memory limits"))
    {  
        msg="[ERRO-EXEC-2304]Executor "$8"丢失！位于"host"，达到Executor允许使用内存上限！";
		msg2="2304"
    }
    else if(index($0,"was preempted"))
    {
        msg="[ERRO-EXEC-2303]Executor "$8"丢失！位于"host"，Executor资源被抢占！";
		msg2="2303"
    }
    else
		msg2="2000";
        msg="[ERRO-EXEC-2000]Executor "$8"丢失！位于"host"，Exit code为"$21
}

else if (match($0,/Running query.*ene.plugins.service.udf.CL_INIT_START/))
{msg="[INFO-BASE-0001]FP启动！";
msg2="0001"}

else if (index($0,"ThriftBinaryCLIService is stopped"))
{msg="[INFO-BASE-0002]FP关闭！";
msg2="0002"}

else if (index($0,"] Could not open client transport with JDBC Uri: jdbc:hive2://localhost:10009/default"))
{msg="[CRIT-CONN-4001]连接拒绝！FP基本服务不正常！";
msg2="4001"}

else if (index($0,"SN CHECK FAIL"))
{
msg2="5001";
msg="[ERRO-AUTH-5001]授权失败！请检查授权！"}

else if (index($0,"executor_error"))
{
    split($7,c,"@");
    code=c[1];
    errinfo="";
    for (i=8;i<=NF;i++)
    {
        errinfo=errinfo" "$i;
    }
	msg2="1000";
    msg="[ERRO-EXEC-1000]Executor内部错误！错误码"code"，错误详情："errinfo
}

else if (index($0,"getApplication"))
{msg="[CRIT-YARN-3001]触发ResourceManager主备切换！可能造成FP服务不可用！";
msg2="3001"}

else if (index($0,"could only be replicated"))
{
    info="";
    for(p=1;p<=NF;p++)
    {
        info=info" "$p;
    }
	msg2="3004";
    msg="[ERRO-HDFS-3004]DataNode存在异常，检查磁盘状态及DataNode服务存活情况！错误详情："info
    $1="";$2="<<Fetch time failed";$3=">>";
}

else if (index($0,"EXEC_CHECK"))
{msg2="5006";
msg="[WARN-EXEC-5006]Executor启动数未达设定值，可能造成FP服务不可用！"}

else if (index($0,"closing properly"))
{msg2="0004";
msg="[INFO-BASE-0004]Beeline连接退出"}

else if (index($0,"validate localhost"))
{msg2="0003";
msg="[INFO-BASE-0003]本地Beeline登录"}
else {$1=""}
;

if ($1!="")
{
time=$1" "$2;gsub(/[-:]/," ",time);
print mktime(time)"#####"msg2"#####"msg"#####"pjname"#####"pjlocation"#####"capture_time}
}' >> ${CURR_DIR}/result/fp_${day}/error_${date}
if [ $? -eq 0 ]
then
echo "analyse progress 1"
else
exit
fi
}

two(){
#获取以下相关信息（跨2行的）：
#namenode.SafeModeException——NameNode安全模式
#Bad connect ack with firstBadLink——DataNode存在故障
#java.lang.ClassNotFoundException——版本冲突
#Error validating the login——登录口令不正确
#has not privileges for——没有权限
#GSS initiate failed——Kerberos认证故障
#NoRouteToHostException——主机未配或机器无法连接

grep -E "namenode.SafeModeException|Bad connect ack with firstBadLink|java.lang.ClassNotFoundException|Error validating the login|has not privileges for|GSS initiate failed|NoRouteToHostException" -B 1 ${cl_dir} | awk -v pjname="${pjname}"  -v pjlocation="${pjlocation}" -v capture_time="${capture_time}" '{
if (index($0,"initializing SparkContext"))
{msg2="3003";
msg="[WARN-HDFS-3003]NameNode正在安全模式！"}
else if (index($0,"Exception in createBlockOutputStream"))
{msg2="5010";
msg="[WARN-HDFS-5010]DataNode疑似存在故障，需要检查！"}
else if (index($0,"mortbay.log"))
{msg2="5002";
msg="[WARN-BASE-5002]FP版本存在冲突！请检查lib下是否有多个lsql包！"}
else if (index($0,"negotiation failure"))
{msg2="5007";
msg="[WARN-BASE-5007]登录口令不正确！"}
else if (index($0,"setCLException"))
{msg2="5008";
msg="[WARN-BASE-5008]操作权限不足！"}
else if (index($0,"ApplicationClientProtocolPBClientImpl"))
{msg2="5009";
msg="[WARN-BASE-5009]Kerberos认证出现问题！"}
else if (index($0,"check_socket:fail"))
{
    split($7,e,",");
    $7=e[2];
    msg2="5011";
    msg="[WARN-BASE-5011]No route to host，主机未配或机器无法连接！问题主机："$7
}
else {$1=""}
;

if ($1!="")
{
time=$1" "$2;gsub(/[-:]/," ",time);
print mktime(time)"#####"msg2"#####"msg"#####"pjname"#####"pjlocation"#####"capture_time
}
}'  >> ${CURR_DIR}/result/fp_${day}/error_${date}

if [ $? -eq 0 ]
then
echo "analyse progress 2"
else
exit
fi
}

three(){
#分析SQL层面相关错误
#目前仅捞取错误，未对具体错误进行分析。同时暂时无法捞取分行sql（建表语句，with as复杂查询等）
grep -E -i  "ERROR] sqllog.*RUNNING|ERROR] sqllog.*CLOSED" -A 1  ${cl_dir} | awk  -v pjname="${pjname}"  -v pjlocation="${pjlocation}" -v capture_time="${capture_time}" '{
if (index($0,",RUNNING") || index($0,",CLOSED"))
{
    sqllog="";
    for(j=7;j<=NF-1;j++)
    {
        sqllog=sqllog" "$j;
    }
    msg2="0000";
    msg="[WARN-SQLs]该SQL执行异常:"sqllog
    #printf "%s %s.%s\t%s",$1,$2,$3,msg
	time=$1" "$2;gsub(/[-:]/," ",time);
    print mktime(time)"#####"msg2"#####"msg
};
if (index($0,"Exception: ") && !index($0,"[ERROR]") )
{
    errinfo=""
    for(k=1;k<=NF;k++)
    {
        errinfo=errinfo" "$k;
    }
    msg=" 异常信息来自："errinfo
    print msg"#####"pjname"#####"pjlocation"#####"capture_time;
};
if (index($0,"java.lang.InterruptedException"))
{
   msg=" 异常信息来自：java.lang.InterruptedException"
   print msg"#####"pjname"#####"pjlocation"#####"capture_time;
}
}' >> ${CURR_DIR}/result/fp_${day}/error_${date}

if [ $? -eq 0 ]
then
echo "analyse progress 3"
else
exit
fi
}

four_1(){
#对不合格SQL进行捞取：目前存在规则 
#未加limit；limit超3w； 使用select *； 使用partition like %
grep -E -i  "adapter sql" -A 1 ${cl_dir} | awk  -v pjname="${pjname}"  -v pjlocation="${pjlocation}" -v capture_time="${capture_time}" '{
if (index($0,"adapter sql") && ( ($NF+0>30000)  || ($8=="*") )) 
{
    sql="";msg="";msg2="";
    for (m=7;m<=NF;m++)
    {
        sql=sql" "$m;
    }
    $NF=$NF+0;
    if(sql==0) {sql="";}
    if(!(index(toupper(sql),"LIMIT")))
    {msg="查询未加limit;";
    msg2="请在查询中添加limit条件限制,limit不得超过3W;"}
    else if($NF > 30000)
    {msg=msg"limit超过30000;";
    msg2=msg2"危险查询！请调低limit设定的值,limit值超过3W可能会导致查询出现问题;"}
    if($8=="*")
    {msg=msg"使用select *;";
    msg2=msg2"危险查询！不要使用*作为查询条件,请填写详细列名;"}
    if(index(toupper(sql),"''%''"))
    {msg=msg"使用partition like %;";
    msg2=msg2"危险查询！不要使用%作为分区,请填写详细分区;"}	    
    time=$1" "$2;gsub(/[-:]/," ",time);
    print mktime(time)"#####"msg"#####"msg2"#####[WARN-SQLs]"sql"#####"pjname"#####"pjlocation"#####"capture_time
    };
}' > ${CURR_DIR}/result/fp_${day}/errsql_${date}


if [ $? -eq 0 ]
then
echo "analyse progress 4"
else
exit
fi
}

four_2(){
#对不合格SQL进行捞取：目前存在规则 
#未加limit；limit超3w； 使用select *； 使用partition like %
grep -E -i  "receiveSQL" -A 1 ${cl_dir} | awk  -v pjname="${pjname}"  -v pjlocation="${pjlocation}" -v capture_time="${capture_time}" '{
if (index($0,"receiveSQL") && ( ($NF+0>30000)  || ($7=="*") )) 
{
    sql="";msg="";msg2="";
    for (m=6;m<=NF;m++)
    {
        sql=sql" "$m;
    }
    $NF=$NF+0;
    if(sql==0) {sql="";}
    if(!(index(toupper(sql),"LIMIT")))
    {msg="查询未加limit;";
    msg2="请在查询中添加limit条件限制,limit不得超过3W;"}
    else if($NF > 30000)
    {msg=msg"limit超过30000;";
    msg2=msg2"危险查询！请调低limit设定的值,limit值超过3W可能会导致查询出现问题;"}
    if($7=="*")
    {msg=msg"使用select *;";
    msg2=msg2"危险查询！不要使用*作为查询条件,请填写详细列名;"}
    if(index(toupper(sql),"''%''"))
    {msg=msg"使用partition like %;";
    msg2=msg2"危险查询！不要使用%作为分区,请填写详细分区;"}	    
    time=$1" "$2;gsub(/[-:]/," ",time);
    print mktime(time)"#####"msg"#####"msg2"#####[WARN-SQLs]"sql"#####"pjname"#####"pjlocation"#####"capture_time
    };
}' > ${CURR_DIR}/result/fp_${day}/errsql_${date}


if [ $? -eq 0 ]
then
echo "analyse progress 4"
else
exit
fi
}

five()
{

sh /opt/software/lsql/bin/load.sh -t fp_operation_table -p ${partition} -tp txt -local -sp '#####' -f ${CURR_DIR}/result/fp_${day}/error_${date} -fl date,errcode,errinfo,pjname,pjlocation,capture_time 1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log
sh /opt/software/lsql/bin/load.sh -t err_result -p ${partition} -tp txt -local -sp '#####' -f ${CURR_DIR}/result/fp_${day}/errsql_${date} -fl date,tag,alter_tag,sql_result,pjname,pjlocation,capture_time   1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log

if [ $? -eq 0 ]
then
echo "analyse progress 5"
else
exit
fi

}

six_1()
{
#截取时间、时长、SQL并入库
cat ${cl_dir} | grep 'sqllog diff:' |awk -F ':' '{print $5}' >  ${CURR_DIR}/tmp/hs_${date}
if [ $? -ne 0 ];then
exit
else
cat ${cl_dir} | grep 'sqllog diff:' | awk -F 'strsql:|id:' '{print $2"#####'${pjname}'#####'${pjlocation}'#####'${capture_time}'" }' >${CURR_DIR}/tmp/sql_${date}
cat ${cl_dir} | grep 'sqllog diff:' | awk -F 'strsql:|id:' '{print $2"#####'${pjname}'#####'${pjlocation}'#####'${capture_time}'" }' >${CURR_DIR}/tmp/sql_${date}
cat ${cl_dir} | grep 'sqllog diff:' | awk -F ' ' '{print $1" "$2 }' |awk -F ',' '{gsub(/[-:]/," ",$1);print mktime($1)}' |  awk '{printf $0 ;getline<"'${CURR_DIR}'/tmp/hs_'${date}'";print "#####"$0}' | awk '{printf $0 ;getline<"'${CURR_DIR}'/tmp/sql_'${date}'";print "#####"$0}' > ${CURR_DIR}/result/fp_${day}/all_result_${date}
if [ $? -eq 0 ]
then
echo "analyse progress 6"
else
exit
fi

fi
}

six_2()
{
#截取时间、时长、SQL并入库
cat ${cl_dir} | grep 'sqllog diff:ok:' |awk -F ':' '{print $5}' >  ${CURR_DIR}/tmp/hs_${date}
if [ $? -ne 0 ];then
exit
else
cat ${cl_dir} | grep 'sqllog diff:ok:' | awk -F 'strsql:|id:' '{print $2"#####'${pjname}'#####'${pjlocation}'#####'${capture_time}'" }' >${CURR_DIR}/tmp/sql_${date}
cat ${cl_dir} | grep 'sqllog diff:ok:' | awk -F ' ' '{print $1" "$2 }' |awk -F ',' '{gsub(/[-:]/," ",$1);print mktime($1)}' |  awk '{printf $0 ;getline<"'${CURR_DIR}'/tmp/hs_'${date}'";print "#####"$0}' | awk '{printf $0 ;getline<"'${CURR_DIR}'/tmp/sql_'${date}'";print "#####"$0}' > ${CURR_DIR}/result/fp_${day}/all_result_${date}
if [ $? -eq 0 ]
then
echo "analyse progress 6"
else
exit
fi
fi
}


seven(){
cat ${CURR_DIR}/result/fp_${day}/all_result_${date} | grep -E -i 'insert|export' | tr a-z A-Z | awk '{ print $0"#####ins|exp"}' > ${CURR_DIR}/result/fp_${day}/ins_result_${date}
if [ $? -ne 0 ];then
exit
else
cat ${CURR_DIR}/result/fp_${day}/all_result_${date} | grep -v -E -i  'insert|export' | grep -i 'select' | tr a-z A-Z |  awk '{s=gsub(/SELECT/,"&");if(s<2) print $0"#####easy"}' > ${CURR_DIR}/result/fp_${day}/easy_result_${date}
cat ${CURR_DIR}/result/fp_${day}/all_result_${date} | grep -v -E -i  'insert|export' | grep -i 'select' | tr a-z A-Z | awk '{s=gsub(/SELECT/,"&");if(s>1) print $0"#####comp"}' > ${CURR_DIR}/result/fp_${day}/comp_result_${date}
cat ${CURR_DIR}/result/fp_${day}/all_result_${date} | grep -v -E -i 'insert|export|select' | tr a-z A-Z | awk '{ print $0"#####else"}' > ${CURR_DIR}/result/fp_${day}/else_result_${date}

if [ $? -eq 0 ]
then
echo "analyse progress 7"
else
exit
fi
fi
}

eight(){
java -jar FieldsFrequency.jar "${CURR_DIR}/result/fp_${day}/easy_result_${date}" "${CURR_DIR}/tmp/row_${date}"  1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log
if [ $? -ne 0 ];then
exit
else
java -jar FieldsFrequency.jar "${CURR_DIR}/result/fp_${day}/comp_result_${date}" "${CURR_DIR}/tmp/crow_${date}"  1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log
cat ${CURR_DIR}/tmp/row_${date} | awk '{print $0"#####'${pjname}'#####'${pjlocation}'#####'${capture_time}'"}' >${CURR_DIR}/result/fp_${day}/row_result_${date}
cat ${CURR_DIR}/tmp/crow_${date} | awk '{print $0"#####'${pjname}'#####'${pjlocation}'#####'${capture_time}'"}' >>${CURR_DIR}/result/fp_${day}/row_result_${date}
if [ $? -eq 0 ]
then
echo "analyse progress 8"
else
exit
fi
fi
}

nine(){
sh /opt/software/lsql/bin/load.sh -t all_result -p ${partition} -tp txt -local -sp '#####' -f ${CURR_DIR}/result/fp_${day}/ins_result_${date} -fl date,time,sql_result,pjname,pjlocation,capture_time,tag 1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log
sh /opt/software/lsql/bin/load.sh -t all_result -p ${partition} -tp txt -local -sp '#####' -f ${CURR_DIR}/result/fp_${day}/easy_result_${date} -fl date,time,sql_result,pjname,pjlocation,capture_time,tag 1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log
sh /opt/software/lsql/bin/load.sh -t all_result -p ${partition} -tp txt -local -sp '#####' -f ${CURR_DIR}/result/fp_${day}/else_result_${date} -fl date,time,sql_result,pjname,pjlocation,capture_time,tag 1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log

sh /opt/software/lsql/bin/load.sh -t sql_tmp -p ${partition} -tp txt -local -sp '#####' -f ${CURR_DIR}/result/fp_${day}/row_result_${date} -fl tag,date,row_name,table_name,pjname,pjlocation,capture_time 1>>${CURR_DIR}/tmp_log 2>>${CURR_DIR}/tmp_log
if [ $? -eq 0 ]
then
echo "analyse progress 9"
else
exit
fi
}

ten(){
if [ $? -eq 0 ]
then
echo "analyse progress 10"
else
exit
fi
}


#检查输入文件
if [ ! $1 ];
then
    echo -e "[ERROR]缺少变量！"
	echo -e "使用方法： sh fp_analysis.sh 日志文件地址 项目名称 项目地点 项目录入时间 下发参数"
    exit;
fi
if [ ! $2 ];
then
    echo -e "[ERROR]缺少变量！"
	echo -e "使用方法： sh fp_analysis.sh 日志文件地址 项目名称 项目地点 项目录入时间 下发参数"
    exit;
fi
if [ ! $3 ];
then
    echo -e "[ERROR]缺少变量！"
	echo -e "使用方法： sh fp_analysis.sh 日志文件地址 项目名称 项目地点 项目录入时间 下发参数"
    exit;
fi
if [ ! $4 ];
then
    echo -e "[ERROR]缺少变量!"
	echo -e "使用方法： sh fp_analysis.sh 日志文件地址 项目名称 项目地点 项目录入时间 下发参数"
    exit;
fi
if [ ! $5 ];
then
    echo -e "[ERROR]缺少变量!"
        echo -e "使用方法： sh fp_analysis.sh 日志文件地址 项目名称 项目地点 项目录入时间 下发参数"
    exit;
fi

cl_dir=$1
pjname=$2
pjlocation=$3
capture_time=$4
again=$5

echo ${date}"日志地址为"${cl_dir} >>${CURR_DIR}/tmp_log
echo ${date}"日志项目名称为"${pjname} >>${CURR_DIR}/tmp_log
echo ${date}"日志项目地点为"${pjlocation} >>${CURR_DIR}/tmp_log
echo ${date}"日志分析时间为"${capture_time} >>${CURR_DIR}/tmp_log

if [ ! -f ${cl_dir} ];
then
   echo -e "[ERROR]指定的目录不存在日志或路径指定错误！"
   exit;
fi 2>/dev/null


#版本判断
ifnewversion=`grep receiveSQL ${cl_dir}| wc -l`
if [[ $again -lt 5 ]]&&[[  $ifnewversion -gt 0 ]];then
one
two
three
four_2
five
six_2
seven
eight
nine
ten
elif [[ $again -lt 5 ]]&&[[  $ifnewversion -eq 0 ]];then
one
two
three
four_1
five
six_1
seven
eight
nine
ten
elif  [[ $again -ge 5 ]]&&[[  $ifnewversion -gt 0 ]];then
six_2
seven
eight
nine
ten
else
six_1
seven
eight
nine
ten
fi


