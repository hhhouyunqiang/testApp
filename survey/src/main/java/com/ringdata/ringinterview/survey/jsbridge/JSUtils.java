package com.ringdata.ringinterview.survey.jsbridge;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by xch on 2017/5/16.
 */

public class JSUtils {

    static public String JSCallFuncString(String func, Object[] params) throws Exception {
        StringBuilder buf = new StringBuilder(func);
        buf.append("(");
        if(params != null) {
            for (int i = 0; i < params.length; ++i) {
                buf.append(JSValueString(params[i]));
                if (i < params.length - 1) {
                    buf.append(",");
                }
            }
        }
        buf.append(")");
        return buf.toString();
    }

    static public String JSValueString(Object obj) throws Exception {
        if (obj == null) {
            return "undefined";
        } else if (obj instanceof  String) {
            return (String) obj;
        } else if (obj instanceof Integer
                || obj instanceof Float
                || obj instanceof Double
                || obj instanceof Boolean
                || obj instanceof JSONArray
                || obj instanceof JSONObject) {
            return obj.toString();
        } else if (obj instanceof Object[]) {
            JSONArray array = new JSONArray();
            for(Object o : (Object[]) obj) {
                array.put(o);
            }
            return array.toString();
        } else {
            throw new Exception("Unsupported value " + obj);
        }
    }
}
