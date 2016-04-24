package com.sunrise.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sunrise.activity.StationListActivity;
import com.sunrise.activity.StationListActivity.StationDetailMsgHandler;
import com.sunrise.jsonparser.JsonFileParser;

public class StationVersionManager {
    private static StationVersionManager manager = new StationVersionManager();
    private List<StationDetail> stationDetails;
    private File jsonFileDirectory;
    private String serverUrl;
    private StationDetailMsgHandler handler;
    private Map<Integer, VersionInfo> staionVersionMap;

    private StationVersionManager() {
        staionVersionMap = new HashMap<Integer, VersionInfo>();
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

    private void checkServerVersion() throws Exception {

        try {
            for (int i = 0; i < stationDetails.size(); i++) {
                int stationId = stationDetails.get(i).getId();

                HttpClient httpClient = new DefaultHttpClient();
                String url = String.format(Locale.getDefault(), "http://%s/stationfile/station%d/pack.station%d.json", serverUrl, stationId, stationId);
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity, "utf-8");

                    JsonParser parser = new JsonParser();
                    JsonObject jsonObject = parser.parse(response).getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                        String key = entry.getKey();
                        JsonElement ele = entry.getValue();
                        if (key.equals("jsverb")) {
                            int serverVersion = ele.getAsInt();
                            VersionInfo versionInfo = staionVersionMap.get(stationId);
                            if(versionInfo!=null)
                                versionInfo.serverVersion = serverVersion;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void checkLocalVersion() throws Exception {
        try {
            for (int i = 0; i < stationDetails.size(); i++) {
                StationDetail s = stationDetails.get(i);
                int stationId = s.getId();

                VersionInfo vi = new VersionInfo();
                vi.id = stationId;
                vi.localVersion = 0;
                vi.serverVersion = 0;

                JsonFileParser.reparseJsonFile(stationId);
                StationWrapper w = JsonFileParser.getStationWrapper(stationId);
                if (w != null) {
                    vi.localVersion = w.getJsverb();
                }
                staionVersionMap.put(stationId, vi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendUpdateList() {
        List<VersionInfo> list = new ArrayList<VersionInfo>();
        for (Integer stationId : staionVersionMap.keySet()) {
            VersionInfo vi = staionVersionMap.get(stationId);
            if (vi.serverVersion > vi.localVersion) {
                list.add(vi);
            }
        }
        handler.obtainMessage(StationListActivity.MSG_VERSION_UPDATED, list).sendToTarget();
    }

    public void startCheck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkLocalVersion();
                    checkServerVersion();
                    sendUpdateList();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    public void setMsgHandler(StationListActivity.StationDetailMsgHandler mHandler) {
        this.handler = mHandler;
    }
}
