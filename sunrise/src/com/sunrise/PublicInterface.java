package com.sunrise;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sunrise.activity.LoginActivity;
import com.sunrise.jsonparser.JsonFileParser;
import com.sunrise.model.DataSubmit;
import com.sunrise.model.DataSubmitItem;
import com.sunrise.model.StationDetail;
import com.sunrise.model.StationVersionManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class PublicInterface  {

    public final String STATION_LIST_FILE = "stationList.json";
    public final String MODIFY_DEV_FILE = "modifydev.json";

    public final int    REQUEST_STATIONLIST_CODE = 1;
    public final int    REQUEST_STATION_CODE     = 2;

    private Context m_ctContext;
    private String  m_strServerUrl;
    public String  m_strUsrId;
    public  File    m_strDownloadDir;
    public  File    m_strUploadDir;
    private List<StationDetail> m_liStationList = null;

  //构造函数
    public PublicInterface(Context context)
    {
        this.m_ctContext = context;
        this.m_strDownloadDir = new File(context.getFilesDir().toString() + "/model");
        if(!this.m_strDownloadDir.exists())
        	this.m_strDownloadDir.mkdirs();

        this.m_strUploadDir = new File(context.getFilesDir().toString() + "/upload");
        if(!this.m_strUploadDir.exists())
        	this.m_strUploadDir.mkdirs();
        getUserInfo();
    }

    private void getUserInfo(){
        SharedPreferences spPreferences=m_ctContext.getSharedPreferences(LoginActivity.PREF_NAME, Activity.MODE_PRIVATE);
        m_strServerUrl=spPreferences.getString("serverurl", "");
        StationVersionManager.getInstance().setServerUrl(m_strServerUrl);
        m_strUsrId=spPreferences.getString("usrid", "");
    }


    public String toASCII(String str){
        StringBuilder buf=new StringBuilder();
        char c;
        for(int i=0; i<str.length(); i++){
            c=str.charAt(i);
            if (c<255) {
                buf.append(c);
            } else {
                buf.append("\\u").append(Integer.toHexString(c));
            }
        }

        return buf.toString();

    }

    //JSON文件解析
    protected List<StationDetail> parseJSONWithJSONObject(String jsonData) {
        Gson gson = new Gson();
        Type typeOfObjectsList = new TypeToken<List<StationDetail>>() {
        }.getType();
        return gson.fromJson(jsonData, typeOfObjectsList);
    }

    //读取stationList文件,返回厂站列表
    public List<StationDetail> readStationList()
    {
        File file = new File(m_strDownloadDir, STATION_LIST_FILE);
        String content = null;
        try
        {
            content = JsonFileParser.readJsonFile(file);
            List<StationDetail> list = parseJSONWithJSONObject(content);
            return list;
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    //下载厂站列表文件
    public boolean handleStationListFile(final FileStateInterface fileStateInterface) {

    	File filestationlist = new File(m_strDownloadDir,STATION_LIST_FILE);
        if (!filestationlist.exists())
        {
        	String url = String.format(Locale.getDefault(),
                    "http://%s/php_data/uiinterface.php?reqType=GetStRtdbofUsr&userid=%s&arid=-1", m_strServerUrl,m_strUsrId);
            DownloadFileUtil downloadFileUtil = new DownloadFileUtil(REQUEST_STATIONLIST_CODE);
            downloadFileUtil.StartDownLoad(fileStateInterface,url,m_strDownloadDir,STATION_LIST_FILE,-1);
            return false;
        }
    	return true;
    }

  //获取厂站列表数目
    public int getStationListNums()
    {
    	if (m_liStationList == null)
    		m_liStationList = readStationList();

    	return m_liStationList.size();
    }

    //根据厂站列表下载每个厂站的json文件
    @SuppressLint("DefaultLocale")
    public void handleStation(final FileStateInterface fileStateInterface)
    {
    	getStationListNums();

        if (m_liStationList != null)
        {
        	DownloadFileUtil downloadFileUtil = new DownloadFileUtil(REQUEST_STATION_CODE);
        	 for (int i = 0; i < m_liStationList.size(); i++)
             {
                 int stationId = m_liStationList.get(i).getId();
                 String jsonFileName = String.format("pack.station%d.json", stationId);
                 String url = String.format("http://%s/stationfile/station%d/%s", m_strServerUrl, stationId, jsonFileName);

                 downloadFileUtil.StartDownLoad(fileStateInterface,url,m_strDownloadDir,jsonFileName,stationId);
                 Log.d("Download ",url);
             }
        }
    }

    public void populateDateSubmitFromFile() {
        File pendingFile = new File(m_strUploadDir, MODIFY_DEV_FILE);
        if (!pendingFile.exists()){
            return;
        } else {
            try {
                String content = JsonFileParser.readJsonFile(pendingFile);
                Gson gson = new Gson();
                java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<DataSubmitItem>>() {
                }.getType();

                List<DataSubmitItem> submitItems = gson.fromJson(content, type);


                DataSubmit.instance().setDataToSubmit(submitItems);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    /****************文件上传**************************/
    public void uploadDevFile()
    {
    	 FileUpLoader post;
         File file = new File(m_strUploadDir, m_strUsrId+MODIFY_DEV_FILE);
         if (file.exists())
         {
             String url = String.format("http://%s/php_data/uiinterface.php?reqType=receivefile&filetype=appdevjson", m_strServerUrl);
             Date date = new Date();
             SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
             String time = sdf.format(date);
             String filename = String.format("modifydev%s.json", time);
             filename=m_strUsrId+filename;
             File newFile = new File(m_strUploadDir, filename);
             file.renameTo(newFile);

             String uploadPath = m_strUploadDir.toString() + "/" + filename;
             post = new FileUpLoader(m_ctContext, uploadPath, url, "modifydevjson");
             post.execute();
         }
         else
         {
             Toast.makeText(m_ctContext, R.string.upload_file_not_exist, Toast.LENGTH_LONG).show();
         }
    }
}
