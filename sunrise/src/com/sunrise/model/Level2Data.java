package com.sunrise.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sunrise.util.SunriseUtil;

public class Level2Data {
    private String label;
    private List<Map<String, String>> data;
    private Map<String, FieldsDisplay> fields;
    private String tbl;

    public JsonObject getFieldsAsMap() {
        Gson gson = new Gson();
        JsonElement element = gson.toJsonTree(fields);
        return element.getAsJsonObject();
    }

    // if label is same, we merge their data
    public Level2Data mergeWith(Level2Data d ) {
        if (!label.equals(d.getLabel()))
            return null;

        Level2Data newData = new Level2Data();
        newData.setLabel(this.label);
        newData.setTbl(this.tbl);
        ArrayList<Map<String, String>> list1 = new ArrayList<Map<String,String>>();
        list1.addAll(data);
        HashMap<String, FieldsDisplay> list2 = new HashMap<String, FieldsDisplay>();
        list2.putAll(fields);

        list1.addAll(d.getData());
        list2.putAll(d.getFields());

        newData.setData(list1);
        newData.setFields(list2);
        return newData;
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
            keyDisplayNames.add(fields.get(keyDbNames.get(i)).getName());
        }
        return keyDisplayNames;
    }

    public List<String> getEditNumbers(){
        List<String> keyDbNames = getKeyDbName();
        List<String> editNumbers=new ArrayList<String>();
        for (int i = 0; i < keyDbNames.size(); i++) {
            editNumbers.add(fields.get( keyDbNames.get(i)).getEdit());
        }

        return editNumbers;
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

    public Map<String, FieldsDisplay> getFields() {
        return fields;
    }

    public void setFields(Map<String, FieldsDisplay> fields) {
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
                info.level3Id = i;
                return true;
            }
        }
        return false;
    }

}
