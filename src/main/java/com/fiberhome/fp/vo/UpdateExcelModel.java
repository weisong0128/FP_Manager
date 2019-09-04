package com.fiberhome.fp.vo;

public class UpdateExcelModel {

    //文件名
    private String fileName;

    //sheet页
    private int sheetNum;

    //错误类型（T:添加新字段类型  E:表格校验内容）
    private String updateType;

    //行号
    private int row;

    //列号
    private int col;

    //修改内容
    private String content;

    //文件路径
    private String path;

    //原始数据类型
    private String oldType;

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

    public String getUpdateType() {
        return updateType;
    }

    public void setUpdateType(String updateType) {
        this.updateType = updateType;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
