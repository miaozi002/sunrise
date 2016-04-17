package com.sunrise.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sunrise.util.SunriseUtil;

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

    public List<String> getKeyDbName() {
        return SunriseUtil.toList(fields.keySet());
    }

    public List<String> getKeyDisplayName() {
        List<String> keyDbNames = getKeyDbName();
        List<String> keyDisplayNames = new ArrayList<String>();
        for (int i = 0; i < keyDbNames.size(); i++) {
            keyDisplayNames.add(fields.get(keyDbNames.get(i)));
        }
        return keyDisplayNames;
    }

    public List<String> getValues(int level3DataId) {
        Map<String, String> map = data.get(level3DataId);
        List<String> valueList = new ArrayList<String>();
        List<String> keyDbNames = getKeyDbName();
        for (int i = 0; i < keyDbNames.size(); i++) {
            valueList.add(map.get(keyDbNames.get(i)));
        }
        return valueList;
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
