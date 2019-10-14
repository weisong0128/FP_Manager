#!/bin/sh
#功能简介：启动上层目录下的jar文件
#参数简介：
#    $1:jar文件名（包含后缀名）
#    注意：jar文件必须位于startup.sh目录的上一层目录。


jar_name="fp-0.0.1-SNAPSHOT.jar"
this_dir="$( cd "$( dirname "$0"  )" && pwd )"
log_dir="${this_dir}/log"
log_file="${log_dir}/catalina.out"
jar_file="${this_dir}/${jar_name}"
PROJECT_NAME="FPTOOL"


#当前目录
Now_Dir=`pwd`
#当前时间
timeStamp=`date "+%Y%m%d"`

##安装目录
success_dir="${this_dir}"

#日志文件夹不存在，则创建
if [ ! -d "${log_dir}" ]; then
    mkdir "${log_dir}"
fi



GLOBE_COMMON_CONF=${this_dir}/globe.common.conf
#安装类型
INSTALL_TYPE=$1

#colour level
SETCOLOR_SUCCESS="echo -en \\033[1;32m"
SETCOLOR_FAILURE="echo -en \\033[1;31m"
SETCOLOR_WARNING="echo -en \\033[1;33m"
SETCOLOR_NORMAL="echo -en \\033[0;39m"


#定义记录日志的函数
writeLog(){
    # print time
    time=`date "+%D %T"`
    echo "[$time] : ${PROJECT_NAME}-INSTALL : $*"
}

#获取全局公共配置中变量的方法，用到哪些变量，Need_Check_Keys和Need_Check_Vaules的值中就根据已有示例添加变量名，不用的删除。
init() {
	writeLog "initlization..."
	Need_Check_Keys=( FP_TOOL_PORT  HIVE_DB_URL  HIVE_DB_USERNAME  HIVE_DB_PASSWORD MYSQL_DB_URL  MYSQL_DB_USERNAME  MYSQL_DB_PASSWORD  NEWTABLE_TEMPLATE_FILEPATH   NEWTABLE_TEMPLATE_FILENAEM )
	Need_Check_Vaules=("$FP_TOOL_PORT" "$HIVE_DB_URL" "$HIVE_DB_USERNAME" "$HIVE_DB_PASSWORD" "$MYSQL_DB_URL" "$MYSQL_DB_USERNAME" "$MYSQL_DB_PASSWORD"  "$NEWTABLE_TEMPLATE_FILEPATH" "$NEWTABLE_TEMPLATE_FILENAEM" )
	for (( i=0; i<"${#Need_Check_Keys[*]}"; ++i ))
	do
		if [ -z "${Need_Check_Vaules[$i]}" ];then
			check_env "${Need_Check_Keys[$i]}"
		fi
	done
	alias cp='cp'

}


usage()
{
	#如果传递进来的参数个数不等于1，则记录错误并退出
	writeLog "---------------------------Usage---------------------------------------"
	writeLog " Input error ! You must input install type !"
	writeLog " sh install.sh <INSTALL_TYPE>"
	writeLog " Example:"
	writeLog " sh install.sh install          Install the application."
	writeLog " sh install.sh uninstall         Uninstall the application."
	writeLog " sh install.sh reinstall          Reinstall the application."
	writeLog " sh install.sh reconfig         Reset the config of the application."
}

LogMsg()
{
  time=`date "+%D %T"`
  echo "[$time] : INFO    : $*"
  $SETCOLOR_NORMAL
}

LogWarnMsg()
{
	time=`date "+%D %T"`
	$SETCOLOR_WARNING
	echo "[$time] : WARN    : $*"
	$SETCOLOR_NORMAL
}

LogSucMsg()
{
	time=`date "+%D %T"`
	$SETCOLOR_SUCCESS
	echo "[$time] : SUCCESS : $*"
	$SETCOLOR_NORMAL
}

LogErrorMsg()
{
	time=`date "+%D %T"`
	$SETCOLOR_FAILURE
	echo "[$time] : ERROR   : $*"
	$SETCOLOR_NORMAL
}

check_env()
{
	# 公共变量值为空获取私有配置文件中的值，如果存在空值，提示失败退出。
	line=`cat $GLOBE_COMMON_CONF | grep $1=`
	value=`echo $line | cut -d= -f2`
	if [ -z "${value}" ]; then
		LogErrorMsg "The value of $1 is null!"
		exit 1
	else
		export $1=$value
	fi
}


init



kill_progress()
{
	  sleep 3
	  #获取版本号
	  pid=`ps -ef | grep $jar_name | grep -v grep|awk '{print $2}'`
	  writeLog "正在杀掉进程 $pid"
	  #删除进程cd 
	  kill -9 $pid
}


uninstall()
{
	LogMsg "删除组件包" 
	if [[ -d $success_dir ]] ; then
		kill_progress
		rm -rf $success_dir
	fi
	LogSucMsg "卸载成功"

}

install()
{
	writeLog $success_dir

	writeLog "INFO: Check whether the port $FP_TOOL_PORT is in use..."
	count=`lsof -i:$FP_TOOL_PORT | wc -l`
	if [ $count -ne 0 ]; then
		LogErrorMsg "The port $FP_TOOL_PORT is in use!"
		exit 1
	fi

    #将globe.common.conf中的配置信息写入application.properties文件中
    applicationConf=./config/application.properties
    sed -i "s#server.port.*#server.port=${FP_TOOL_PORT}#g" $applicationConf
    sed -i "s#spring.datasource.hadoop.jdbc-url.*#spring.datasource.hadoop.jdbc-url=${HIVE_DB_URL}#g" $applicationConf
    sed -i "s#spring.datasource.hadoop.username.*#spring.datasource.hadoop.username=${HIVE_DB_USERNAME}#g" $applicationConf
    sed -i "s#spring.datasource.hadoop.password.*#spring.datasource.hadoop.password=${HIVE_DB_PASSWORD}#g" $applicationConf

    sed -i "s#spring.datasource.mysql.jdbc-url.*#spring.datasource.mysql.jdbc-url=${MYSQL_DB_URL}#g" $applicationConf
    sed -i "s#spring.datasource.mysql.username.*#spring.datasource.mysql.username=${MYSQL_DB_USERNAME}#g" $applicationConf
    sed -i "s#spring.datasource.mysql.password.*#spring.datasource.mysql.password=${MYSQL_DB_PASSWORD}#g" $applicationConf

    sed -i "s#newTable.template.filePath.*#newTable.template.filePath=${NEWTABLE_TEMPLATE_FILEPATH}#g" $applicationConf
    sed -i "s#newTable.template.fileName2.*#newTable.template.fileName2=${NEWTABLE_TEMPLATE_FILENAEM}#g" $applicationConf


     #父目录下jar文件存在
     if [ -f "${jar_file}" ]; then
         #启动jar包；重定向标准错误输出到文件，丢掉标准输出
        nohup  java  -jar ${jar_file} > "${log_file}" &
     else
         echo -e "\033[31m${jar_file}文件不存在！\033[0m"
         exit 1
     fi

	LogSucMsg "install success!"
	exit 0
}



#init
case "$INSTALL_TYPE" in
	install)
		install
		;;
	reinstall)
		uninstall
		install
		;;
	uninstall)
		uninstall
		;;
	reconfig)
		LogWarnMsg "暂不支持当前操作"
		;;
	*)
		usage
		exit 1
esac

cd "${this_dir}"
