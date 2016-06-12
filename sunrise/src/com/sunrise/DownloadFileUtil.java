package com.sunrise;

import java.io.File;

public class DownloadFileUtil {
	private int m_iRequestCode;
	
	public DownloadFileUtil(final int requestCode)
	{
		this.m_iRequestCode = requestCode;
	}
    
    public void StartDownLoad(final FileStateInterface fileStateInterface, final String url, final File savedir,
            final String filename,final int stationId) 
    {
        fileStateInterface.onFileStart(m_iRequestCode,stationId);

        new Thread(new Runnable() {

            @Override
            public void run() {
                final FileDownloader loader = new FileDownloader(url, savedir, filename);
                fileStateInterface.onFileSize(m_iRequestCode,stationId,loader.getFileSize());

                try {
                    loader.download(new DownloadProgressListener() 
                    {
                        @Override
                        public void onDownloadSize(int size) 
                        {
                            fileStateInterface.onDownloadSize(m_iRequestCode,stationId,size);
                        }
                        
                        @Override
                        public void onDownloadEnd() 
                        {
                        	fileStateInterface.onEnd(m_iRequestCode,stationId);
                        }
                    });

                } 
                catch (Exception e)
                {
                    fileStateInterface.onDownloadFailed(m_iRequestCode,stationId);
                }
            }

        }).start();
    }
}
