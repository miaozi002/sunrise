package com.sunrise.model;

import java.util.ArrayList;
import java.util.List;

public class DataSubmit {
    private List<DataSubmitItem> submitDataList = new ArrayList<DataSubmitItem>();
    private static DataSubmit instance = new DataSubmit();

    private DataSubmit() {

    }

    public void addDataToSubmit(DataSubmitItem item) {
        submitDataList.add(item);
    }

    public static DataSubmit instance() {
        return instance;
    }

    public void commit() {

    }
}
