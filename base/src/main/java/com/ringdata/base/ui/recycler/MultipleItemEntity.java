package com.ringdata.base.ui.recycler;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.LinkedHashMap;


public class MultipleItemEntity implements MultiItemEntity {
    public static final String KEY_ITEM_TYPE = "ITEM_TYPE";
    public static final int ITEM_SURVEY = 1;//调查
    public static final int ITEM_KNOWLEDGE = 2;//知识

    public static final int ITEM_SURVEY_TASK_CAXI = 3;//任务
    public static final int ITEM_SURVEY_TASK_TOP = 31;//任务顶部
    public static final int ITEM_SURVEY_TASK_ADD = 32;//任务添加
    public static final int ITEM_SURVEY_QUESTIONNAIRE = 4;//问卷

    private final ReferenceQueue<LinkedHashMap<Object, Object>> ITEM_QUEUE = new ReferenceQueue<>();
    private final LinkedHashMap<Object, Object> MULTIPLE_FIELDS = new LinkedHashMap<>();
    private final SoftReference<LinkedHashMap<Object, Object>> FIELDS_REFERENCE =
            new SoftReference<>(MULTIPLE_FIELDS, ITEM_QUEUE);

    MultipleItemEntity(LinkedHashMap<Object, Object> fields) {
        FIELDS_REFERENCE.get().putAll(fields);
    }

    public static MultipleEntityBuilder builder(){
        return new MultipleEntityBuilder();
    }

    @Override
    public int getItemType() {
        return (int) FIELDS_REFERENCE.get().get(KEY_ITEM_TYPE);
    }

    @SuppressWarnings("unchecked")
    public final <T> T getField(Object key){
        return (T) FIELDS_REFERENCE.get().get(key);
    }

    public final LinkedHashMap<?,?> getFields(){
        return FIELDS_REFERENCE.get();
    }

    public final MultipleItemEntity setField(Object key, Object value){
        FIELDS_REFERENCE.get().put(key,value);
        return this;
    }
}
