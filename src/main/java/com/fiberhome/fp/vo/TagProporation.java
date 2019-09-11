package com.fiberhome.fp.vo;

import org.apache.commons.lang.StringUtils;

/**
 * sql类型比例
 * @author fengxiaochun
 * @date 2019/7/3
 */
public class TagProporation {

    //类型
    private String tag;

    //数量
    private double count;

    //统计图sql类型占比
    private String name;
    //统计图sql类型占比
    private String value;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
        if (StringUtils.isNotBlank(tag)){
            switch (tag) {
                case "easy": this.name = "简单语句";
                    break;
                case "comp": this.name = "复杂语句";
                    break;
                case "insert": this.name = "导入导出语句";
                    break;
                case "else": this.name = "其他语句";
                    break;
                default:
            }
        }
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
