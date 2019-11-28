package com.lease.framework.persistence.database.converter;

import android.database.Cursor;

import com.lease.framework.persistence.database.sqlite.ColumnDbType;

public interface ColumnConverter<T> {

    T getFieldValue(final Cursor cursor, int index);

    T getFieldValue(String fieldStringValue);

    Object fieldValue2ColumnValue(T fieldValue);

    ColumnDbType getColumnDbType();
}
