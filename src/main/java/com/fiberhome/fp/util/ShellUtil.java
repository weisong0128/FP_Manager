package com.fiberhome.fp.util;

import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.fiberhome.fp.listener.event.FileStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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


    private static String encodedType = "utf-8";

    public static final int SLEEPTIME = 2000;
    public static final int SETPROCESS_5 = 5;
    public static final int SETPROCESS_10 = 10;

    public static final ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 20, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());


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

    private ShellUtil() {
    }

    /**
     * @description:
     * @Param:
     * @Return:
     * @Auth:User on 2019/9/6 16:28
     */
    public static boolean newShSuccess(String bashCommand, String uuid, String filePath) {

        Process pro = null;
        logging.debug("执行{}命令", bashCommand);

        try {
            pro = Runtime.getRuntime().exec(bashCommand);
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        }
        int success = -1;
        try {
            if (pro == null) {
                logging.error("调用脚本发生错误,无法返回输出流");
                return false;
            }
            Process finalPro = pro;
            AnalyseProcess analyseProcess = AnalyseProcess.map.get(uuid);
            FileStatus fileStatus = analyseProcess.getFileMap().get(filePath);
            pool.execute(() -> errorMsg(finalPro.getErrorStream(), fileStatus));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream(),encodedType));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(encodedType), encodedType);
                if (line.contains("analyse progress")) {
                    Integer progress = Integer.valueOf(line.substring(line.lastIndexOf(' ') + 1));
                    if (progress == SETPROCESS_10) {
                        fileStatus.setProcess(SETPROCESS_10);
                        fileStatus.setSuccess(true);
                        analyseProcess.getUnSuccessFileMap().remove(filePath);
                    } else if (progress == SETPROCESS_5) {
                        fileStatus.setProcess(SETPROCESS_5);
                        fileStatus.setShow(true);
                    }
                }
            }
            success = pro.waitFor();
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
        }
        return success == 0 ? true : false;
    }


    /**
     * 根据执行结果判断执行成功还是失败
     */
    public static boolean execSh(String bashCommand, String successFlag, String path) throws IOException, InterruptedException {
        Process pro = Runtime.getRuntime().exec(bashCommand);
        String fileName = "temp" + UUID.randomUUID() + ".txt";
        pool.execute(() -> errorMsg(pro.getErrorStream()));
        pool.execute(() -> {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            String line = null;
            FileWriter fWriter = null;
            try {
                fWriter = new FileWriter(path + File.separator + fileName, true);
            } catch (IOException e) {
                logging.error(e.getMessage(), e);
            }
            try {
                while (((line = bufferedReader.readLine()) != null)) {
                    line = new String(line.getBytes(encodedType), encodedType);
                    logging.info(String.format("执行%s脚本：输出%s", bashCommand, line));
                    fWriter.write(line + "\r\n");
                }
            } catch (Exception e) {
                logging.error(e.getMessage(), e);
            }

            try {
                fWriter.flush();
                fWriter.close();
                logging.info(String.format("输出内容写入文件%s", fileName));
                bufferedReader.close();
            } catch (IOException e) {
                logging.error(e.getMessage(), e);
            }
        });
        pro.waitFor();
        Thread.sleep(SLEEPTIME);//2秒等待输出内容写完在读取
        logging.info("读取{}文件", fileName);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path + File.separator + fileName)),encodedType));
        ) {
            String line = null;
            while ((line = br.readLine()) != null) {
                line = new String(line.getBytes(encodedType), encodedType);
                logging.info("读取{}文件内容{}", fileName, line);
                if (line.contains(successFlag)) {
                    FileUtil.deleteFile(path, fileName);
                    logging.info("删除{}文件", fileName);
                    return true;
                }
            }
        }
        return false;
    }

    public static void errorMsg(InputStream errorStream, FileStatus fileStatus) {
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(encodedType), encodedType);
                builder.append(line + "\r\n");
            }
            if (builder.length() > 0) {
                String errorResult = builder.toString();
                fileStatus.setErrorResult(errorResult);
                logging.info("错误输出流结果：{}", errorResult);
            }

        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        } finally {
            FileUtil.closeStream(bufferedReader);
            FileUtil.closeStream(errorStream);
        }
    }

    public static void errorMsg(InputStream errorStream) {
        logging.info("执行错误输出流");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(errorStream));
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(encodedType), encodedType);
                logging.info("执行错误输出流输出：{}", line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        } finally {
            try {
                errorStream.close();
            } catch (IOException e) {
                logging.error(e.getMessage(), e);
            }
        }
        try {
            errorStream.close();
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        }
    }
}

