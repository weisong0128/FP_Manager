package com.fiberhome.fp.createtable;

import com.fiberhome.fp.vo.CheckExcelModel;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CheckExcel {
    /**
     * 校验导入的excel是否合格
     * @param execl_file  导入excel
     * @param file2 原始类型表
     * @throws IOException
     */

    public static final int TWO=2;
    public static final int FOUR=4;
    public static final int TEN=10;

    public static List<CheckExcelModel> CheckExecl(File execl_file, File file2) throws IOException {
        List<CheckExcelModel>  resultList = new ArrayList<>();
        CheckExcelModel checkExcelModel = new CheckExcelModel();
        checkExcelModel.setSuccesss(true);
        InputStream execl_file1 = new FileInputStream(execl_file.getAbsolutePath());
        InputStream fieldfilename = new FileInputStream(file2.getAbsoluteFile());
        try {
            Workbook check = Workbook.getWorkbook(execl_file1);
            Workbook field = Workbook.getWorkbook(fieldfilename);
            Sheet fieldsheet = field.getSheet(0);
            List<String> olddatatype = new ArrayList<>();
            List<String> newdatatype = new ArrayList<>();
            for (int fieldrow = 1; fieldrow < fieldsheet.getRows(); fieldrow++) {
                olddatatype.add(fieldsheet.getCell(0, fieldrow).getContents().trim());
                newdatatype.add(fieldsheet.getCell(1, fieldrow).getContents().trim());
            }
            int checknum = check.getNumberOfSheets();
            for (int sheetcheck = 0; sheetcheck < checknum; sheetcheck++) {
                Sheet chechsheet = check.getSheet(sheetcheck);
                if (chechsheet.getRows() == 0){
                    continue;
                }
                String tablename = chechsheet.getCell(1, 0).getContents().trim();
                String tabletype = chechsheet.getCell(1, 1).getContents().trim();
                String physicaltable = chechsheet.getCell(1,TWO).getContents().trim();
                if (StringUtils.isEmpty(tablename)){
                    checkExcelModel = new CheckExcelModel();
                    checkExcelModel.setFileName(execl_file.getName());
                    checkExcelModel.setSheetNum(sheetcheck);
                    checkExcelModel.setErrType(TWO);
                    checkExcelModel.setRow(0);
                    checkExcelModel.setCol(1);
                    checkExcelModel.setMsg("表名不能为空！");
                    // msgMap.put("tableName","表名不能为空!");
                    checkExcelModel.setSuccesss(false);
                    resultList.add(checkExcelModel);
                }
                if (StringUtils.isEmpty(tabletype)){
                    checkExcelModel = new CheckExcelModel();
                    checkExcelModel.setFileName(execl_file.getName());
                    checkExcelModel.setSheetNum(sheetcheck);
                    checkExcelModel.setErrType(TWO);
                    checkExcelModel.setRow(2);
                    checkExcelModel.setCol(1);
                    checkExcelModel.setMsg("建表类型不能为空！");
                    // msgMap.put("tableType","建表类型不能为空！");
                    checkExcelModel.setSuccesss(false);
                    resultList.add(checkExcelModel);
                }
                if (!("物理表".equals(tabletype) || "映射表".equals(tabletype))){
                    checkExcelModel = new CheckExcelModel();
                    checkExcelModel.setFileName(execl_file.getName());
                    checkExcelModel.setSheetNum(sheetcheck);
                    checkExcelModel.setErrType(TWO);
                    checkExcelModel.setRow(1);
                    checkExcelModel.setCol(1);
                    checkExcelModel.setMsg("建表类型必须是物理表或者映射表！");
                    //msgMap.put("createTableType","建表类型必须是物理表或者映射表！");
                    checkExcelModel.setSuccesss(false);
                    resultList.add(checkExcelModel);
                }
                if ("映射表".equals(tabletype) && StringUtils.isEmpty(physicaltable)){
                    checkExcelModel = new CheckExcelModel();
                    checkExcelModel.setFileName(execl_file.getName());
                    checkExcelModel.setSheetNum(sheetcheck);
                    checkExcelModel.setErrType(TWO);
                    checkExcelModel.setRow(TWO);
                    checkExcelModel.setCol(1);
                    checkExcelModel.setMsg("创建映射表时，对应的物理表名不能为空！");
                    //msgMap.put("mappingTable","创建映射表时，对应的物理表名不能为空！");
                    checkExcelModel.setSuccesss(false);
                    resultList.add(checkExcelModel);
                }
                for (int i = 0; i < chechsheet.getRows() - TEN; i++) {
                    String fieldname = chechsheet.getCell(0, i + FOUR).getContents().trim().toUpperCase();
                    String fieldtype = chechsheet.getCell(1, i + FOUR).getContents().trim().toUpperCase();
                    if (StringUtils.isNotEmpty(fieldname) && StringUtils.isEmpty(fieldtype)) {
                        checkExcelModel = new CheckExcelModel();
                        /*System.out.println("字段车型不能为空");
                        System.out.println("文件名："+execl_file.getName()+" ;sheet页："+sheetcheck +" ;错误类型：2；行号："+(i+4)+"；列号："+1+"；字段名："+fieldname+"；类型为空");*/
                        checkExcelModel.setFileName(execl_file.getName());
                        checkExcelModel.setSheetNum(sheetcheck);
                        checkExcelModel.setErrType(TWO);
                        checkExcelModel.setRow(i+FOUR);
                        checkExcelModel.setCol(1);
                        checkExcelModel.setFieldName(fieldname);
                        checkExcelModel.setMsg(tablename+"表"+fieldname+"字段类型不能为空！");
                        //msgMap.put("fieldType",tablename+"表"+fieldname+"字段类型不能为空！");
                        checkExcelModel.setSuccesss(false);
                        resultList.add(checkExcelModel);
                    }
                    if (StringUtils.isNotEmpty(fieldtype)){
                        String type =fieldtype.trim().toLowerCase();
                        if (fieldtype.contains("(")){
                            type = fieldtype.substring(0, fieldtype.indexOf("(")).toLowerCase();
                        }
                        if (!olddatatype.contains(type)){
                            checkExcelModel = new CheckExcelModel();
                         /*   System.out.println("原始类型不存在type="+type);
                            System.out.println("文件名："+execl_file.getName()+" ;sheet页："+sheetcheck +" ;错误类型：1；行号："+fieldsheet.getRows()+"；列号："+0+"；类型："+type+"不存在");*/
                            checkExcelModel.setFileName(execl_file.getName());
                            checkExcelModel.setSheetNum(sheetcheck);
                            checkExcelModel.setErrType(1);
                            checkExcelModel.setRow(fieldsheet.getRows());
                            checkExcelModel.setCol(0);
                            checkExcelModel.setFieldName(fieldname);
                            checkExcelModel.setOldType(type);
                            checkExcelModel.setMsg(tablename+"表"+fieldname+"字段类型原始类型不存在，请添加！");
                            //msgMap.put("fieldOriginalType",tablename+"表"+fieldname+"字段类型原始类型不存在，请添加！");
                            checkExcelModel.setSuccesss(false);
                            resultList.add(checkExcelModel);
                        }
                    }
                }

            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
        finally {
            execl_file1.close();
            fieldfilename.close();
        }
        if (StringUtils.isEmpty(checkExcelModel.getFileName())){
            checkExcelModel.setFileName(execl_file.getName());
        }
        return resultList;
    }
}
