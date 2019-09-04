package com.fiberhome.fp.pojo;

/**
 * @author fengxiaochun
 * @date 2019/7/2
 */
public class FpHelp {

    private String errCode;

    private String errKeyWord;

    private String errReason;

    private String solution;

    private Integer count;

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrKeyWord() {
        return errKeyWord;
    }

    public void setErrKeyWord(String errKeyWord) {
        this.errKeyWord = errKeyWord;
    }

    public String getErrReason() {
        return errReason;
    }

    public void setErrReason(String errReason) {
        this.errReason = errReason;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
