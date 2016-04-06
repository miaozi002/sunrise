package com.sunrise.model;

import java.util.List;

public class Level2Data {
    private String label;
    private List<Level3Data> data;
    private Level3Data fields;
    private String tbl;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Level3Data> getData() {
        return data;
    }

    public Level3Data getLevel3DataItem(int id) {
        return data.get(id);
    }

    public void setData(List<Level3Data> data) {
        this.data = data;
    }

    public Level3Data getFields() {
        return fields;
    }

    public void setFields(Level3Data fields) {
        this.fields = fields;
    }

    public String getTbl() {
        return tbl;
    }

    public void setTbl(String tbl) {
        this.tbl = tbl;
    }

}
