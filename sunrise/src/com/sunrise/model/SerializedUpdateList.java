package com.sunrise.model;

import java.io.Serializable;
import java.util.List;

public class SerializedUpdateList implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public List<Integer> updateList;

    public SerializedUpdateList(List<Integer> list) {
        this.updateList = list;
    }
}
