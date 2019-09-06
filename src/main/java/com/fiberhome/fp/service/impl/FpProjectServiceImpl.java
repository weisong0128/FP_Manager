package com.fiberhome.fp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.fp.dao.FpProjectDao;
import com.fiberhome.fp.pojo.FpProject;
import com.fiberhome.fp.service.FpProjectService;
import com.fiberhome.fp.util.FileUtil;
import com.fiberhome.fp.util.ShellUtil;
import com.fiberhome.fp.util.StrUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fengxiaochun
 * @date 2019/7/4
 */

@Service
public class FpProjectServiceImpl implements FpProjectService {

    Logger logging = LoggerFactory.getLogger(FpProjectServiceImpl.class);

    public static ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

    @Autowired
    FpProjectDao fpProjectDao;

    @Value("${update.conf.name}")
    String config;

    @Value("${update.conf.path}")
    String path;

    @Value("${shell.execute.path}")
    String shellPath;

    @Override
    public List<FpProject> listProject(String pjName) {
        List<FpProject> projects = fpProjectDao.listProject(pjName);
        if (projects != null && projects.size()>0){
            for (FpProject project:projects){
                project.setPjLocationList((List<String>)JSONObject.parse(project.getPjLocation()));
            }
        }
        return projects;
    }


    @Override
    public boolean upload(String dir,String project,String location) throws IOException, InterruptedException {

        FileUtil.replacerConf(path + File.separator + config,"cl_dir="+dir,"business="+project,"relief="+location);
        //单启线程 操作服务器 修改配置文件  运行fp_sql_analysis.sh脚本
        pool.execute(() -> {
//            FileUtil.replacerConf(path + File.separator + config,"cl_dir="+dir,"business="+project,"relief="+location);
            try {
                logging.info(String.format("执行日志分析命令：sh  %sfp_sql_analysis.sh",shellPath + File.separator));
                boolean ret = ShellUtil.shSuccess("sh " + shellPath + File.separator + "fp_sql_analysis.sh");
//                boolean ret = ShellUtil.shSuccess("sh " + shellPath + File.separator + "aa.sh");
                if (ret){
                    logging.info("执行日志分析fp_sql_analysis.sh成功");
                }
            } catch (IOException e) {
                logging.error("执行日志分析fp_sql_analysis.sh失败",e);
                e.printStackTrace();
            } catch (InterruptedException e) {
                logging.error("执行日志分析fp_sql_analysis.sh失败",e);
                e.printStackTrace();
            }
        });
        //单启线程 操作服务器 添加或修改项目和地市信息
        pool.execute(() -> {
            List<FpProject> projects = fpProjectDao.listProject(null);
            boolean flag = false;
            String currentdate = String.valueOf(System.currentTimeMillis());
            if (projects != null && projects.size()>0){
                for (FpProject dbpl:projects){
                    if (StringUtils.equals(dbpl.getPjName(),project)){
                        flag = true;
                        List<String> locationList = StrUtil.json2List(dbpl.getPjLocation());
                        if (!locationList.contains(location)){

                            String fileName = "update"+currentdate+".txt";
                            List<String> locations = (List<String>)JSONObject.parse(dbpl.getPjLocation());
                            locations.add(location);
                            String content = StrUtil.assemblyProject(project,locations);
                            FileUtil.creatAndWriteFile(path,fileName,content);
                            logging.info(String.format("修改%s项目地市命令：sh /opt/software/lsql/bin/load.sh -k pjname -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f %s",project,path + File.separator + fileName));
                            boolean ret = false;
                            try {
                                ret = ShellUtil.shSuccess(" sh /opt/software/lsql/bin/load.sh -k pjname -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f  "+path + File.separator + fileName+"  ");
                                if (ret){
                                    FileUtil.deleteFile(path,fileName);
                                    logging.info("修改地市成功，并删除修改文件");
                                }
                            } catch (IOException e) {
                                logging.error(String.format("修改地市%s失败",fileName),e);
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                logging.error(String.format("修改地市%s失败",fileName),e);
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            if (!flag){
                String fileName = "insert"+currentdate+".txt";
                String content = project + "," + location;
                FileUtil.creatAndWriteFile(path,fileName,content);
                logging.info(String.format("添加%s项目%s地市命令：sh /opt/software/lsql/bin/load.sh  -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f %s",project,location,path + File.separator + fileName));
                boolean ret = false;
                try {
                    ret = ShellUtil.shSuccess(" sh /opt/software/lsql/bin/load.sh  -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f "+path + File.separator + fileName+"  ");
                    if (ret){
                        FileUtil.deleteFile(path,fileName);
                        logging.info("添加项目成功，并删除添加文件");
                    }
                } catch (IOException e) {
                    logging.error(String.format("添加项目%s失败",fileName),e);
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    logging.error(String.format("添加项目%s失败",fileName),e);
                    e.printStackTrace();
                }

            }
        });

        logging.info(String.format("执行日志分析命令：sh  %sfp_err_analysis.sh",shellPath + File.separator));
        boolean ret = ShellUtil.execSh("sh " + shellPath + File.separator + "fp_err_analysis.sh","analysis end",path);
//        boolean ret = ShellUtil.execSh("sh " + shellPath + File.separator + "test.sh","test success");
        logging.info(String.format("执行日志分析fp_err_analysis.sh 结果：%s",ret));
        return ret;
    }




}
