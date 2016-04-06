package com.sunrise.jsonparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.sunrise.model.StationWrapper;

public class JsonParser {

    private static List<StationWrapper> stations;


    private JsonParser() {
    }



    public static List<StationWrapper> parseAllJsonFiles(File jsonFileDirectory) throws Exception {
        if (stations != null)
            return stations;

        File[] jsonFilesDir = jsonFileDirectory.listFiles();
        stations = new ArrayList<StationWrapper>();
        for (File file : jsonFilesDir) {
            if (file.getName().matches(".*json")) {
                StationWrapper stationsInOneFile = parseJson(file);
                if (stationsInOneFile != null)
                    stations.add(stationsInOneFile);
            }
        }
        return stations;
    }

    private static StationWrapper parseJson(File jsonFile) throws Exception {
        String jsonContent = readJsonFile(jsonFile);
        Gson gson = new Gson();
        return gson.fromJson(jsonContent.toString(), StationWrapper.class);
    }

    public static String readJsonFile(File jsonFile) throws Exception {
        StringBuffer sb = new StringBuffer();
        InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(jsonFile));
        BufferedReader reader = new BufferedReader(inputStreamReader);
        String line = reader.readLine();
        while (line != null) {
            sb.append(line);
            line = reader.readLine();
        }
        reader.close();
        return sb.toString();
    }
}
