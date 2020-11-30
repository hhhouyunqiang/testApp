package com.ringdata.ringinterview.capi.components.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * Created by admin on 2018/4/11.
 */

public class ResponseJson {
    JSONObject responseJsonObject;

    public Boolean getSuccess() {
        if (responseJsonObject == null) {
            return false;
        }
        int code = responseJsonObject.getInteger("code");
        return code == 0;
    }

    public List<Integer> getDeleteIdsAsInt(String key) {
        try {
            JSONObject data = getDataAsObject();
            if (data == null) {
                return null;
            }
            JSONArray jsonArray = data.getJSONArray(key);
            if (jsonArray == null) {
                return null;
            }
            return jsonArray.toJavaObject(List.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getDeleteIdsAsString(String key) {
        try {
            JSONObject data = getDataAsObject();
            if (data == null) {
                return null;
            }
            JSONArray jsonArray = data.getJSONArray(key);
            if (jsonArray == null) {
                return null;
            }
            return jsonArray.toJavaObject(List.class);
        } catch (Exception e) {
            return null;
        }

    }

    public Long getSyncTime() {
        JSONObject extras = getExtrasAsObject();
        if (extras == null) {
            return null;
        }
        return extras.getLong("lastSyncTime");
    }

    public Integer getSyncId() {
        JSONObject extras = getExtrasAsObject();
        if (extras == null) {
            return null;
        }
        return extras.getInteger("id");
    }

    public String getMsg() {
        if (responseJsonObject != null) {
            return responseJsonObject.getString("message") + "";
        } else {
            return "服务繁忙";
        }
    }

    public JSONArray getDataAsArray() {
        try {
            return responseJsonObject.getJSONArray("data");
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getDataAsObject() {
        try {
            return responseJsonObject.getJSONObject("data");
        } catch (Exception e) {
            return null;
        }
    }

    public boolean getDataAsBoolean() {
        try {
            return responseJsonObject.getBoolean("data");
        } catch (Exception e) {
            return false;
        }
    }

    public JSONObject getExtrasAsObject() {
        try {
            return responseJsonObject.getJSONObject("extras");
        } catch (Exception e) {
            return null;
        }
    }

    public JSONArray getExtrasAsArray() {
        try {
            return responseJsonObject.getJSONArray("extras");
        } catch (Exception e) {
            return null;
        }
    }

    public JSONObject getAsObjectByKey(String key) {
        try {
            return responseJsonObject.getJSONObject(key);
        } catch (Exception e) {
            return null;
        }
    }

    public String getAsStringByKey(String key) {
        try {
            return responseJsonObject.getString(key);
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseJson(String response) {
        try {
            this.responseJsonObject = JSON.parseObject(response);
        } catch (Exception e) {
            this.responseJsonObject = null;
        }

    }
}
