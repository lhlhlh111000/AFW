

package com.lease.framework.persistence.database.table;

import android.text.TextUtils;

import com.lease.framework.persistence.database.annotation.Check;
import com.lease.framework.persistence.database.annotation.MultiUnique;
import com.lease.framework.persistence.database.annotation.NotNull;
import com.lease.framework.persistence.database.annotation.Transient;
import com.lease.framework.persistence.database.annotation.Unique;
import com.lease.framework.persistence.database.annotation.Column;
import com.lease.framework.persistence.database.annotation.Id;
import com.lease.framework.persistence.database.converter.ColumnConverter;
import com.lease.framework.persistence.database.converter.ColumnConverterFactory;
import com.lease.framework.core.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

public class ColumnUtils {

    private ColumnUtils() {
    }

    private static final HashSet<String> DB_PRIMITIVE_TYPES = new HashSet<String>(14);

    static {
        DB_PRIMITIVE_TYPES.add(int.class.getName());
        DB_PRIMITIVE_TYPES.add(long.class.getName());
        DB_PRIMITIVE_TYPES.add(short.class.getName());
        DB_PRIMITIVE_TYPES.add(byte.class.getName());
        DB_PRIMITIVE_TYPES.add(float.class.getName());
        DB_PRIMITIVE_TYPES.add(double.class.getName());

        DB_PRIMITIVE_TYPES.add(Integer.class.getName());
        DB_PRIMITIVE_TYPES.add(Long.class.getName());
        DB_PRIMITIVE_TYPES.add(Short.class.getName());
        DB_PRIMITIVE_TYPES.add(Byte.class.getName());
        DB_PRIMITIVE_TYPES.add(Float.class.getName());
        DB_PRIMITIVE_TYPES.add(Double.class.getName());
        DB_PRIMITIVE_TYPES.add(String.class.getName());
        DB_PRIMITIVE_TYPES.add(byte[].class.getName());
    }

    public static boolean isDbPrimitiveType(Class<?> fieldType) {
        return DB_PRIMITIVE_TYPES.contains(fieldType.getName());
    }

    /**
     * 获取该字段的方法名，必须get驼峰式命名
     *
     * @param entityType
     * @param field
     * @return
     */
    public static Method getColumnGetMethod(Class<?> entityType, Field field) {
        Method getMethod = getColumnMethod(false, field, entityType);

        if (getMethod == null && !Object.class.equals(entityType.getSuperclass())) {
            return getColumnGetMethod(entityType.getSuperclass(), field);
        }
        return getMethod;
    }

    public static Method getColumnMethod(boolean isSet, Field field, Class<?> entityType) {
        String fieldName = field.getName();
        String pre = "get";
        if (isSet) {
            pre = "set";
        }
        StringBuilder str = new StringBuilder();
        str.append(pre).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));
        String methodName = str.toString();
        Method method = null;
        try {
            if (isSet) {
                method = entityType.getDeclaredMethod(methodName, field.getType());
            } else
                method = entityType.getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            if (fieldName.startsWith("m") && fieldName.length() >= 2) {
                try {
                    fieldName = fieldName.substring(1);
                    str = new StringBuilder();
                    str.append(pre).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));
                    methodName = str.toString();
                    if (isSet) {
                        method = entityType.getDeclaredMethod(methodName, field.getType());
                    } else
                        method = entityType.getDeclaredMethod(methodName);
                } catch (NoSuchMethodException e1) {
                    LogUtils.e("<< " + entityType.getName() + " >>not suchMethod:" + methodName + "--->错误的命名:" + fieldName);
                    new Throwable(entityType.getName() + " 变量命名不规范");
                }
            }
        }
        return method;
    }

    /**
     * 设置该字段的方法，必须set驼峰式命名
     *
     * @param entityType
     * @param field
     * @return
     */
    public static Method getColumnSetMethod(Class<?> entityType, Field field) {
        Method setMethod = getColumnMethod(true, field, entityType);
        if (setMethod == null && !Object.class.equals(entityType.getSuperclass())) {
            return getColumnSetMethod(entityType.getSuperclass(), field);
        }
        return setMethod;
    }

    /**
     * 获取字段名称，如果有自定义名称，则用自定义名称作为字段名称
     *
     * @param field
     * @return
     */
    public static String getColumnNameByField(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !TextUtils.isEmpty(column.column())) {
            return column.column();
        }

        Id id = field.getAnnotation(Id.class);
        if (id != null && !TextUtils.isEmpty(id.column())) {
            return id.column();
        }

//        Foreign foreign = field.getAnnotation(Foreign.class);
//        if (foreign != null && !TextUtils.isEmpty(foreign.column())) {
//            return foreign.column();
//        }
//
//        Finder finder = field.getAnnotation(Finder.class);
//        if (finder != null) {
//            return field.getName();
//        }

        return field.getName();
    }

//    public static String getForeignColumnNameByField(Field field) {
//
//        Foreign foreign = field.getAnnotation(Foreign.class);
//        if (foreign != null) {
//            return foreign.foreign();
//        }
//
//        return field.getName();
//    }

    /**
     * 获取字段默认值
     *
     * @param field
     * @return
     */
    public static String getColumnDefaultValue(Field field) {
        Column column = field.getAnnotation(Column.class);
        if (column != null && !TextUtils.isEmpty(column.defaultValue())) {
            return column.defaultValue();
        }
        return null;
    }

    public static boolean isTransient(Field field) {
        return field.getAnnotation(Transient.class) != null;
    }

//    public static boolean isForeign(Field field) {
//        return field.getAnnotation(Foreign.class) != null;
//    }
//
//    public static boolean isFinder(Field field) {
//        return field.getAnnotation(Finder.class) != null;
//    }

    public static boolean isUnique(Field field) {
        return field.getAnnotation(Unique.class) != null;
    }

    public static boolean isNotNull(Field field) {
        return field.getAnnotation(NotNull.class) != null;
    }

    public static boolean isMultiUnique(Field field) {
        return field.getAnnotation(MultiUnique.class) != null;
    }


    /**
     * @param field
     * @return check.value or null
     */
    public static String getCheck(Field field) {
        Check check = field.getAnnotation(Check.class);
        if (check != null) {
            return check.value();
        } else {
            return null;
        }
    }

  /*  @SuppressWarnings("unchecked")
    public static Class<?> getForeignEntityType(com.lease.framework.persistence.database.table.Foreign foreignColumn) {
        Class<?> result = foreignColumn.getColumnField().getType();
        if (result.equals(ForeignLazyLoader.class) || result.equals(List.class)) {
            result = (Class<?>) ((ParameterizedType) foreignColumn.getColumnField().getGenericType()).getActualTypeArguments()[0];
        }
        return result;
    }*/

    @SuppressWarnings("unchecked")
    public static Object convert2DbColumnValueIfNeeded(final Object value) {
        Object result = value;
        if (value != null) {
            Class<?> valueType = value.getClass();
            if (!isDbPrimitiveType(valueType)) {
                ColumnConverter converter = ColumnConverterFactory.getColumnConverter(valueType);
                if (converter != null) {
                    result = converter.fieldValue2ColumnValue(value);
                } else {
                    result = value;
                }
            }
        }
        return result;
    }

//    private static boolean isStartWithIs(final String fieldName) {
//        return fieldName != null && fieldName.startsWith("is");
//    }

    /**
     * 获取布尔类型的方法，规则是is开头，驼峰命名： isMethod()
     * @param entityType
     * @return
     */
//    private static Method getBooleanColumnGetMethod(Class<?> entityType, final String fieldName) {
//        String methodName = "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
//        String compatMethodName = methodName;
//        if (isStartWithIs(fieldName)) {
//            methodName = fieldName;
//        }
//        try {
//            return entityType.getDeclaredMethod(methodName);
//        } catch (NoSuchMethodException e) {
//            LogUtils.w(methodName + " not exist ,now try anther one ");
//            try {
//                return entityType.getDeclaredMethod(compatMethodName);
//            } catch (NoSuchMethodException e1) {
//                LogUtils.w(methodName + " not exist too!!! ");
//            }
//        }
//        return null;
//    }

//    private static Method getBooleanColumnSetMethod(Class<?> entityType, Field field) {
//        String fieldName = field.getName();
//        String methodName = null;
//        String compatName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);;
//        if (isStartWithIs(field.getName())) {
//            methodName = "set" + fieldName.substring(2, 3).toUpperCase() + fieldName.substring(3);
//        } else {
//            methodName = compatName;
//        }
//        try {
//            return entityType.getDeclaredMethod(methodName, field.getType());
//        } catch (NoSuchMethodException e) {
//            LogUtils.w(methodName + " not exist ,now try anther one ");
//            try {
//                return entityType.getDeclaredMethod(compatName);
//            } catch (NoSuchMethodException e1) {
//                LogUtils.w(methodName + " not exist too!!! ");
//            }
//        }
//        return null;
//    }

}
