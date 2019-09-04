package com.fiberhome.fp.vo;

import java.util.HashMap;
import java.util.Map;

public class CheckExcelModel {

    //文件名
    private String fileName;

    //sheet页
    private int sheetNum;

    //校验是否通过
    private boolean isSuccesss;

    //错误类型
    private int errType;

    //行号
    private int row;

    //列号
    private int col;

    //字段名（判断类型为空方式显示）
    private String fieldName;

    //类型名（原始类型不存在时显示）
    private String typeName;

    //错误信息
    private String msg;


    //文件路径
    private String path;

    //原始数据类型
    private String oldType;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSheetNum() {
        return sheetNum;
    }

    public void setSheetNum(int sheetNum) {
        this.sheetNum = sheetNum;
    }

    public int getErrType() {
        return errType;
    }

    public void setErrType(int errType) {
        this.errType = errType;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isSuccesss() {
        return isSuccesss;
    }

    public void setSuccesss(boolean successs) {
        isSuccesss = successs;
    }



    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getOldType() {
        return oldType;
    }

    public void setOldType(String oldType) {
        this.oldType = oldType;
    }
}
