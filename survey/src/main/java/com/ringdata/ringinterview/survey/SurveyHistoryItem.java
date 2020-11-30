package com.ringdata.ringinterview.survey;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by xch on 2017/10/31.
 */

public class SurveyHistoryItem {

    static class SectionItem {
        public String id;
        public List<String> titles;
    }

    public String qid;
    public int index;
    public int total;
    public boolean exit = false;
    public boolean lastStep = false;
    public boolean hasPrev;
    public boolean recording = false;
    public List<SectionItem> sections;

    SurveyHistoryItem() {

    }

    SurveyHistoryItem(JSONObject object) {
        try {
            qid = object.getString("qid");
            index = object.getInt("index");
            total = object.getInt("total");
            if (object.has("exit")) {
                exit = object.getBoolean("exit");
            }
            if (object.has("last_step")) {
                lastStep = object.getBoolean("last_step");
            }
            hasPrev = object.getBoolean("has_prev");
            if (object.has("recording")) {
                recording = object.getBoolean("recording");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
