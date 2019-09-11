package com.fiberhome.fp.pojo;

import com.fiberhome.fp.util.TimeUtil;
import com.fiberhome.fp.vo.SqlCount;
import com.fiberhome.fp.vo.TagProporation;
import com.fiberhome.fp.vo.ErrorSqlCount;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * @author fengxiaochun
 * @date 2019/7/2
 */
@ApiModel(value = "AllResult",description = "所有返回结果")
public class AllResult {
    @ApiModelProperty(value = "时间",name = "data",dataType = "int")
    private int date;

    private Integer time;

    private String tag;

    private String sqlResult;

    private String pjName;

    private String  pjLocation;

    private String partition;

    //#############################

    private int qualifiedSql;

    private int unqualifiedSql;

    private Integer count;

    private List<TagProporation> tagProporationsList;

    private List<SqlCount> sqlCountList;

    private List<ErrorSqlCount> errorSqlCountList;

    private String timeTag;


    //查询时长
    @ApiModelProperty(value = "查询时长",name = "duration",dataType = "String")
    private String duration;

    private List<String> pjNameList;

    private List<String> pjLocationList;

    private List<String> tagList;

    private String tagDescribe;

    //日期格式化显示字段
    @ApiModelProperty(value = "日期格式化显示字段",name = "dateStr",dataType = "String")
    private String dateStr;


    //业务分析详情查询时间范围参数
    private String searchTime;
    //查询结束时间
    private String endTime;
    // 查询开始时间
    private String startTime;

    //关键字查询
    private String keyWord;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public AllResult(Integer time, String sqlResult, String pjLocation, String tagDescribe, String endTime, String startTime) {
        this.time = time;
        this.sqlResult = sqlResult;
        this.pjLocation = pjLocation;
        this.tagDescribe = tagDescribe;
        this.endTime = endTime;
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public AllResult() {
    }
//String sqlResult,String pjLocaation,String tagDescribe,String time,String startTime,String endTime


    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
        this.dateStr = TimeUtil.long2String(date,"yyyy-MM-dd HH:mm:ss");
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        if (StringUtils.isNotEmpty(tag)){
            switch (tag) {
                case "easy": this.tagDescribe = "简单语句";
                    break;
                case "comp": this.tagDescribe = "复杂语句";
                    break;
                case "insert": this.tagDescribe = "导入导出语句";
                    break;
                case "else": this.tagDescribe = "其他语句";
                    break;
            }
        }

    }

    public String getSqlResult() {
        return sqlResult;
    }

    public void setSqlResult(String sqlResult) {
        this.sqlResult = sqlResult;
    }

    public String getPjName() {
        return pjName;
    }

    public void setPjName(String pjName) {
        this.pjName = pjName;
    }

    public String getPjLocation() {
        return pjLocation;
    }

    public void setPjLocation(String pjLocation) {
        this.pjLocation = pjLocation;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }

    public int getQualifiedSql() {
        return qualifiedSql;
    }

    public void setQualifiedSql(int qualifiedSql) {
        this.qualifiedSql = qualifiedSql;
    }

    public int getUnqualifiedSql() {
        return unqualifiedSql;
    }

    public void setUnqualifiedSql(int unqualifiedSql) {
        this.unqualifiedSql = unqualifiedSql;
    }

    public List<TagProporation> getTagProporationsList() {
        return tagProporationsList;
    }

    public void setTagProporationsList(List<TagProporation> tagProporationsList) {
        this.tagProporationsList = tagProporationsList;
    }

    public List<SqlCount> getSqlCountList() {
        return sqlCountList;
    }

    public void setSqlCountList(List<SqlCount> sqlCountList) {
        this.sqlCountList = sqlCountList;
    }

    public List<ErrorSqlCount> getErrorSqlCountList() {
        return errorSqlCountList;
    }

    public void setErrorSqlCountList(List<ErrorSqlCount> errorSqlCountList) {
        this.errorSqlCountList = errorSqlCountList;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getTimeTag() {
        return timeTag;
    }

    public void setTimeTag(String timeTag) {
        this.timeTag = timeTag;
    }

    public List<String> getPjNameList() {
        return pjNameList;
    }

    public void setPjNameList(List<String> pjNameList) {
        this.pjNameList = pjNameList;
    }

    public List<String> getPjLocationList() {
        return pjLocationList;
    }

    public void setPjLocationList(List<String> pjLocationList) {
        this.pjLocationList = pjLocationList;
    }

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }


    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTagDescribe() {
        return tagDescribe;
    }

    public void setTagDescribe(String tagDescribe) {
        this.tagDescribe = tagDescribe;
    }

    public String getDateStr() {
        return dateStr;
    }

    public void setDateStr(String dateStr) {
        this.dateStr = dateStr;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

}
