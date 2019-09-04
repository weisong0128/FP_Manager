package com.fiberhome.fp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengxiaochun
 * @date 2019/7/5
 */
public class FileUtil {

    static Logger logging = LoggerFactory.getLogger(ShellUtil.class);

    public static void creatDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }


    /**
     * 在path下创建文件file  写入内容
     *
     * @param path
     * @param fileName
     * @param content
     */
    public static void creatAndWriteFile(String path, String fileName, String content) {
        creatDir(path);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(path + File.separator + fileName));
            bw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isFileExixts(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 删除path下的file
     *
     * @param path
     * @param fileName
     */
    public static void deleteFile(String path, String fileName) {
        File file = new File(path + File.separator + fileName);
        if (file.exists()) {
            file.delete();
        }
    }


    /**
     * 修改配置文件
     *
     * @param file
     * @param path
     * @param business
     * @param relief
     * @return
     */
    public static boolean modifyFileContent(String file, String path, String business, String relief) {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            String line = null;
            // 记住上一次的偏移量
            long lastPoint = 0;
            while ((line = raf.readLine()) != null) {
                line = new String(line.getBytes("ISO-8859-1"), "utf-8");
                // 文件当前偏移量
                final long ponit = raf.getFilePointer();
                // 查找要替换的内容
                if (line.contains("cl_dir")) {
                    Long tempPoint = lastPoint;
                    for (long i = lastPoint; i < ponit; i++) {
                        raf.seek(tempPoint);
                        raf.write(" ".getBytes());
                        tempPoint = raf.getFilePointer();
                    }
                    raf.seek(lastPoint);
                    raf.write(path.getBytes("utf-8"));
                    raf.seek((ponit >= raf.getFilePointer()) ? ponit : raf.getFilePointer());
                    raf.write("\n".getBytes());
                }
                if (line.contains("business")) {
                    Long tempPoint = lastPoint;
                    for (long i = lastPoint; i < ponit; i++) {
                        raf.seek(tempPoint);
                        raf.write(" ".getBytes());
                        tempPoint = raf.getFilePointer();
                    }
                    raf.seek(lastPoint);
                    raf.write(business.getBytes("utf-8"));
                    raf.seek((ponit >= raf.getFilePointer()) ? ponit : raf.getFilePointer());
                    raf.write("\n".getBytes());
                }
                if (line.contains("relief")) {
                    Long tempPoint = lastPoint;
                    for (long i = lastPoint; i < ponit; i++) {
                        raf.seek(tempPoint);
                        raf.write(" ".getBytes());
                        tempPoint = raf.getFilePointer();
                    }
                    raf.seek(lastPoint);
                    raf.write(relief.getBytes("utf-8"));
                    raf.seek((ponit >= raf.getFilePointer()) ? ponit : raf.getFilePointer());
                    raf.write("\n".getBytes());
                }
//                lastPoint = ponit;
                lastPoint = raf.getFilePointer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    public static void replacerConf(String filePath, String path, String business, String relief, String analyseTime, String config) {
        creatDir(path);
        File file = new File(filePath);
        Long fileLength = file.length();
        FileInputStream in = null;
        byte[] fileContext = new byte[fileLength.intValue()];
        PrintWriter out = null;
        String oldDir = null;
        String oldBusiness = null;
        String oldRelief = null;
        String oldAnalyseTime = null;
        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;

        //读取修改之前的配置
        try {
            fileInputStream = new FileInputStream(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                if (line.contains("cl_dir")) {
                    oldDir = line;
                }
                if (line.contains("business")) {
                    oldBusiness = line;
                }
                if (line.contains("relief")) {
                    oldRelief = line;
                }
                //analyseTime
                if (line.contains("analyseTime")) {
                    oldAnalyseTime = line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //替换新配置
        try {
            in = new FileInputStream(filePath);
            in.read(fileContext);
            // 避免出现中文乱码
            String str = new String(fileContext, "utf-8");
            if (oldDir != null) {
                str = str.replace(oldDir, path);
            } else {
                str += path + "\n";
            }
            if (oldBusiness != null) {
                str = str.replace(oldBusiness, business);
            } else {
                str += business + "\n";
            }
            if (oldRelief != null) {
                str = str.replace(oldRelief, relief);
            } else {
                str += relief + "\n";
            }
            if (oldAnalyseTime != null) {
                str.replace(oldAnalyseTime, analyseTime);
            } else {
                str += analyseTime + "\n";
            }
            path = path.split("=")[1];
            creatDir(path);
            String configPath = path + config;
            File file1 = new File(configPath);
            if (file1.exists()) {
                file1.delete();
            }

            out = new PrintWriter(configPath);
            out.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.flush();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static void replacerConf(String filePath, String path, String business, String relief) {
        File file = new File(filePath);
        Long fileLength = file.length();
        FileInputStream in = null;
        byte[] fileContext = new byte[fileLength.intValue()];
        PrintWriter out = null;
        String oldDir = null;
        String oldBusiness = null;
        String oldRelief = null;

        FileInputStream fileInputStream = null;
        BufferedReader bufferedReader = null;

        //读取修改之前的配置
        try {
            fileInputStream = new FileInputStream(filePath);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(), "utf-8");
                if (line.contains("cl_dir")) {
                    oldDir = line;
                }
                if (line.contains("business")) {
                    oldBusiness = line;
                }
                if (line.contains("relief")) {
                    oldRelief = line;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //替换新配置
        try {
            in = new FileInputStream(filePath);
            in.read(fileContext);
            // 避免出现中文乱码
            String str = new String(fileContext, "utf-8");
            str = str.replace(oldDir, path);
            str = str.replace(oldBusiness, business);
            str = str.replace(oldRelief, relief);
            out = new PrintWriter(filePath);
            out.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static int getDirectSize(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            return file.listFiles().length;
        }
        return 0;
    }


    /**
     * 此处不管文件大小，固定切割成5份
     */
    public static List<File> cutFile(File orginalFile) {
        //  Pattern queryTimePatter = Pattern.compile("([01]?\\d|2[0-3]):[0-5]?\\d[0-5]?\\d");
        ArrayList<File> cutFileList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        // SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            long orginalFileLength = orginalFile.length();
            String orginalFileName = orginalFile.getName();
            String targetDirectPath = orginalFile.getParentFile().getPath() + File.separator + orginalFileName + ".cut";
            long oneFileLength = orginalFileLength / 5;
            if (!orginalFile.exists()) {
                logging.error("原始文件不存在");
                return null;
            }
            File targetDirect = new File(targetDirectPath);
            if (!targetDirect.exists()) {
                targetDirect.mkdirs();
            }
            //设置切割分数
            for (int i = 0; i < 1; i++) {
                cutFileList.add(new File(targetDirectPath + File.separator + orginalFileName + "_" + i));
            }
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(orginalFile)));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cutFileList.get(0))));
            String line = "";
            int length = 0;
            int i = 1;
            while ((line = bufferedReader.readLine()) != null) {
                length += line.getBytes().length;
                if (length > oneFileLength) {
                    if (!line.startsWith("\tat ") && !line.contains("[ERROR]")) {
                      /*  Matcher timeMatcher = queryTimePatter.matcher(line);
                        if (!timeMatcher.find()) {*/
                        if (cutFileList.size() != 1) {
                            File file = cutFileList.get(i);
                            i++;
                            length = 0;
                            closeStream(bufferedWriter);
                            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                        }

                        //     }
                    }
                }
                bufferedWriter.write(line + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bufferedWriter);
            closeStream(bufferedReader);
        }
        return cutFileList;
    }


    public static void main(String[] args) {
        // List<File> files = cutFile(new File("D:\\test\\新建文件夹 (2)\\cl.log"), 10 * 1024 * 1024);
        ArrayList<File> files = new ArrayList<>();
        //findAllSizeMore(new File("D:\\test\\新建文件夹 (2)"), 5 * 1024 * 1024);

       /* getSizeLLesser(new File("D:\\test\\新建文件夹 (2)"), 6 * 1024 * 1024, files);
        System.out.println(files);*/

        deleteAllDirect(new File("D:\\test\\新建文件夹 (2)"));

    }

    public static List<File> cutFile(File orginalFile, long oneFileLength) {
        ArrayList<File> cutFileList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            String orginalFileName = orginalFile.getName();
            String targetDirectPath = orginalFile.getParentFile().getPath();
            String rootPath = orginalFile.getParentFile().getPath();
            long orginalFilelength = orginalFile.length();
            if (!orginalFile.exists()) {
                logging.error("原始文件不存在");
                return null;
            }
            File targetDirect = new File(targetDirectPath);
            if (!targetDirect.exists()) {
                targetDirect.mkdirs();
            }
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(orginalFile)));
            String line = "";
            int length = 0;
            long lenth2 = 0;
            int i = 0;

            creatDir(rootPath + File.separator + orginalFileName + "_cut");
            cutFileList.add(new File(rootPath + File.separator + orginalFileName + "_cut" + File.separator + orginalFileName + "." + (i + 1)));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cutFileList.get(0))));
            while ((line = bufferedReader.readLine()) != null) {
                length += line.getBytes().length;
                lenth2 += line.getBytes().length;
                if (length > oneFileLength && orginalFilelength - lenth2 > getByteSize(1)) {
                    if (!line.startsWith("\tat ") && !line.contains("[ERROR]")) {
                        i++;
                        cutFileList.add(new File(rootPath + File.separator + orginalFileName + "_cut" + File.separator + orginalFileName + "." + (i + 1)));
                        File file = cutFileList.get(i);
                        closeStream(bufferedWriter);
                        bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                        length = 0;
                    }
                }
                bufferedWriter.write(line + "\r\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeStream(bufferedWriter);
            closeStream(bufferedReader);
        }
        return cutFileList;
    }

    public static void deleteDirect(String path) {
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirect(file.getPath());
                }
                deleteFile(file.getParentFile().getPath(), file.getName());
            }
            rootFile.delete();
        } else {
            rootFile.delete();
        }
    }

    public static void deleteEmptyDirect(File rootFile, String path) {

        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            if (files.length == 0) {
                rootFile.delete();
                if (rootFile.getParentFile().getPath() != path) {
                    deleteEmptyDirect(rootFile.getParentFile(), path);
                }
            } else {
                for (File file : files) {
                    deleteEmptyDirect(file, path);
                }
            }
        } else {
            return;
        }
    }


    public static void closeStream(Object stream) {
        if (stream != null && stream instanceof BufferedReader) {
            BufferedReader bufferedReader = (BufferedReader) stream;
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (stream != null && stream instanceof BufferedWriter) {
            BufferedWriter bufferedWriter = (BufferedWriter) stream;
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void findAllSizeMore(File rootFile, long size) {
        if (rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (file.length() > size) {
                    cutFile(file, size);
                }
            }
        }
    }

    //获取文件夹下所有小于 size大小的日志文件
    public static void getSizeLLesser(File rootFile, long size, List<File> fileList) {
        size += FileUtil.getByteSize(1);
        // ArrayList<File> newfiles = new ArrayList<>();
        if (rootFile.exists()) {
            if (rootFile.isDirectory()) {
                File[] files = rootFile.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        getSizeLLesser(file, size, fileList);
                    } else {
                        if (file.getName().contains("cl.log") && file.length() < size) {
                            fileList.add(file);
                        }
                    }
                }
            }
        }
    }

    public static void deleteAllDirect(File rootFile) {
        if (rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirect(file.getPath());
                }
            }
        }
    }

    public static long getByteSize(int sizeMB) {
        return (long) (sizeMB * 1024 * 1024);
    }


    public static boolean ObjectOutputStreamDisk(Object object, String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            ObjectOutputStream ops = new ObjectOutputStream(fos);
            ops.writeObject(object);
            fos.close();
            ops.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Object ObjectInputStreamDisk(String filePath) {
        try {
            FileInputStream fos = new FileInputStream(filePath);
            ObjectInputStream ops = new ObjectInputStream(fos);
            Object o = ops.readObject();
            fos.close();
            ops.close();
            return o;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 脚本下载
     */
    static boolean isSql = false;

    public static Response uploadSql(MultipartFile file, String uploadPath) {
        try {
            isSql = isLog(file);
        } catch (Exception e) {
            e.printStackTrace();
            logging.error("上传失败", e);
            return Response.error("文件上传失败！");
        }
        if (!isSql) {
            return Response.error("文件不是一个.sql格式！");
        }
        System.out.println("文件大小----------:" + file.getSize());
        String fileName = file.getOriginalFilename();

        String currentdate = String.valueOf(System.currentTimeMillis());
//        String path  = uploadTablePath + File.separator  + "uploadCreateTable" + currentdate;
        String path = uploadPath + File.separator + currentdate;
        File director = new File(path);
        if (!director.exists()) {
            director.mkdir();
        }
        try {
            file.transferTo(new File(director.getPath() + File.separator + fileName));
            logging.info(String.format("文件上传至：%s", path));
        } catch (IOException e) {
            e.printStackTrace();
            return Response.error("文件上传失败！");
        }
        Map<String, Object> retMap = new HashMap<>();
        retMap.put("fileName", fileName);
        retMap.put("path", path);
        return Response.ok(retMap);
    }

    private static boolean isLog(MultipartFile file) {
        if (file.isEmpty()) {
            logging.info("上传文件为空！");
            return false;
        }
        if (file.getOriginalFilename().toLowerCase().indexOf(".sql") >= 0) {
            return true;
        }
        return false;
    }

    /*文件下载*/
    public static void downloadFile(HttpServletResponse response, String fileName, String path) {
        if (fileName != null) {
            //设置文件路径
            File file = new File(path);
            if (file.exists()) {
                response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                try {
                    response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("utf-8"), "ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                byte[] buffer = new byte[1024];
                FileInputStream fis = null;
                BufferedInputStream bis = null;
                try {
                    file = new File(path + File.separator + fileName);
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    OutputStream os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
