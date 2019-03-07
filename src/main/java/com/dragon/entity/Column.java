package com.dragon.entity;

/**
 * 列的属性
 */
public class Column {
    // 列名
    private String columnName;
    // 列名类型
    private String dataType;
    // 列名备注
    private String comments;
    // auto_increment
    private String extra;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

//    //属性名称(第一个字母大写)，如：user_name => UserName
//    private String attrName;
//    //属性名称(第一个字母小写)，如：user_name => userName
//    private String attrname;
//    //属性类型
//    private String attrType;


}
