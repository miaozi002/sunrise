package com.sunrise.jsonparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrise.model.Station;

public class JsonParser {

    private static List<Station> stations;

    private JsonParser() {
    }

    public static List<Station> parseAllJsonFiles(File jsonFileDirectory) throws Exception {
        if (stations != null)
            return stations;

        File[] jsonFilesDir = jsonFileDirectory.listFiles();
        stations = new ArrayList<Station>();
        for (File file : jsonFilesDir) {
            if (file.getName().matches(".*json")) {
                List<Station> stationsInOneFile = parseJson(file);
                if (stationsInOneFile != null)
                    stations.addAll(stationsInOneFile);
            }
        }
        return stations;
    }

    private static List<Station> parseJson(File jsonFile) throws Exception {
        String jsonContent = readJsonFile(jsonFile);
        Gson gson = new Gson();
        Type typeOfObjectsList = new TypeToken<List<Station>>() {
        }.getType();
        return gson.fromJson(jsonContent.toString(), typeOfObjectsList);
    }

    private static String readJsonFile(File jsonFile) throws Exception {
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
