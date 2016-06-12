package com.sunrise.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                info.level1Id = i;
                return true;
            }
        }
        return false;
    }

    public List<Level2Data> getAllLevel2Data() {
        List<Level2Data> leve2DataList = new ArrayList<Level2Data>();

        List<Level1Data> level1DataList = getData();
        for (int i = 0; i < level1DataList.size(); i++) {
            leve2DataList.addAll(level1DataList.get(i).getData());
        }
        Map<String, List<Level2Data>> groupByLabel = new HashMap<String, List<Level2Data>>();
        for (Level2Data d : leve2DataList) {
            if(groupByLabel.containsKey(d.getLabel())){
                groupByLabel.get(d.getLabel()).add(d);
            }else{
                List<Level2Data> l = new ArrayList<Level2Data>();
                l.add(d);
                groupByLabel.put(d.getLabel(), l);
            }
        }

        List<Level2Data> mergedList = new ArrayList<Level2Data>();
        for (String key : groupByLabel.keySet()) {
            List<Level2Data> vDatas = groupByLabel.get(key);
            Level2Data dest = vDatas.get(0);
            for (int i = 1; i < vDatas.size(); i++) {
                dest = dest.mergeWith(vDatas.get(i));
            }
            mergedList.add(dest);
        }
        return mergedList;
       // return leve2DataList;
    }
}