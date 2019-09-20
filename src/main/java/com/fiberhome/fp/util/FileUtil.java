package com.fiberhome.fp.util;

import com.fiberhome.fp.listener.event.AnalyseProcess;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author fengxiaochun
 * @date 2019/7/5
 */
public class FileUtil {

    static Logger logging = LoggerFactory.getLogger(ShellUtil.class);

    private static String encodedType = "utf-8";
    private static String cldir = "cl_dir";
    private static String cutRex = "_cut";
    private static int MBsize = 1024;


    public static void creatDir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private FileUtil() {

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
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path + File.separator + fileName));) {
            bw.write(content);
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        }
    }

    public static boolean isFileExixts(String filePath) {
        File file = new File(filePath);
        return file.exists() ? true : false;
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
            filesDelete(file);
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
        try (RandomAccessFile raf = new RandomAccessFile(file, "rw");) {
            String line = null;
            // 记住上一次的偏移量
            long lastPoint = 0;
            while ((line = raf.readLine()) != null) {
                line = new String(line.getBytes("ISO-8859-1"), encodedType);
                // 文件当前偏移量
                final long ponit = raf.getFilePointer();
                // 查找要替换的内容
                if (line.contains(cldir)) {
                    Long tempPoint = lastPoint;
                    for (long i = lastPoint; i < ponit; i++) {
                        raf.seek(tempPoint);
                        raf.write(" ".getBytes());
                        tempPoint = raf.getFilePointer();
                    }
                    raf.seek(lastPoint);
                    raf.write(path.getBytes(encodedType));
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
                    raf.write(business.getBytes(encodedType));
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
                    raf.write(relief.getBytes(encodedType));
                    raf.seek((ponit >= raf.getFilePointer()) ? ponit : raf.getFilePointer());
                    raf.write("\n".getBytes());
                }
                lastPoint = raf.getFilePointer();
            }
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
        }
        return true;
    }


    public static void replacerConf(String filePath, String path, String business, String relief) {
        File file = new File(filePath);
        Long fileLength = file.length();
        byte[] fileContext = new byte[fileLength.intValue()];
        PrintWriter out = null;
        String oldDir = null;
        String oldBusiness = null;
        String oldRelief = null;
        //读取修改之前的配置
        try (FileInputStream fileInputStream = new FileInputStream(filePath);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));) {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = new String(line.getBytes(), encodedType);
                if (line.contains(cldir)) {
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
            logging.error(e.getMessage(), e);
        }


        //替换新配置
        try (FileInputStream in = new FileInputStream(filePath);) {
            int count = 0;
            if ((count = in.read(fileContext)) > 0) {
                // 避免出现中文乱码
                String str = new String(fileContext, encodedType);
                str = str.replace(oldDir, path);
                str = str.replace(oldBusiness, business);
                str = str.replace(oldRelief, relief);
                out = new PrintWriter(filePath);
                out.write(str);
            }
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception e) {
                logging.error(e.getMessage(), e);
            }
        }

    }

    public static List<File> cutFile(File orginalFile, String uuid) {
        AnalyseProcess analyseProcess = AnalyseProcess.map.get(uuid);
        Long oneFileLength = analyseProcess.getCutfilesize();
        Integer cutFileMaxCount = analyseProcess.getCutFileMaxCount();
        ArrayList<File> cutFileList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        String orginalFileName = orginalFile.getName();
        String targetDirectPath = orginalFile.getParentFile().getPath();
        String rootPath = orginalFile.getParentFile().getPath();
        if (!orginalFile.exists()) {
            logging.error("原始文件不存在");
            return Collections.emptyList();
        }
        long orginalFilelength = orginalFile.length();
        if (orginalFilelength / oneFileLength > cutFileMaxCount) {
            oneFileLength = orginalFilelength / cutFileMaxCount;
            analyseProcess.setCutfilesize(oneFileLength);
        }
        FileUtil.creatDir(targetDirectPath);
        String line = "";
        int length = 0;
        long lenth2 = 0;
        int i = 0;
        String cutRootPath = rootPath + File.separator + orginalFileName + cutRex;
        creatDir(cutRootPath);
        cutFileList.add(new File(rootPath + File.separator + orginalFileName + cutRex + File.separator + orginalFileName + "." + (i + 1)));
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(orginalFile)));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cutFileList.get(0))));
            while ((line = bufferedReader.readLine()) != null) {
                length += line.getBytes().length;
                lenth2 += line.getBytes().length;
                if (length > oneFileLength && orginalFilelength - lenth2 > getByteSize(1) && !line.startsWith("\tat ") && !line.contains("[ERROR]")) {
                    i++;
                    cutFileList.add(new File(rootPath + File.separator + orginalFileName + cutRex + File.separator + orginalFileName + "." + (i + 1)));
                    File file = cutFileList.get(i);
                    closeStream(bufferedWriter);
                    bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
                    length = 0;
                }
                bufferedWriter.write(line + "\n");
            }
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        } finally {
            closeStream(bufferedWriter);
            closeStream(bufferedReader);
        }
        return cutFileList;
    }

    public static void deleteDirect(File rootFile) {
        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirect(file);
                }
                filesDelete(file);
            }
            filesDelete(rootFile);
        } else {
            filesDelete(rootFile);
        }
    }

    public static void filesDelete(File file) {
        try {
            Files.deleteIfExists(file.toPath());
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        }
    }
    public static void filesDelete(String path) {
        try {
            Files.deleteIfExists(Paths.get(path));
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        }
    }

    public static void deleteEmptyDirect(File rootFile, String path) {

        if (!rootFile.exists()) {
            return;
        }
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            if (files.length == 0) {
                filesDelete(rootFile);
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
        if (stream != null) {
            if (stream instanceof Reader) {
                Reader reader = (Reader) stream;
                try {
                    reader.close();
                } catch (IOException e) {
                    logging.error(e.getMessage(), e);
                }
                return;
            } else if (stream instanceof InputStream) {
                InputStream is = (InputStream) stream;
                try {
                    is.close();
                } catch (IOException e) {
                    logging.error(e.getMessage(), e);
                }
                return;
            } else if (stream instanceof OutputStream) {
                OutputStream os = (OutputStream) stream;
                try {
                    os.close();
                } catch (IOException e) {
                    logging.error(e.getMessage(), e);
                }
                return;
            } else if (stream instanceof Writer) {
                Writer writer = (Writer) stream;
                try {
                    writer.close();
                } catch (IOException e) {
                    logging.error(e.getMessage(), e);
                }
                return;
            } else {
                try {
                    throw new IOException("流无法关闭");
                } catch (Exception e) {
                    logging.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void findAllSizeMore(File rootFile, String uuid) {
        Long size = AnalyseProcess.map.get(uuid).getCutfilesize();
        long size1 = FileUtil.getByteSize(1) + size;
        if (rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            ArrayList<String> dirNameList = new ArrayList<>();
            ArrayList<File> fileList = new ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    dirNameList.add(file.getName().replace(cutRex, ""));
                } else {
                    fileList.add(file);
                }
            }
            fileListLengthSort(fileList);
            for (File file : fileList) {
                if (file.isFile() && file.length() > size1 && !dirNameList.contains(file.getName())) {
                    List<File> cutFile = cutFile(file, uuid);
                    logging.info("{}日志文件{}MB,执行切割成{}份,单个文件最大{}MB,异步下发分析", file.getName(), file.length() / MBsize / MBsize, cutFile.size(), cutFile.get(0).length() / MBsize / MBsize);
                }
            }
        }
    }
    public static void fileListLengthSort(List<File> files) {
        Collections.sort(files, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return (int) (o2.length() - o1.length());
            }
        });
    }

    //获取文件夹下所有小于 size大小的日志文件
    public static void getSizeLLesser(File rootFile, List<File> fileList, String uuid) {
        AnalyseProcess analyseProcess = AnalyseProcess.map.get(uuid);
        long size = analyseProcess.getCutfilesize();
        size += FileUtil.getByteSize(1);
        if (rootFile.exists() && rootFile.isDirectory()) {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    getSizeLLesser(file, fileList, uuid);
                } else {
                    if (file.getName().contains("cl.log") && file.length() < size) {
                        fileList.add(file);
                    }
                }
            }
        }
    }


    public static void deleteRootPathDir(File rootFile, String rex) {
        File[] files = rootFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && file.getName().contains(rex)) {
                    deleteDirect(file);
                }
            }
        }
    }

    public static long getByteSize(int sizeMB) {
        return (long) (sizeMB * MBsize * MBsize);
    }


    public static boolean objectOutputStreamDisk(Object object, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             ObjectOutputStream ops = new ObjectOutputStream(fos);) {
            ops.writeObject(object);
            return true;
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
        }
        return false;
    }

    public static Object objectInputStreamDisk(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ops = new ObjectInputStream(fis);) {
            return ops.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logging.error(e.getMessage(), e);
            throw new IOException();
        }
    }

    /**
     * 脚本下载
     */
    static boolean isSql = false;

    public static Response uploadSql(MultipartFile file, String uploadPath) {
        try {
            isSql = isLog(file);
        } catch (Exception e) {
            logging.error(e.getMessage(), e);
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
            logging.info("文件上传至：{}", path);
        } catch (IOException e) {
            logging.error(e.getMessage(), e);
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
                    response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(encodedType), "ISO-8859-1"));
                } catch (UnsupportedEncodingException e) {
                    logging.error(e.getMessage(), e);
                }
                byte[] buffer = new byte[MBsize];
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
                    logging.error(e.getMessage(), e);
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            logging.error(e.getMessage(), e);
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            logging.error(e.getMessage(), e);
                        }
                    }
                }
            }
        }
    }
}
