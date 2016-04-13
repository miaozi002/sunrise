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

    public Level2Data getLevel2DataItem(int id) {
        return data.get(id);
    }

    public boolean findByNfc(String nfc, NFCSearchInfo info) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).findByNfc(nfc, info)) {
                info.lowCategoryId = i;
                return true;
            }
        }
        return false;
    }

}
