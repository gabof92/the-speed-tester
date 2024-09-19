package com.bit45.thespeedtester.managers;

import android.app.Activity;
import android.content.Context;

import com.bit45.thespeedtester.SpeedTesterApplication;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class GoogleServicesManager {

    private Context context;
    private Tracker mTracker;

    public GoogleServicesManager(Context context){
        this.context = context;
    }

    public void initializeBanner(int idBanner){
        AdView mAdView = (AdView) ((Activity)context).findViewById(idBanner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void initializeTracker(){
        // Obtain the shared Tracker instance.
        //SpeedTesterApplication application = (SpeedTesterApplication) ((Activity)context).getApplication();
        //mTracker = application.getTracker(SpeedTesterApplication.TrackerName.APP_TRACKER);
    }

    //Get an Analytics tracker to report app starts and uncaught exceptions etc.
    public void reportActivityStart(Activity activity){
        GoogleAnalytics.getInstance(context).reportActivityStart(activity);
    }

    //Stop the analytics tracking
    public void reportActivityStop(Activity activity){
        GoogleAnalytics.getInstance(context).reportActivityStop(activity);
    }

    /*This is supposed to be used in the appropriate method,
    such as onResume for an Activity or onPageSelected for a ViewPager
    to log when the screen changes*/
    public void trackScreen(String name){
        mTracker.setScreenName(name);
    }

    public void sendEvent(String category, String event){
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(event)
                .build());
    }
}
