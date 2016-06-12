package com.sunrise;

public interface DownloadProgressListener {
	public void onDownloadSize(int size);
	public void onDownloadEnd();
}
