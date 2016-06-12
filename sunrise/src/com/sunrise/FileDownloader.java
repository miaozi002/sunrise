package com.sunrise;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

public class FileDownloader {
	private static final String TAG = "FileDownloader";

	/* 下载路径  */
    private String downloadUrl;

    /* 线程数 */
    private int threadNums = 3;

    /* 原始文件长度  */
    private int fileSize = 0;

    /* 已下载文件长度 */
    private int downloadSize = 0;

    /* 本地保存文件 */
    private File saveFileDir;

    /* 本地保存文件名 */
    private String saveFileName;

    private int blockSize, downloadSizeMore;

    private int startPosition;
    
    private int endPosition;
    
    /**
     * 获取文件大小
     * @return
     */
    public int getFileSize() {
        return fileSize;
    }

    /**
     * 修改文件名
     * @param fileDir
     * @param fileName
     * @param way 0:删除旧文件,去掉new_;1:把new_去掉;
     * @return true or false
     */
    public boolean handleFile(String fileDir,String fileName,int way)
    {

    	String path = fileDir +"/" + fileName;
    	File file = new File(path);

    	String newPath = fileDir +"/" + "new_" + fileName;
    	File newfile = new File(newPath);
		if (newfile.exists())
		{
			if (way == 0)
	    	{
	    		if (file.exists())
	    		{
	    			if(!file.delete())
					{
						Log.i(FileDownloader.TAG, "File delete failed!");
						return false;
					}
	    		}
	    	}

			if(!newfile.renameTo(file))
			{
				Log.i(FileDownloader.TAG, "File rename failed!");
				 return false;
			}
		}

    	return true;
    }
	 /**
     * 构建文件下载器
     * @param downloadUrl 下载路径
     * @param fileSaveDir 文件保存目录
     * @param fileSaveName 文件保存名称
     * @param threadNum 下载线程数
     */
    public FileDownloader(String downloadUrl, File fileSaveDir, String fileSaveName)
    {
    	try
    	{
    		this.downloadUrl = downloadUrl;
        	this.saveFileDir = fileSaveDir;
        	this.saveFileName = fileSaveName;
        	this.threadNums = 1;
    		URL url = new URL(this.downloadUrl);

    		//创建下载目录
        	if(!fileSaveDir.exists())
            	fileSaveDir.mkdirs();

        	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5*1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Referer", downloadUrl);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("contentType", "UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();

            if (conn.getResponseCode()==200)
            {
                this.fileSize = conn.getContentLength();//根据响应获取文件大小
                if (this.fileSize <= 0)
                {
                	Log.i(FileDownloader.TAG, "Unkown file size!");
                	throw new RuntimeException("Unkown file size ");
                }
            }
            else
            {
            	Log.i(FileDownloader.TAG,"server no response度");
                throw new RuntimeException("server no response ");
            }
		}
    	catch (Exception e)
		{
    		Log.i(FileDownloader.TAG,"don't connection this url");
            throw new RuntimeException("don't connection this url");
		}
    }


    /**
     *  开始下载文件
     * @param listener 监听下载数量的变化,如果不需要了解实时下载的数量,可以设置为null
     * @return 已下载文件大小
     * @throws Exception
     */
    public int download(DownloadProgressListener listener) throws Exception
    {
        try {
            URL url = new URL(downloadUrl);
            URLConnection conn = url.openConnection();

            //获取下载文件的总大小
            fileSize = conn.getContentLength();
            if (fileSize >= 3000)
            	threadNums = 3;

            //计算每个线程要下载的数据量
            blockSize = fileSize / threadNums;

            // 解决整除后百分比计算误差
            downloadSizeMore = (fileSize % threadNums);

            if (!handleFile(saveFileDir.toString(),saveFileName,1))
            	throw new RuntimeException("File delete failed! ");

            String fileName = saveFileDir.toString() + "/" + "new_" + saveFileName;
            File file = new File(fileName);

            FileDownloadThread[] fds = new FileDownloadThread[threadNums];
            for (int i = 0; i < threadNums; i++)
            {
                //启动线程，分别下载自己需要下载的部分
            	startPosition = i*blockSize;
            	endPosition = (i+1)*blockSize-1;
            	
            	//把余数加到最后一个下载线程上
            	if ((downloadSizeMore > 0) && (i == (threadNums-1)))
            		endPosition = (i+1)*blockSize-1+downloadSizeMore;
            	
                FileDownloadThread fdt = new FileDownloadThread(url, file,startPosition,endPosition);
                fdt.setName("Thread" + i);
                fdt.start();
                fds[i] = fdt;
            }

            boolean finished = false;
            while (!finished)
            {
                // 先把整除的余数搞定
            	downloadSize = downloadSizeMore;
                finished = true;
                for (int i = 0; i < fds.length; i++)
                {
                	downloadSize += fds[i].getDownloadSize();
                    if (!fds[i].isFinished())
                    {
                        finished = false;
                    }
                }

                //通知handler去更新视图组件
                if ((listener!=null) && (this.downloadSize < this.fileSize))
                	listener.onDownloadSize(this.downloadSize);//通知目前已经下载完成的数据长度
                
                Thread.sleep(100);
            }

            handleFile(saveFileDir.toString(),saveFileName,0);

          //通知handler去更新视图组件
            if ((listener!=null))
            {
            	listener.onDownloadSize(this.downloadSize);//通知目前已经下载完成的数据长度
            	listener.onDownloadEnd();//通知目前已经下载完成的数据长度
            }
        }
        catch (Exception e)
        {
        	this.downloadSize = -1;
        	Log.d(FileDownloader.TAG +" Failed:",downloadUrl);
        	Log.d(FileDownloader.TAG +" Error:", e.getMessage());
        	throw new Exception("file download fail");
        }

        return this.downloadSize;
    }
}
