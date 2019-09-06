package com.fiberhome.fp.util;

import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.fiberhome.fp.listener.event.FileStatus;
import com.fiberhome.fp.pojo.LogAnalze;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author fengxiaochun
 * @date 2019/7/5
 */
public class ShellUtil {

    static Logger logging = LoggerFactory.getLogger(ShellUtil.class);

    public static ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());


    /**
     * 执行命令
     *
     * @param bashCommand
     * @return
     */
    public static boolean shSuccess(String bashCommand) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(bashCommand);
        int status = pro.waitFor();
        return 0 == status;
    }

    /**
     * @description:
     * @Param:
     * @Return:
     * @Auth:User on 2019/9/6 16:28
     */
    public static boolean newShSuccess(String bashCommand, String uuid, String filePath) {

        logging.info(String.format("执行%s脚本", bashCommand));
        Process pro = null;
        try {
            pro = Runtime.getRuntime().exec(bashCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int success = -1;
        try {
            Process finalPro = pro;
            AnalyseProcess analyseProcess = AnalyseProcess.map.get(uuid);
            FileStatus fileStatus = analyseProcess.getFileMap().get(filePath);
            pool.execute(() -> errorMsg(finalPro.getErrorStream(), fileStatus));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                if (line.contains("analyse progress")) {
                    String[] split = line.split(" ");
                    Integer progress = Integer.valueOf(split[split.length - 1]);
                    if (progress == 10) {
                        fileStatus.setSuccess(true);
                        analyseProcess.getUnSuccessFileMap().remove(filePath);
                    } else if (progress == 5) {
                        fileStatus.setShow(true);
                    }
                }
            }
            success = pro.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (success == 0) {
            return true;
        }
        return false;
    }

    public static boolean shSuccess(String bashCommand, String logPath, String configPath) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(bashCommand);
        int status = pro.waitFor();
        return 0 == status;
    }


    /**
     * 根据执行结果判断执行成功还是失败
     */
    public static boolean execSh(String bashCommand, String successFlag, String path) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(bashCommand);
        String currentdate = String.valueOf(new Date().getTime());
        // String fileName = "temp" + currentdate + ".txt";
        String fileName = "temp" + UUID.randomUUID() + ".txt";
//        String path = "/home/analysis/fptool";
        pool.execute(() -> errorMsg(pro.getErrorStream()));
        pool.execute(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            FileWriter fWriter = null;
            try {
                fWriter = new FileWriter(path + File.separator + fileName, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    if (!((line = bufferedReader.readLine()) != null)) break;
                    line = new String(line.getBytes(), "utf-8");
                    logging.info(String.format("执行%s脚本：输出%s", bashCommand, line));
                    try {

                        fWriter.write(line + "\r\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                fWriter.flush();
                fWriter.close();
                logging.info(String.format("输出内容写入文件%s", fileName));
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        pro.waitFor();
        Thread.sleep(2000);//2秒等待输出内容写完在读取
        logging.info(String.format("读取%s文件", fileName));
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path + File.separator + fileName))));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes(), "utf-8");
            logging.info(String.format("读取%s文件内容%s", fileName, line));
            if (line.contains(successFlag)) {
                FileUtil.deleteFile(path, fileName);
                logging.info(String.format("删除%s文件", fileName));
                return true;
            }
           /* if (line.contains("ShowErrorAnalyse")) {

            }*/
        }
        br.close();
        return false;
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
//        String line = null;
//        while ((line = bufferedReader.readLine()) != null) {
//            line = new String(line.getBytes(), "utf-8");
//            logging.info(String.format("执行%s脚本：输出%s",bashCommand,line));
//            if (line.contains(successFlag)){
//                return true;
//            }
//        }
//        bufferedReader.close();
//        return false;
    }


    public static boolean execSh(String bashCommand, String uuid, String filePath, String ww) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(bashCommand);
//        String path = "/home/analysis/fptool";
        pool.execute(() -> errorMsg(pro.getErrorStream()));
        pool.execute(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            while (true) {
                try {
                    if (!((line = bufferedReader.readLine()) != null)) break;
                    line = new String(line.getBytes(), "utf-8");
                    logging.info(String.format("执行%s脚本：输出%s", bashCommand, line));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        pro.waitFor();
        Thread.sleep(2000);//2秒等待输出内容写完在读取
        return false;
    }


    public static void errorMsg(InputStream errorStream, FileStatus fileStatus) {
        StringBuilder builder = new StringBuilder();
        // logging.info("执行错误输出流");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                builder.append(line + "\r\n");
            }
            if (builder.length() > 0) {
                fileStatus.setErrorResult(builder.toString());
                logging.info("错误输出流结果：" + builder.toString());
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                errorStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            errorStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void errorMsg(InputStream errorStream) {
        logging.info("执行错误输出流");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                logging.info("执行错误输出流输出：" + line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                errorStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            errorStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


//    public static void write(){
//        String currentdate = String.valueOf(new Date().getTime());
//        String fileName = "temp"+currentdate+"txt";
//        String path = "D:\\zgd\\test";
////        File file = new File(path + File.separator + fileName);
//        FileWriter fWriter = null;
//        try {
//            fWriter = new FileWriter(path + File.separator + fileName, true);
//            for (int i=0;i<5;i++){
//                fWriter.write("11111111" + "\r\n");
//            }
//            fWriter.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        write();
//    }


}

