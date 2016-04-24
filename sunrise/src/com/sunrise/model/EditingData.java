package com.sunrise.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.support.v4.util.ArrayMap;

public class EditingData {
    private Map<StationId, List<String>> editMap = new ArrayMap<StationId, List<String>>();

    public static EditingData instance = new EditingData();

    private EditingData() {

    }

    public static EditingData instance() {
        return instance;
    }

    public List<String> getEditValues(StationId id) {
        return editMap.get(id);
    }

    public List<String> createEditValues(StationId id) {
        List<String> list = new ArrayList<String>();
        editMap.put(id, list);
        return list;
    }

    public void setEditValues(StationId id, List<String> values) {
        editMap.put(id, values);
    }
}
