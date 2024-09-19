package com.bit45.thespeedtester.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import com.bit45.thespeedtester.R;
import com.bit45.thespeedtester.SpeedTestActivity;
import com.bit45.thespeedtester.WaitingDots.DotsTextView;
import com.bit45.thespeedtester.font.FontelloTextView;
import com.bit45.thespeedtester.font.RobotoTextView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DecimalFormat;
import java.util.Random;

public class MyUIManager {

    private final SharedPreferences prefs;
    private Context context;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;
    public Thread timerThread;
    public TimerTask timerTask;
    public MyDownloadManager downloadManager;
    public MyChartManager chartManager;
    public RobotoTextView rbConnection;
    public FontelloTextView flConnection;
    public RobotoTextView rbSpeed;
    public RobotoTextView rbData;
    public RobotoTextView rbInfo;
    public DotsTextView rbDots;
    public RobotoTextView btnStart;
    public RobotoTextView btnCancel;
    public FontelloTextView btnDuration;
    public FontelloTextView btnData;
    private Random r;
    private boolean firstTest = true;
    private int interstitialCount = 1;
    public boolean isRunning = false;
    public boolean isOnScreen = true;
    private boolean firstTime = false;

    public MyUIManager(Context context, MyDownloadManager downloadManager, MyChartManager chartManager) {
        this.downloadManager = downloadManager;
        this.chartManager = chartManager;
        this.context = context;
        r = new Random();
        initializeInterstitial();

//      Initializing preferences
        prefs = context.getSharedPreferences("SpeedTestPrefs", SpeedTestActivity.MODE_PRIVATE);
        firstTime = prefs.getBoolean("first_time", true);
    }

    private void initializeInterstitial(){
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getString(R.string.interstitial_ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            //This method executed when the ad fails to load
            @Override
            public void onAdFailedToLoad(int errorCode) {
                //See method
                onInterstitialFailToLoad();
                super.onAdFailedToLoad(errorCode);
            }
            //When an interstitial is closes we request another ad
            @Override
            public void onAdClosed() {
                requestAd();
                super.onAdClosed();
            }
        });
        requestAd();
    }

    private void requestAd(){
        //adRequest = new AdRequest.Builder()
                //.addTestDevice("C50165544E3A5E2837030A2726648319")
                //.build();
        //mInterstitialAd.loadAd(adRequest);
    }

    private void showInterstitial(){
        if(mInterstitialAd.isLoaded()) mInterstitialAd.show();
    }

    private void onInterstitialFailToLoad(){
        //We use a postDelayed method to try loading the ad again after 10 seconds
        new Handler().postDelayed(
                //Task to execute
                new Runnable() {
                    @Override
                    public void run() {
                        requestAd();
                    }
                }
                //Delay time for task
                ,10000);
    }

    public void startDownload(int duration, int unitSelected) {
        rbInfo.setText(context.getString(R.string.label_info_predownload));
        rbDots.setVisibility(View.VISIBLE);
        chartManager.resetChart(duration);
        timerTask = new TimerTask(duration, unitSelected);
        timerThread = new Thread(timerTask);
        timerThread.start();
    }

    public void reset(int duration, int unitSelected) {
        //chartManager.resetChart(duration);
        timerTask = new TimerTask(duration, unitSelected);
        timerThread = new Thread(timerTask);
    }

    public void cancel() {
        timerTask.cancel(1);
    }

    public void kill() {
        if(timerTask!=null) timerTask.cancel(4);
    }

    public void onFileDownloaded() {
        timerTask.cancel(2);
    }

    public void onDownloadTimeout() {
        timerTask.cancel(3);
    }

    public void enableStart() {
        isRunning = false;
        ((View) btnCancel.getParent()).setVisibility(View.GONE);
        ((View) btnStart.getParent()).setVisibility(View.VISIBLE);
        btnStart.setEnabled(true);
        btnDuration.setEnabled(true);
        btnData.setEnabled(true);

        //No interstitials on first use of the app
        if (firstTime) return;

        //Not applying interstitial on first test for now
        firstTest = false;

        //Show it if first test or every 3 tests
        if(firstTest || (interstitialCount==3)) {

            /*Show interstitial 1.5 seconds later*/
            Handler mHandler = new Handler();
            Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    showInterstitial();
                }
            };
            mHandler.postDelayed(mRunnable, 1500);

            //Not first test anymore
            firstTest = false;
            //Reset test counter
            interstitialCount = 1;
        }
        //Increment test counter
        else interstitialCount++;
    }

    public int getTime(){
        if(timerTask==null) return 0;
        return timerTask.timeCount;
    }


   /*---------------------------------|
   |                                  |
   |    RUNNABLE CLASS                |
   |                                  |
   |----------------------------------*/

    private class TimerTask implements Runnable {

        int timeCount = 0;
        int duration = 0;
        int unit = 0;
        float lastProgress = 0;
        private int cancel = 0;

        public TimerTask(int duration, int unit) {
            this.duration = duration;
            this.unit = unit;
        }

        @Override
        public void run() {
            //System.out.println("Checking");
            while (!downloadManager.isDownloading()) {
                if (cancel > 0) {
                    updateErrorUI();
                    return;
                }
            }
            //System.out.println("Started");

            int timeSubSec = 0;
            changeTimerTypeface(RobotoTextView.Roboto.ALARM_CLOCK);
            updateTimerInfo(0, 0);
            enableCancelBtn();

            while (timeCount < duration) {

                try {
                    Thread.sleep(10);
                    if (cancel > 0) {
                        changeTimerTypeface(RobotoTextView.Roboto.INFO);
                        if (cancel == 1) updateCancelUI();
                        if (cancel == 2) updateFinishUI(true);
                        if (cancel == 4) return;
                        return;
                    }

                    timeSubSec++;
                    if (timeSubSec == 100) {
                        timeCount++;
                        Log.d("Bit45_TheSpeedTester", "timecount++: " + timeCount);
                        timeSubSec = 0;
                    } else {
                        updateTimerInfo(timeCount, timeSubSec);
                        continue;
                    }

                    updateTimerInfo(timeCount, timeSubSec);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                updateDownloadUI(false);

            }
            timeUp();
        }

        private void updateDownloadUI(final boolean finishedBeforeDuration) {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(finishedBeforeDuration) {
                        timeCount++;
                        Log.d("Bit45_TheSpeedTester", "finishedBeforeDuration timecount++: " + timeCount);
                    }
                    float progress = downloadManager.getProgress();
                    int time = timeCount;
                    float speedBytes;
                    float speedBits = 0;
                    float lengthBits = progress * 8;
                    float lengthBytes = progress;
                    float progressNowBytes = progress - lastProgress;
                    float progressNowBits = progressNowBytes * 8;
                    float downloaded;

                    //descargado este segundo en KiloBytes
                    progressNowBytes /= 1024;
                    //descargado este segundo en KiloBits
                    progressNowBits /= 1000;

                    //tamanio en KiloBytes
                    lengthBytes /= 1024;
                    //Tamanio en KiloBits
                    lengthBits /= 1000;

                    //Velocidad promedio en KiloBytes por Segundo
                    speedBytes = lengthBytes / time;
                    //Velocidad promedio en KIloBits por Segundo
                    speedBits = lengthBits / time;

                    downloaded = lengthBytes;

                    String downloadUnit = context.getString(R.string.label_KB);
                    if(downloaded>=1024){
                        downloadUnit = context.getString(R.string.label_MB);
                        downloaded /= 1024;
                    }

                    String speedUnit = unit==0?
                            context.getString(R.string.label_variable_Kbps)
                            :context.getString(R.string.label_variable_KBps);

                    float labelSpeed = unit==0? speedBits : speedBytes;

                    if(labelSpeed>=1024){
                        speedUnit = unit==0?
                                context.getString(R.string.label_variable_Mbps)
                                :context.getString(R.string.label_variable_MBps);

                        labelSpeed /= 1024;
                    }

                    //Updating UI values
                    //to format decimals
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");

                    rbSpeed.setText(decimalFormat.format(labelSpeed) + " "+speedUnit);
                    rbData.setText(decimalFormat.format(downloaded) + " "+downloadUnit);

                    //System.out.println("lengthBytes: " + lengthBytes + " | speedBytes: " + speedBytes + " | progress: " + progress + " | lastProgress: " + lastProgress);
                    float chartProgress = unit==0? (progressNowBits/1024) : (progressNowBytes/1024);
                    float chartSpeed = unit==0? (speedBits/1024) : (speedBytes/1024);
                    chartManager.addValue(chartProgress , chartSpeed);

                    lastProgress = progress;
                }
            });
        }

        private void updateTimerInfo(int sec, int subSec) {
            final String strTimeSec = sec < 10 ? ("0" + sec) : ("" + sec);
            final String strTimeSubSec = subSec < 10 ? ("0" + subSec) : ("" + subSec);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    rbInfo.setText(strTimeSec + ":" + strTimeSubSec);
                }
            });
        }

        private void changeTimerTypeface(final int type) {

            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(type == RobotoTextView.Roboto.ALARM_CLOCK)
                        rbDots.setVisibility(View.GONE);
                    rbInfo.setRobotoTypeface(type);
                }
            });
        }

        private void updateFinishUI(boolean finishedBeforeDuration) {
            updateDownloadUI(finishedBeforeDuration);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    rbInfo.setRobotoTypeface(RobotoTextView.Roboto.INFO);
                    rbInfo.setText(context.getString(R.string.label_info_postdownload));
                    rbDots.setVisibility(View.GONE);
                }
            });
        }

        private void updateCancelUI() {
            updateDownloadUI(true);
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    rbInfo.setRobotoTypeface(RobotoTextView.Roboto.INFO);
                    rbInfo.setText(context.getString(R.string.label_info_cancelled));
                    rbDots.setVisibility(View.GONE);
                }
            });
        }

        private void updateErrorUI() {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    rbInfo.setRobotoTypeface(RobotoTextView.Roboto.INFO);
                    rbInfo.setText(context.getString(R.string.label_info_failed));
                    rbDots.setVisibility(View.GONE);
                }
            });
        }

        private void enableCancelBtn() {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    ((View) btnStart.getParent()).setVisibility(View.GONE);
                    ((View) btnCancel.getParent()).setVisibility(View.VISIBLE);
                }
            });
        }

        public void cancel(int how) {
            Log.d("Bit45_TheSpeedTester", "cancel: " + how);
            cancel = how;
        }

        public void timeUp() {
            changeTimerTypeface(RobotoTextView.Roboto.INFO);
            updateFinishUI(false);
            downloadManager.timeIsUP();
        }
    }
}
