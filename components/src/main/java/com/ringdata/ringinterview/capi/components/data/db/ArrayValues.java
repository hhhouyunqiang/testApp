package com.ringdata.ringinterview.capi.components.data.db;

/**
 * Created by Xie Chenghao on 16/8/29.
 */
public class ArrayValues {
    public Object[] array;

    public ArrayValues(int length) {
        array = new Object[length];
    }

    public void set(int i, Object obj) {
        array[i] = obj;
    }

    public Object get(int i) {
        return array[i];
    }

    public String getString(int i) {
        return (String) array[i];
    }

    public int getInt(int i) {
        return (Integer) array[i];
    }
}
