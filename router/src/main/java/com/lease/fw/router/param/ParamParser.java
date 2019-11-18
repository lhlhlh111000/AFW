package com.lease.fw.router.param;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * created time: 2019-11-13
 * author: cqt
 * description: 参数解析
 */
public class ParamParser {

    public static void init(Activity activity) {
        List<Field> fields = parseField(activity.getClass());
        if(fields.isEmpty()) {
            return;
        }
        Bundle bundle = activity.getIntent().getExtras();
        if(null == bundle) {
            bundle = new Bundle();
        }
        for(Field field : fields) {
            setupField(activity, field, bundle);
        }
    }

    public static void init(Fragment fragment) {
        List<Field> fields = parseField(fragment.getClass());
        if(fields.isEmpty()) {
            return;
        }

        Bundle bundle = fragment.getArguments();
        if(null == bundle) {
            bundle = new Bundle();
        }
        for(Field field : fields) {
            setupField(fragment, field, bundle);
        }
    }

    private static List<Field> parseField(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        if(declaredFields.length > 0) {
            for(Field field : declaredFields) {
                if(field.isAnnotationPresent(BindParam.class)) {
                    fields.add(field);
                }
            }
        }
        return fields;
    }

    private static void setupField(Object target, Field field, Bundle bundle) {
        String key = field.getAnnotation(BindParam.class).value();
        trySet(field, target, bundle.get(key));
    }

    static void trySet(Field field, Object target, @Nullable Object value) {
        if(null == value) {
            return;
        }
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to assign " + value + " to " + field + " on " + target, e);
        }
    }

//    public static Bundle buildParam(Object ...objects) {
//        if(objects.length <= 0) {
//            return null;
//        }
//        List<Object> listObject = new ArrayList<>();
//        for(Object obj : objects) {
//            if(obj.getClass().isAnnotationPresent(ParamKey.class)) {
//                listObject.add(obj);
//            }
//        }
//        if(listObject.size() <= 0) {
//            return null;
//        }
//        Bundle bundle = new Bundle();
//        for(Object obj : listObject) {
//            String key = obj.getClass().getAnnotation(ParamKey.class).value();
//            if(obj instanceof Integer) {
//                bundle.putInt(key, (Integer) obj);
//            }else if(obj instanceof int[]){
//                bundle.putIntArray(key, (int[]) obj);
//            }else if(obj instanceof Boolean) {
//                bundle.putBoolean(key, (Boolean) obj);
//            }else if(obj instanceof boolean[]){
//                bundle.putBooleanArray(key, (boolean[]) obj);
//            }else if(obj instanceof Byte) {
//                bundle.putByte(key, (Byte) obj);
//            }else if(obj instanceof byte[]) {
//                bundle.putByteArray(key, (byte[]) obj);
//            }else if(obj instanceof String) {
//                bundle.putString(key, (String) obj);
//            }else if(obj instanceof String[]) {
//                bundle.putStringArray(key, (String[]) obj);
//            }else if(obj instanceof Double) {
//                bundle.putDouble(key, (Double) obj);
//            }else if(obj instanceof double[]) {
//                bundle.putDoubleArray(key, (double[]) obj);
//            }else if(obj instanceof Float) {
//                bundle.putFloat(key, (Float) obj);
//            }else if(obj instanceof float[]){
//                bundle.putFloatArray(key, (float[]) obj);
//            }else if(obj instanceof Long) {
//                bundle.putLong(key, (Long) obj);
//            }else if(obj instanceof long[]) {
//                bundle.putLongArray(key, (long[]) obj);
//            }else if(obj instanceof Short) {
//                bundle.putShort(key, (Short) obj);
//            }else if(obj instanceof short[]){
//                bundle.putShortArray(key, (short[]) obj);
//            }else if(obj instanceof Bundle) {
//                bundle.putBundle(key, (Bundle) obj);
//            }else if(obj instanceof Parcelable) {
//                bundle.putParcelable(key, (Parcelable) obj);
//            }else if(obj instanceof Parcelable[]) {
//                bundle.putParcelableArray(key, (Parcelable[]) obj);
//            }else if(obj instanceof ArrayList && ((ArrayList)obj).size() > 0) {
//                Object childItem = ((ArrayList) obj).get(0);
//                if(childItem instanceof String) {
//                    bundle.putStringArrayList(key, (ArrayList<String>)obj);
//                }else if(childItem instanceof Integer) {
//                    bundle.putIntegerArrayList(key, (ArrayList<Integer>) obj);
//                }else if(childItem instanceof Parcelable) {
//                    bundle.putParcelableArrayList(key, (ArrayList<Parcelable>) obj);
//                }
//            }else if(obj instanceof Serializable) {
//                bundle.putSerializable(key, (Serializable) obj);
//            }
//        }
//        return bundle;
//    }
}