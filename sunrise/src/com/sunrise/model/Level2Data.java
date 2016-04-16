package com.sunrise.model;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Level2Data {
    private String label;
    private List<Map<String, String>> data;
    private Map<String, String> fields;
    private String tbl;

    public JsonObject getFieldsAsMap() {
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(fields);
        return element.getAsJsonObject();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public Map<String, String> getLevel3DataItem(int id) {
        return data.get(id);
    }

    public void setData(List<Map<String, String>> data) {
        this.data = data;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public String getTbl() {
        return tbl;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }

    public boolean findByNfc(String nfc, NFCSearchInfo info) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get("NFC").equals(nfc)) {
                info.dataId = i;
                return true;
            }
        }
        return false;
    }

}
