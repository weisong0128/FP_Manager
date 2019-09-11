package com.fiberhome.fp.util;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Objects;

/**
 * @author fengxiaochun
 * @date 2019/7/6
 */
public class ExcelUtil {

    private  static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    /* 给excel指定位置写入值
     * @param path       写入文件在路径
     * @param coordinate 写入内容的位置（例如:B4）
     * @param value      写的值
     */
    public static void writeSpecifiedCell(String path,int sheetNum, String coordinate, String value) {
        //根据路径获取文件
        File file = new File(path);
        //定义输入流对象
        FileInputStream excelFileInputStream;
        Workbook workbook=null;
        try {
            excelFileInputStream = new FileInputStream(file);
            // 拿到文件转化为JavaPoi可操纵类型
            workbook = WorkbookFactory.create(excelFileInputStream);
            excelFileInputStream.close();
            ////获取excel表格
            Sheet sheet = workbook.getSheetAt(sheetNum);
            //获取单元格的row和cell
            CellAddress address = new CellAddress(coordinate);
            // 获取行
            Row row = sheet.getRow(address.getRow());
            // 获取列
//            Cell cell = row.getCell(address.getColumn());
            Cell cell = row.createCell(address.getColumn());
            //设置单元的值
            cell.setCellValue(value);
            //写入数据
            FileOutputStream excelFileOutPutStream = new FileOutputStream(file);
            workbook.write(excelFileOutPutStream);
            excelFileOutPutStream.flush();
            excelFileOutPutStream.close();
            //System.out.println("指定单元格设置数据写入完成");
            logger.info("指定单元格设置数据写入完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }finally {
            if (workbook!=null){
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /* 给excel指定位置写入值
     * @param path       写入文件在路径
     * @param value      写的值
     */
    public static void writeSpecifiedCell(String fieldFile,int sheetNum, int rowNum ,int colNum, String value) {
        //根据路径获取文件
        File file = new File(fieldFile);
        //定义输入流对象
        FileInputStream excelFileInputStream;
        Workbook workbook =null;
        try {
            excelFileInputStream = new FileInputStream(file);
            // 拿到文件转化为JavaPoi可操纵类型
            workbook = WorkbookFactory.create(excelFileInputStream);

            ////获取excel表格
            Sheet sheet = workbook.getSheetAt(sheetNum);
            //获取单元格的row和cell
            CellAddress address = new CellAddress(rowNum,colNum);
            // 获取行
            Row row = sheet.getRow(address.getRow());
            if (row == null){
                row = sheet.createRow(address.getRow());
            }
            // 获取列
//            Cell cell = row.getCell(address.getColumn());
            Cell cell = row.createCell(address.getColumn());
            //设置单元的值
            cell.setCellValue(value);
            //写入数据
            FileOutputStream excelFileOutPutStream = new FileOutputStream(file);
            workbook.write(excelFileOutPutStream);
            excelFileInputStream.close();
            excelFileOutPutStream.flush();
            excelFileOutPutStream.close();
            //System.out.println("指定单元格设置数据写入完成");
            logger.info("指定单元格设置数据写入完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EncryptedDocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        }finally {
            if (workbook!=null){
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断是不是excel文件
     * @param inputStream
     * @return
     */
    public static boolean isExcel(InputStream inputStream){
        try {
            FileMagic fileMagic = FileMagic.valueOf(FileMagic.prepareToCheckMagic(inputStream));
            if (Objects.equals(fileMagic,FileMagic.OLE2)){
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }



    /**
     * 导出Excel
     * @param sheetName sheet名称
     * @param title 标题
     * @param values 内容
     * @param wb HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName,String []title,String [][]values, HSSFWorkbook wb){

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if(wb == null){
            wb = new HSSFWorkbook();
        }

        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        //style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for(int i=0;i<title.length;i++){
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for(int i=0;i<values.length;i++){
            row = sheet.createRow(i + 1);
            for(int j=0;j<values[i].length;j++){
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }

}
