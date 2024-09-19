package com.bit45.thespeedtester.managers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class handles the Asynchronous Task that takes care of establishing
 * an internet connection to the server that contains the test file we download
 * to test the download speed, keeping track of the progress of the download
 * and communicating with the UIManager to publish progress on the UI
 */
public class MyDownloadManager {

    private Context context;
    //Asynchronous Task that handles the download
    public DownloadTask downloadTask;
    //Class that manages UI updates
    public MyUIManager uiManager;

    /*CONSTRUCTOR*/
    public MyDownloadManager(Context context) {
        this.context = context;
    }

    /*This method starts the download*/
    public void startDownload(int dataLimit) {
        //Create a new download Task (needs to know the data limit to stop the download)
        downloadTask = new DownloadTask(context, dataLimit);
        //Execute the download of the file referenced by the URL
        downloadTask.execute(getURL());
    }

    /*Stops the download, this method is called when the user presses the stop button*/
    public void cancel() {
        downloadTask.stopDownload(true);
    }

    /*Stops the download, this method is called when the maximum duration of the test is reached*/
    public void timeIsUP() {
        downloadTask.stopDownload(false);
    }

    /*Stops the download, this method is called when the activity is destroyed while a download is in progress*/
    public void kill() {
        if(downloadTask==null) return;
        downloadTask.kill = true;
        downloadTask.stopDownload(true);
    }

    /*Returns the URL of the file to download*/
    private String getURL() {
        return "https://dl.google.com/dl/android/studio/install/2.1.1.0/android-studio-bundle-143.2821654-windows.exe";
    }

    /*This method is called when another class needs to access the current download progress*/
    public float getProgress() {
        return downloadTask.progress;
    }

    /*This method is called when another class needs to know if the download task is running*/
    public boolean isDownloading() {
        return downloadTask.hasStarted;
    }

    /*---------------------------------|
    |                                  |
    |    ASYNC TASK DOWNLOAD CLASS     |
    |                                  |
    |----------------------------------*/
                                     //AsyncTask<Params, Progress, Result>
    private class DownloadTask extends AsyncTask<String, Float, String> {

        private final int maxData;
        private Context context;
        private PowerManager.WakeLock mWakeLock;
        public double length = 0;
        public float progress = 0;
        public boolean hasStarted = false;
        private boolean userCancelled = false;
        private InputStream input;
        private HttpURLConnection connection;
        private String backgroundResult;
        public boolean kill = false;

        public DownloadTask(Context context, int data) {
            this.context = context;
            this.maxData = data * 1024 * 1024;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            input = null;
            //OutputStream output = null;
            connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10000);
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1 if server did not report the length
                length = connection.getContentLength();

                // download the file
                input = connection.getInputStream();

                byte data[] = new byte[1024];
                float total = 0;
                int count;

                hasStarted = true;
                Log.d("Bit45_TheSpeedTester", "Starting Download");
                while ((count = input.read(data)) != -1) {

                    total += count;
                    if(total>=maxData) {
                        publishProgress(total);
                        input.close();
                        return null;
                    }

                    // allow canceling with back button
                    if (isCancelled()) {
                        //Progress is publish so that UI doesn't have to
                        // to wait for input to close in order to refresh
                        publishProgress(total);
                        input.close();

                        backgroundResult = "CANCELLED";
                        return backgroundResult;
                    }

                    // publishing the progress....
                    publishProgress(total);

                }

            } catch (Exception e) {
                Log.d("Bit45_TheSpeedTester", "doInBackground() Exception: " + e.toString());
                backgroundResult = "TIMEOUT";
                return backgroundResult;
            } finally {
                closeCommunications();
            }
            return null;
        }

        private void closeCommunications() {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
                //TODO research what this means
            }

            if (connection != null)
                connection.disconnect();
        }

        @Override
        protected void onProgressUpdate(Float... data) {
            super.onProgressUpdate(data);
            progress = data[0];
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Bit45_TheSpeedTester", "onPostExecute");
            mWakeLock.release();

            if (result!=null && result.equals("TIMEOUT")) timeOutDownload();
            else finishDownload();

            Log.d("Bit45_TheSpeedTester", "Result: " + result);
        }

        @Override
        protected void onCancelled(String result) {
            Log.d("Bit45_TheSpeedTester", "onCancelled API>11");
            handleCancelled(result);
        }

        @Override
        protected void onCancelled() {
            Log.d("Bit45_TheSpeedTester", "onCancelled API<11");
            handleCancelled(backgroundResult);
        }

        private void handleCancelled(String result){
            mWakeLock.release();

            Log.d("Bit45_TheSpeedTester", "Result: " + result);

            if (userCancelled) {
                Log.d("Bit45_TheSpeedTester", "Cancelled by USER");
            } else {
                Log.d("Bit45_TheSpeedTester", "Cancelled by timer");
            }

            closeCommunications();
            if(kill) return;
            uiManager.enableStart();
        }

        public void stopDownload(boolean userCancelled) {
            Log.d("Bit45_TheSpeedTester", "DownloadTask CANCEL");
            this.userCancelled = userCancelled;
            cancel(true);
        }

        private void finishDownload() {
            if(kill) return;
            uiManager.enableStart();
            uiManager.onFileDownloaded();
        }

        private void timeOutDownload() {
            if(kill) return;
            uiManager.enableStart();
            uiManager.onDownloadTimeout();
        }

    }

}
