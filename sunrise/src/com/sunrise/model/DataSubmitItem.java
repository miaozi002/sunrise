package com.sunrise.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

    public void mergeChangesWith(DataSubmitItem item) {
        if (!this.tbl.equals(item.getTbl()))
            return;
        if (!this.id.equals(item.getId()))
            return;
        Set<String> keySet = item.data.keySet();
        for (String key : keySet) {
            data.put(key, item.data.get(key));
        }
    }

    // @Override
    // public int hashCode() {
    // int hashCode = 0;
    // if (tbl != null)
    // hashCode += tbl.hashCode();
    // if (id != null)
    // hashCode += id.hashCode();
    // return hashCode;
    // }
    //
    // @Override
    // public boolean equals(Object o) {
    // if (o == null)
    // return false;
    // if (!(o instanceof DataSubmitItem))
    // return false;
    // DataSubmitItem other = (DataSubmitItem) o;
    // if (tbl == null && other.tbl != null)
    // return false;
    // else if (!tbl.equals(other.tbl))
    // return false;
    //
    // if (id == null && other.id != null)
    // return false;
    // else if (!id.equals(other.id))
    // return false;
    //
    // return true;
    // }

}
