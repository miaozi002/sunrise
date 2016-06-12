package com.sunrise;

public interface FileStateInterface 
{
	void onFileStart(int requestCode,int stationId);
	void onEnd(int requestCode,int stationId);
	void onFileSize(int requestCode,int stationId,int fileSize);
	void onDownloadSize(int requestCode,int stationId,int size);
	void onDownloadFailed(int requestCode,int stationId);
}
