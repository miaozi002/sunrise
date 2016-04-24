package com.sunrise.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

public class DataSubmit {
    private List<DataSubmitItem> submitDataList = new ArrayList<DataSubmitItem>();
    private static DataSubmit instance = new DataSubmit();

    private DataSubmit() {

    }

    public void addDataToSubmit(DataSubmitItem item) {
        DataSubmitItem existingItemWithSameId = findExistingItem(item);
        if (existingItemWithSameId != null) {
            existingItemWithSameId.mergeChangesWith(item);
            return;
        }
        submitDataList.add(item);
    }

    private DataSubmitItem findExistingItem(DataSubmitItem item) {
        for (DataSubmitItem dataSubmitItem : submitDataList) {
            if (dataSubmitItem.getId().equals(item.getId()) && dataSubmitItem.getTbl().equals(item.getTbl()))
                return dataSubmitItem;
        }
        return null;
    }

    public static DataSubmit instance() {
        return instance;
    }

    public String commit() {
        Gson gson = new Gson();
        String response = gson.toJson(submitDataList);
        return response;
    }

    public void expireOldData() {
        submitDataList.clear();
    }
}
