package com.lease.framework.persistence.database;

import com.lease.framework.persistence.database.annotation.Id;

import java.io.Serializable;

/**
 *表的基类，存放表共用字段
 * Created by caishuxing on 2015/7/3.
 */
public class BaseDO implements Serializable{
    /**数据库用的主键**/
    @Id(column="columnId")
    public int columnId=0;

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

}
