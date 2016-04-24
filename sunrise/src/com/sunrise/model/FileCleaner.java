package com.sunrise.model;

import java.io.File;

public class FileCleaner {
    public static void cleanFiles(File dir, String pattern) {
        File[] jsonFilesDir = dir.listFiles();
        for (File file : jsonFilesDir) {
            if (file.getName().matches(pattern)) {
                file.delete();
            }
        }
    }
}
