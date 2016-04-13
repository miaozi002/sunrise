package com.sunrise.jsonparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.sunrise.model.StationWrapper;

public class JsonFileParser {

    private static Map<Integer, StationWrapper> stationsMap;
    private static File jsonFileDirectory;

    private JsonFileParser() {
    }

    public static void setJsonDir(File dir) {
        jsonFileDirectory = dir;
    }

    public static void reparseJsonFile(int stationId) {
        File file = new File(jsonFileDirectory, String.format("pack.station%d.json", stationId));
        StationWrapper stationsInOneFile = parseJson(file);
        if (stationsInOneFile != null)
            stationsMap.put(stationId, stationsInOneFile);
    }

    public static Map<Integer, StationWrapper> scanAndParseAllJsons() {
        File[] jsonFilesDir = jsonFileDirectory.listFiles();
        Map<Integer, StationWrapper> stationsMap = new HashMap<Integer, StationWrapper>();
        for (File file : jsonFilesDir) {
            if (file.getName().matches(".*json")) {
                Pattern pattern = Pattern.compile("pack.station(\\d+).json");
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    int stationId = Integer.parseInt(matcher.group(1));
                    StationWrapper stationsInOneFile = parseJson(file);
                    if (stationsInOneFile != null)
                        stationsMap.put(stationId, stationsInOneFile);
                }
            }
        }
        return stationsMap;
    }

    public static StationWrapper getStationWrapper(int id) throws Exception {
        if (stationsMap != null)
            return stationsMap.get(id);
        stationsMap = scanAndParseAllJsons();
        return stationsMap.get(id);
    }

    public static StationWrapper parseJson(File jsonFile) {
        try {
            String jsonContent = readJsonFile(jsonFile);
            Gson gson = new Gson();
            return gson.fromJson(jsonContent, StationWrapper.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
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
