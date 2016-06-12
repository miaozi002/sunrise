package com.sunrise;

import java.io.File;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.sunrise.CustomMultipartEntity.ProgressListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class FileUpLoader extends AsyncTask<String, Integer, String> {

	private Context context;
    private String filePath;
    private ProgressDialog pd;
    private long totalSize;
    private String requestUrl;
    private String name;

    //filePath:文件上载全路径,name:上传文件方法
    public FileUpLoader(Context context, String filePath, String requestUrl, String name) {
        this.context = context;
        this.filePath = filePath;
        this.requestUrl = requestUrl;
        this.name = name;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("Uploading Picture...");
        pd.setCancelable(false);
        pd.show();
    }

    @Override
    protected String doInBackground(String... params) {
        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(requestUrl);

        try {
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                    new ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });

            // 使用FileBody上传
            File file = new File(filePath);
            multipartContent.addPart(name, new FileBody(file));
            totalSize = multipartContent.getContentLength();
            // 上传
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());

            if(response.getStatusLine().getStatusCode() == 200)
            	file.delete();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return serverResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        pd.setProgress((progress[0]));
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("result: " + result);
        pd.dismiss();
    }

    @Override
    protected void onCancelled() {
        System.out.println("cancel");
    }
}
