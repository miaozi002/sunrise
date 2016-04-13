package com.sunrise.model;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.sunrise.jsonparser.JsonParser;

public class StationVersionManager {
    private static StationVersionManager manager = new StationVersionManager();
    private List<StationDetail> stationDetails;
    private File jsonFileDirectory;
    private String serverUrl;
    private Map<Integer, VersionInfo> staionVersionMap;

    private StationVersionManager() {
        staionVersionMap = new HashMap<Integer, StationVersionManager.VersionInfo>();
    }

    public void setJsonDir(File dir) {
        this.jsonFileDirectory = dir;
    }

    public void setServerUrl(String url) {
        serverUrl = url;
    }

    public static StationVersionManager getInstance() {
        return manager;
    }

    public void setStationDetailList(List<StationDetail> list) {
        stationDetails = list;
    }

    public void getUpdateList(List<Integer> updateIdList) {
        updateIdList.clear();
        for (Integer stationId : staionVersionMap.keySet()) {
            VersionInfo vi = staionVersionMap.get(stationId);
            if (vi.serverVersion > vi.localVersion) {
                updateIdList.add(vi.id);
            }
        }
    }

    public void startCheck() {
        checkLocalVersion();
        checkServerVersion();
    }

    private void checkServerVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Integer stationId : staionVersionMap.keySet()) {
                        HttpClient httpClient = new DefaultHttpClient();
                        String url = String.format("http://%s/stationfile/station%d/pack.station%d.json", serverUrl, stationId, stationId);
                        HttpGet httpGet = new HttpGet(url);
                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        if (httpResponse.getStatusLine().getStatusCode() == 200) {
                            HttpEntity entity = httpResponse.getEntity();
                            String response = EntityUtils.toString(entity, "utf-8");
                            Gson gson = new Gson();
                            StationWrapper stationWrapper = gson.fromJson(response, StationWrapper.class);
                            VersionInfo vInfo = staionVersionMap.get(stationId);
                            if (vInfo != null) {
                                vInfo.serverVersion = stationWrapper.getJsverb();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void checkLocalVersion() {
        try {
            Map<Integer, StationWrapper> stationMap = JsonParser.scanAndParseAllJsons(jsonFileDirectory);
            for (Integer stationId : stationMap.keySet()) {
                StationWrapper wrapper = stationMap.get(stationId);
                VersionInfo vi = staionVersionMap.get(stationId);
                if (vi == null) {
                    vi = new VersionInfo();
                    vi.id = stationId;
                    vi.localVersion = wrapper.getJsverb();
                    vi.serverVersion = vi.localVersion;
                    staionVersionMap.put(stationId, vi);
                } else {
                    vi.localVersion = wrapper.getJsverb();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseJSONWithGSON(String jsondata) {
        Gson gson = new Gson();
        StationWrapper stationWrapper = gson.fromJson(jsondata, StationWrapper.class);
        int localVersion = stationWrapper.getJsverb();
        System.out.println(localVersion);
    }

    class VersionInfo {
        public int id;
        public int localVersion;
        public int serverVersion;
    }
}
