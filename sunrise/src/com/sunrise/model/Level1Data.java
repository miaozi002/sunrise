package com.sunrise.model;

import java.util.List;

public class Level1Data {
    private String label;
    private List<Level2Data> data;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Level2Data> getData() {
        return data;
    }

    public void setData(List<Level2Data> data) {
        this.data = data;
    }

    public Level2Data getDataItem(int id) {
        return data.get(id);
    }
}
