package com.sunrise.model;

import java.util.HashMap;
import java.util.Map;

public class DataSubmitItem {
    private String tbl;
    private String id;

    private Map<String, String> data;

    public DataSubmitItem() {
        data = new HashMap<String, String>();
    }

    public void saveToMap(String k, String v) {
        data.put(k, v);
    }

    public String getTbl() {
        return tbl;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
