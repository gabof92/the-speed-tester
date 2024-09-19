package com.bit45.thespeedtester;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

public class SpeedTesterApplication extends Application {

    private static final String PROPERTY_ID = "UA-67288311-2";

    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    @Override
    public void onCreate() {
        super.onCreate();
        GoogleAnalytics.getInstance(this).enableAutoActivityReports(this);
    }

    public SpeedTesterApplication() {
        super();
    }

    /*public synchronized Tracker getTracker(TrackerName trackerId) {
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker t;
        if (!mTrackers.containsKey(trackerId)) {

            if (trackerId == TrackerName.APP_TRACKER){
                t = analytics.newTracker(R.xml.app_tracker);
            }
            else if (trackerId == TrackerName.GLOBAL_TRACKER){
                t = analytics.newTracker(R.xml.global_tracker);
            }
            else{
                t = analytics.newTracker(PROPERTY_ID);
            }
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }*/

}
