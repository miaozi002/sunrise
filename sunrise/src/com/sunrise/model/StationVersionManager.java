package com.sunrise.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationVersionManager {
    private static StationVersionManager manager = new StationVersionManager();
    private List<StationDetail> stationDetails;
    private File jsonFileDirectory;
    private Map<Integer, VersionInfo> staionVersionMap;

    private StationVersionManager() {
        staionVersionMap = new HashMap<Integer, StationVersionManager.VersionInfo>();
    }

    public void setJsonDir(File dir) {
        this.jsonFileDirectory = dir;
    }

    public static StationVersionManager getInstance() {
        return manager;
    }

    public void setStationDetailList(List<StationDetail> list) {
        stationDetails = list;
    }

    public void startCheck() {
        //checkLocalVersion();
        checkServerVersion();
    }

    private void checkServerVersion() {

    }

   /* private void checkLocalVersion() {
        File[] jsonFilesDir = jsonFileDirectory.listFiles();
        for (File file : jsonFilesDir) {
            if (file.getName().matches(".*json")) {
                Pattern pattern = Pattern.compile("pack.station(\\d+).json");
                Matcher matcher = pattern.matcher(file.getName());
                if (matcher.matches()) {
                    int stationId = Integer.parseInt(matcher.group(1));
                    StationWrapper stationsInOneFile =JsonParser.parseJson(file);
                    if (stationsInOneFile != null){

                        VersionInfo info = new VersionInfo();
                        stationsMap.put(stationId, stationsInOneFile);
                      }
                }
            }
        }
    }*/

    class VersionInfo {
        public int id;
        public int localVersion;
        public int serverVersion;
    }
}
