package com.lease.framework.persistence.database.table;

import android.database.Cursor;

import com.lease.framework.persistence.database.converter.ColumnConverter;
import com.lease.framework.persistence.database.converter.ColumnConverterFactory;
import com.lease.framework.persistence.database.sqlite.ColumnDbType;
import com.lease.framework.core.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Column {

    private Table table;

    private int index = -1;

    protected final String columnName;
    private final Object defaultValue;

    protected final Method getMethod;
    protected final Method setMethod;

    protected final Field columnField;
    protected final ColumnConverter columnConverter;
    protected boolean isUnique = false;//是否是唯一键
    protected boolean multiUnique = false;//是否是 约束键

    /* package */ Column(Class<?> entityType, Field field) {
        this.columnField = field;
        this.columnConverter = ColumnConverterFactory.getColumnConverter(field.getType());
        this.columnName = ColumnUtils.getColumnNameByField(field);
        if (this.columnConverter != null) {
            this.defaultValue = this.columnConverter.getFieldValue(ColumnUtils.getColumnDefaultValue(field));
        } else {
            this.defaultValue = null;
        }
        this.getMethod = ColumnUtils.getColumnGetMethod(entityType, field);
        this.setMethod = ColumnUtils.getColumnSetMethod(entityType, field);
        this.isUnique = ColumnUtils.isUnique(field);
        this.multiUnique = ColumnUtils.isMultiUnique(field);
    }

    @SuppressWarnings("unchecked")
    public void setValue2Entity(Object entity, Cursor cursor, int index) {
        this.index = index;
        Object value = columnConverter.getFieldValue(cursor, index);
        if (value == null && defaultValue == null) return;

        if (setMethod != null) {
            try {
                setMethod.invoke(entity, value == null ? defaultValue : value);
            } catch (Throwable e2) {
                LogUtils.e(e2.getMessage());
                try {
                    this.columnField.setAccessible(true);
                    this.columnField.set(entity, value == null ? defaultValue : value);
                } catch (Throwable e) {
                    LogUtils.e(e.getMessage());
                }
            }
        } else {
            try {
                this.columnField.setAccessible(true);
                this.columnField.set(entity, value == null ? defaultValue : value);
            } catch (Throwable e) {
                LogUtils.e("com.xutils.db.table.Column", e.getMessage());
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Object getColumnValue(Object entity) {
        Object fieldValue = getFieldValue(entity);
        return columnConverter.fieldValue2ColumnValue(fieldValue);
    }

    public Object getFieldValue(Object entity) {
        Object fieldValue = null;
        if (entity != null) {
            if (getMethod != null) {
                try {
                    fieldValue = getMethod.invoke(entity);
                } catch (Throwable e) {
                    try {
                        this.columnField.setAccessible(true);
                        fieldValue = this.columnField.get(entity);
                    } catch (Throwable e2) {
                        LogUtils.e(e.getMessage());
                    }
                }
            } else {
                try {
                    this.columnField.setAccessible(true);
                    fieldValue = this.columnField.get(entity);
                } catch (Throwable e) {
                    LogUtils.e(e.getMessage());
                }
            }
        }
        return fieldValue;
    }

    public Table getTable() {
        return table;
    }

    /* package */ void setTable(Table table) {
        this.table = table;
    }

    /**
     * The value set in setValue2Entity(...)
     *
     * @return -1 or the index of this column.
     */
    public int getIndex() {
        return index;
    }

    public String getColumnName() {
        return columnName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Field getColumnField() {
        return columnField;
    }

    public ColumnConverter getColumnConverter() {
        return columnConverter;
    }

    public ColumnDbType getColumnDbType() {
        return columnConverter.getColumnDbType();
    }
}
