package com.sunrise.model;

import java.util.List;

public class Station {

    private String label;
    private List<Level1Data> data;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<Level1Data> getData() {
        return data;
    }

    public void setData(List<Level1Data> data) {
        this.data = data;
    }

    public Level1Data getDataItem(int id) {
        return data.get(id);
    }

    public boolean findByNfc(String nfc, NFCSearchInfo info) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).findByNfc(nfc, info)) {
                info.highCategoryId = i;
                return true;
            }
        }
        return false;
    }
}