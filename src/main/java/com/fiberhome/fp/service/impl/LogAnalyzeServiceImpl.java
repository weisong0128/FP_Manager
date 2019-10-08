package com.fiberhome.fp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fiberhome.fp.dao.FpProjectDao;
import com.fiberhome.fp.dao.LogAnalzeDao;
import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.fiberhome.fp.listener.event.FileStatus;
import com.fiberhome.fp.pojo.*;
import com.fiberhome.fp.service.FpProjectService;
import com.fiberhome.fp.service.LogAnalyzeService;
import com.fiberhome.fp.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Service
public class LogAnalyzeServiceImpl implements LogAnalyzeService {


    static Logger logging = LoggerFactory.getLogger(LogAnalyzeServiceImpl.class);
    @Autowired
    private LogAnalzeDao logAnalzeDao;
    @Autowired
    public FpProjectDao fpProjectDao;

    @Autowired
    private FpProjectService fpProjectService;
    @Autowired
    private AllResultServiceImpl allResultService;
    private ThreadPoolExecutor pool = new ThreadPoolExecutor(20, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
    // private Executor pool2 = Executors.newFixedThreadPool(1);

    @Value("${upload.log.path}")
    private String uploadLogPath;
    @Autowired
    private AnalyseProcess analyseProcess;

    @Value("${update.conf.name}")
    String config;

    @Value("${update.conf.path}")
    String path;

    @Value("${shell.execute.path}")
    String shellPath;

    @Value("${cut.file.size}")
    int cutfilesize;
    @Value("${cut.file.max.count}")
    int cutFileMaxCount;
    @Value("${template.file.dir}")
    String outFilePath;

    @Value("${template.file.name}")
    String templateName;

    @Value("${template.root.path}")
    String templateRootPath;


    private Map<String, AnalyseProcess> map = AnalyseProcess.getMap();

    public static final int SLEEPTIME = 1050;

    @Override
    public Response getLogListByParam(Page page, LogAnalze logAnalze) {
        return null;
    }


    @Override
    public boolean createLogAnalze(LogAnalze logAnalze) {
        return logAnalzeDao.createLogAnalze(logAnalze);
    }

    @Override
    public List<LogAnalze> findLogAnalyseList(LogAnalze param, Page page) {
        if (StringUtils.isNotEmpty(param.getProjectName())) {
            String[] arrays = param.getProjectName().split(",");
            param.setProjectNameList(new ArrayList<>(Arrays.asList(arrays)));
        }
        if (StringUtils.isNotEmpty(param.getAddress())) {
            String[] arrays = param.getAddress().split(",");
            param.setAddressList(new ArrayList<>(Arrays.asList(arrays)));
        }
        return logAnalzeDao.findLogAnalyseList(param, page);
    }


    @Override
    public boolean startAnalyse(String project, String location, String uuid, Long analyseTime) {
        String dir = uploadLogPath + File.separator + location + File.separator + project + File.separator + analyseTime;
        File direct = new File(dir);
        ArrayList<File> fileArrayList1 = new ArrayList<>();
        AnalyseProcess.fileSizeInit(uuid, FileUtil.getByteSize(cutfilesize), cutFileMaxCount);
        FileUtil.findAllSizeMore(direct, uuid);
        FileUtil.getSizeLLesser(direct, fileArrayList1, uuid);
        AnalyseProcess.init(uuid, fileArrayList1, project, location, analyseTime, dir);
        changeAnalyseMsg(project, location);
        upload(map.get(uuid));
        return true;
    }


    public void upload(AnalyseProcess analyseProcess) {
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        String dir =
                uploadLogPath + File.separator +
                        analyseProcess.getProjectLocation() + File.separator +
                        analyseProcess.getProjectName() + File.separator +
                        analyseProcess.getCreateTime();
        ConcurrentMap<String, FileStatus> fileMap =
                analyseProcess.getFileMap().size() != analyseProcess.getUnSuccessFileMap().size() ?
                        analyseProcess.getUnSuccessFileMap() :
                        analyseProcess.getFileMap();
        String uuid = analyseProcess.getUuid();
        String uploadFileRootPath = analyseProcess.getUploadFileRootPath();
        List<File> fileArrayList1 = map2FileList(fileMap);
        for (int i = 0; i < fileArrayList1.size(); i++) {
            File file = fileArrayList1.get(i);
            String filePath = file.getPath();
            Long createTime = analyseProcess.getCreateTime();
            String projectName = analyseProcess.getProjectName();
            String projectLocation = analyseProcess.getProjectLocation();
            executorService.execute(() -> {
                AnalyseProcess analyseProcess1 = map.get(uuid);
                FileStatus fileStatus = analyseProcess1.getFileMap().get(filePath);
                //调用脚本方法
                String bashCommand = "sh " + shellPath + File.separator + "fp_analysis.sh  " +
                        filePath + " " +
                        projectName + " " +
                        projectLocation + " " +
                        createTime + " " +
                        fileStatus.getProcess();
                ShellUtil.newShSuccess(bashCommand, uuid, filePath);
                LogAnalze logAnalze = fileStatus.setFinish(true);
                if (logAnalze != null) {
                    //如果本次分析完整
                    String objectSerializePath = analyseProcess1.getObjectSerializePath(dir);
                    FileUtil.filesDelete(objectSerializePath);
                    try {
                        if (analyseProcess1.getUnSuccessFileMap().size() == 0) {
                            FileUtil.deleteRootPathDir(new File(uploadFileRootPath), "_cut");
                        } else {
                            //将错误对象序列化到硬盘
                            FileUtil.objectOutputStreamDisk(analyseProcess, objectSerializePath);
                        }
                        logAnalzeDao.updateLogAnalze(logAnalze);
                    } catch (Exception e) {
                        logging.error(e.getMessage(), e);
                    }
                }
            });
            sleep(SLEEPTIME);
        }
    }

    public void changeAnalyseMsg(String project, String location) {
        pool.execute(() -> {
            List<FpProject> projects = fpProjectDao.listProject(null, null);
            boolean flag = false;
            String currentdate = String.valueOf(System.currentTimeMillis());
            if (projects != null && !projects.isEmpty()) {
                for (FpProject dbpl : projects) {
                    if (StringUtils.equals(dbpl.getPjName(), project)) {
                        flag = true;
                        List<String> locationList = StrUtil.json2List(dbpl.getPjLocation());
                        if (!locationList.contains(location)) {

                            String fileName = "update" + currentdate + ".txt";
                            List<String> locations = (List<String>) JSONObject.parse(dbpl.getPjLocation());
                            locations.add(location);
                            String content = StrUtil.assemblyProject(project, locations);
                            FileUtil.creatAndWriteFile(path, fileName, content);
                            logging.info(String.format("修改%s项目地市命令：sh /opt/software/lsql/bin/load.sh -k pjname -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f %s", project, path + File.separator + fileName));
                            boolean ret = false;
                            try {
                                ret = ShellUtil.shSuccess(" sh /opt/software/lsql/bin/load.sh -k pjname -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f  " + path + File.separator + fileName + "  ");
                                if (ret) {
                                    FileUtil.deleteFile(path, fileName);
                                    logging.info("修改地市成功，并删除修改文件");
                                    break;
                                }
                            } catch (IOException | InterruptedException e) {
                                logging.error(String.format("修改地市%s失败", fileName), e);
                                logging.error(e.getMessage(), e);
                            }
                        }
                    }
                }
            }

            if (!flag) {
                String fileName = "insert" + currentdate + ".txt";
                String content = project + "," + location;
                FileUtil.creatAndWriteFile(path, fileName, content);
                logging.info(String.format("添加%s项目%s地市命令：sh /opt/software/lsql/bin/load.sh  -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f %s", project, location, path + File.separator + fileName));
                boolean ret = false;
                try {
                    ret = ShellUtil.shSuccess(" sh /opt/software/lsql/bin/load.sh  -t fp_project -tp txt -local -sp , -fl pjname,pjlocation -f " + path + File.separator + fileName + "  ");
                    if (ret) {
                        FileUtil.deleteFile(path, fileName);
                        logging.info("添加项目成功，并删除添加文件");
                    }
                } catch (IOException | InterruptedException e) {
                    logging.error(String.format("添加项目%s失败", fileName), e);
                    logging.error(e.getMessage(), e);
                }

            }
        });
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            logging.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public List<ErrorResult> listErrorResult(Page page, ErrorResult errorResult) {
        if (StringUtils.isNotEmpty(errorResult.getPjName()) && !errorResult.getPjName().equalsIgnoreCase("")) {
            String[] pjNames = errorResult.getPjName().split(",");
            errorResult.setPjNameList(new ArrayList<>(Arrays.asList(pjNames)));
        }
        if (StringUtils.isNotEmpty(errorResult.getPjLocation()) && !errorResult.getPjLocation().equalsIgnoreCase("")) {
            String[] pjLocations = errorResult.getPjLocation().split(",");
            errorResult.setPjLocationList(new ArrayList<>(Arrays.asList(pjLocations)));
        }
        if (StringUtils.isNotEmpty(errorResult.getTag()) && !errorResult.getTag().equalsIgnoreCase("")) {
            String[] tagList = errorResult.getTag().split(",");
            errorResult.setTagList(new ArrayList<>(Arrays.asList(tagList)));
        }
        if (StringUtils.isNotEmpty(errorResult.getTag()) && !errorResult.getTag().equalsIgnoreCase("")) {
            String[] tagList = errorResult.getTag().split(",");
            errorResult.setTagList(new ArrayList<>(Arrays.asList(tagList)));
        }
        return logAnalzeDao.listErrResult(page, errorResult);
    }


    @Override
    public Boolean batchDeleteLogAnaylse(List<String> uuids) {
        for (String uuid : uuids) {
            try {
                if (map.containsKey(uuid)) {
                    map.remove(uuid);
                }
                LogAnalze oneLogAnalyse = logAnalzeDao.findOneLogAnalyse(uuid);
                String projectLocation = oneLogAnalyse.getAddress();
                String projectName = oneLogAnalyse.getProjectName();
                Long createTime = oneLogAnalyse.getCreateTime();
                String uploadFileRootPath = uploadLogPath + File.separator + projectLocation + File.separator + projectName + File.separator + createTime;
                FileUtil.deleteRootPathDir(new File(uploadFileRootPath), "_cut");
                logAnalzeDao.deleteLogAnalze(uuid);
            } catch (Exception e) {
                logging.error(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }


    private List<File> map2FileList(Map<String, FileStatus> fileMap) {
        ArrayList<File> list = new ArrayList<>();
        for (Map.Entry<String, FileStatus> entry : fileMap.entrySet()) {
            File file = new File(entry.getValue().getFilePath());
            if (file.exists()) {
                list.add(file);
            }
        }
        return list;
    }

    public LogAnalze findOneLogAnalyse(String uuid) {
        return logAnalzeDao.findOneLogAnalyse(uuid);
    }

    @Override
    public String wordExport(String pjName, String pjLocation, String createTime) {
        HashMap<String, Object> templateMap = new HashMap<>();
        templateMap.put("projectName", pjName);
        templateMap.put("projectLocation", pjLocation);
        //不合格sql占比
        AllResult proportionDate = logAnalzeDao.getProportion(pjName, pjLocation, createTime);
        int unqualifiedSql = proportionDate.getUnqualifiedSql();
        int qualifiedSql = proportionDate.getQualifiedSql();
        templateMap.put("errorResultPercent", unqualifiedSql * 100 / (qualifiedSql));
        //查询	不合格SQL 统计信息
        List<ErrorResult> errorResultList = logAnalzeDao.wordExportErrorResult(pjName, pjLocation, createTime);
        ArrayList<Map<String, String>> errResultList = new ArrayList<>();
        for (ErrorResult errorResult : errorResultList) {
            HashMap<String, String> inMap = new HashMap<>();
            inMap.put("tag", errorResult.getTag());
            inMap.put("alterTag", errorResult.getAlterTag());
            inMap.put("count", errorResult.getCount() + "");
            inMap.put("exam", errorResult.getSqlResult());
            errResultList.add(inMap);
        }
        templateMap.put("errResults", errResultList);
        //查询	数据库报错信息 统计信息
        List<FpOperationTable> fpOperationTableList = logAnalzeDao.wordExportFpOperationTable(pjName, pjLocation, createTime);
        ArrayList<Map<String, String>> operationList = new ArrayList<>();
        for (FpOperationTable fpOperationTable : fpOperationTableList) {
            HashMap<String, String> inMap = new HashMap<>();
            inMap.put("dateStr", fpOperationTable.getDateStr());
            inMap.put("errLevel", fpOperationTable.getErrLevel());
            inMap.put("count", fpOperationTable.getCount() + "");
            inMap.put("errInfo", fpOperationTable.getErrInfo());
            operationList.add(inMap);
        }
        templateMap.put("operations", operationList);
        List<String> wordAdvice = WordUtil.getWordAdvice(WordUtil.advice);
        templateMap.put("adviceList", wordAdvice);

        String outFileName = pjName + "_" + pjLocation + "_template_" + System.currentTimeMillis() + ".xml";
        WordUtil.wordExport(templateMap, templateName, outFilePath, outFileName, templateRootPath);
        //FileUtil.deleteFile(outFilePath,outFileName);
        return outFileName;
    }

    public List<LogAnalze> findLogAnalyseListByUuids(List<String> uuids) {
        return logAnalzeDao.findLogAnalyseListByUuids(uuids);
    }
}
