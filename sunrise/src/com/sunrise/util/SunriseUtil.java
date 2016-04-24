package com.sunrise.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SunriseUtil {
    public static <T> List<T> toList(Set<T> set) {
        List<T> list = new ArrayList<T>();
        list.addAll(set);
        return list;
    }
}
