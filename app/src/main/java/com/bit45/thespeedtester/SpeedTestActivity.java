package com.bit45.thespeedtester;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bit45.thespeedtester.managers.GoogleServicesManager;
import com.db.chart.view.LineChartView;
import com.bit45.thespeedtester.Database.DatabaseCreator;
import com.bit45.thespeedtester.Database.TestResultModel;
import com.bit45.thespeedtester.Dialogs.CustomSingleChoiceDialog;
import com.bit45.thespeedtester.NavDrawer.DrawerAdapter;
import com.bit45.thespeedtester.NavDrawer.DrawerItem;
import com.bit45.thespeedtester.WaitingDots.DotsTextView;
import com.bit45.thespeedtester.font.FontelloTextView;
import com.bit45.thespeedtester.font.RobotoTextView;
import com.bit45.thespeedtester.managers.MyChartManager;
import com.bit45.thespeedtester.managers.MyDatabaseManager;
import com.bit45.thespeedtester.managers.MyDownloadManager;
import com.bit45.thespeedtester.managers.MyUIManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;


//TODO COMMENT EVERYTHING
//TODO REMOVE WARNINGS (there are still some but its ready for production)
public class SpeedTestActivity extends AppCompatActivity {

    int apiVersion = android.os.Build.VERSION.SDK_INT;

    /*Navigation Drawer and Actionbar*/
    private ListView mDrawerList;
    private List<DrawerItem> mDrawerItems;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    /*Buttons*/
    private RobotoTextView btnStart;
    private RobotoTextView btnCancel;
    private FontelloTextView btnDuration;
    private FontelloTextView btnData;

    /*Labels*/
    private FontelloTextView flConnection;
    private RobotoTextView rbConnection;
    private RobotoTextView rbSpeed;
    private RobotoTextView rbData;
    private RobotoTextView rbInfo;
    private RobotoTextView rbChartAxisY;
    private DotsTextView rbDots;

    /*Managers*/
    private MyDownloadManager downloader;
    private MyUIManager uiManager;
    private MyChartManager chartManager;
    private GoogleServicesManager gsManager;

    /*Chart*/
    private LineChartView chart;

    /*Dialogs*/
    private CustomSingleChoiceDialog customDialogDuration;
    private CustomSingleChoiceDialog customDialogData;
    private CustomSingleChoiceDialog customDialogUnit;

    /*Toast*/
    private Toast mToast = null;

    /*Preferences*/
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;
    private int duration;
    private int dataSize;
    private int durationSelected;
    private int dataSelected;
    private int unitSelected;
    private boolean choseUnit;
    private boolean isUpdate;

    /*Database*/
    private DatabaseCreator dbCreator;
    private TestResultModel resultModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Bit45_TheSpeedTester", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);

//      initialize Google services manager (used for Admob and Google Analytics)
        //gsManager = new GoogleServicesManager(this);
//      initialize G-Analytics Tracker
        //gsManager.initializeTracker();
//      initialize AdMob Banner
        //gsManager.initializeBanner(R.id.adView);

//      Init duration and data values from Shared Preferences
        initializePrefs();

//      Get database and initialize data model
        dbCreator = new DatabaseCreator(this, "DBTester", null);
        resultModel = new TestResultModel();

//      Set up Navigation Drawer variables, listeners, icons, actionbar
        initializeNavDrawer();
//      Initialize all graphic component variables, and its listeners
        initializeViews();
//      Initialize UI, Download and Database Managers
        initializeManagers();

//      Creating dialogs for selecting data and duration
        customDialogData = createDialogData();
        customDialogDuration = createDialogDuration();
        customDialogUnit = createDialogUnit();

        if(!choseUnit) {
            customDialogUnit.show();
            prefsEditor.putBoolean("choseUnit", true);
            prefsEditor.apply();
        }

//      Show product tour if its not first time (check for first time = true)
        showProductTour(true);

        checkVersion();
    }

    /*---------------------------------|
    |   ACTIVITY's LIFECYCLE METHODS   |
    |----------------------------------*/
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//      Activate the actionbar icon that opens NavDrawer
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStart() {
        //CALLED AFTER onCreate BUT BEFORE HAVING ACCESS TO THE UI
        Log.d("Bit45_TheSpeedTester", "onStart");
        super.onStart();
        //gsManager.reportActivityStart(this);
    }

    @Override
    protected void onResume() {
        //ALWAYS CALLED, HAVE ACCESS TO UI
        Log.d("Bit45_TheSpeedTester", "onResume");
        super.onResume();
        checkConnection();
        //gsManager.trackScreen("Speed Test");
    }

    @Override
    protected void onPause() {
        //APP IS GOING TO LEAVE THE SCREEN
        //COULD BE THE ONLY METHOD TO BE CALLED BEFORE APP IS KILLED
        Log.d("Bit45_TheSpeedTester", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        //APP HAS OFFICIALLY LEFT THE SCREEN
        Log.d("Bit45_TheSpeedTester", "onStop");
        super.onStop();
        //gsManager.reportActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        //GOING TO THE GARBAGE COLLECTOR
        Log.d("Bit45_TheSpeedTester", "onDestroy");
//      Killing background tasks
        uiManager.kill();
        downloader.kill();
        /*Save the pref value for the first time as false to indicate
        * its no longer the first time the user uses the app*/
        prefsEditor.putBoolean("first_time", false);
        prefsEditor.apply();
        super.onDestroy();
    }

    /*---------------------------------|
    |   TOOLBAR MENU METHODS           |
    |----------------------------------*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//      Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_speed_test, menu);

//      Assign icons to actionbar items (buttons)
        setMenuItemIcon(menu.findItem(R.id.action_test_save), R.string.icon_label_action_save);
        setMenuItemIcon(menu.findItem(R.id.action_test_list), R.string.icon_label_action_result_list);
        setMenuItemIcon(menu.findItem(R.id.action_test_unit), R.string.icon_label_action_unit);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

    /*Handle action bar item clicks here.*/

        int id = item.getItemId();

        /*Button to save the result pressed*/
        if (id == R.id.action_test_save) {
            //if the test is running
            if(uiManager.isRunning) {
                showToast(R.string.test_toast_running);
                return false;
            }
            //if result has already been saved
            else if(resultModel.id!=-1){
                showToast(R.string.test_toast_save_again);
                return false;
            }
            //if the test didn't start
            else if(uiManager.getTime()<1){
                showToast(R.string.test_toast_save_noresult);
                return false;
            }
            //error while trying to save result
            if(!saveResult()){
                showToast(R.string.test_toast_save_error);
                return false;
            }
            //result saved successfully
            showToast(R.string.test_toast_save_done);
            //gsManager.sendEvent("Action ST", "Save Test");
            return true;
        }
        /*Button to open result list was pressed*/
        else if(id == R.id.action_test_list){
            //gsManager.sendEvent("Action ST", "Open Saved Results");
            Intent i = new Intent(SpeedTestActivity.this, ResultListActivity.class);
            startActivity(i);
        }
        /*Button to change units was pressed*/
        else if(id == R.id.action_test_unit){
            //if the test is running
            if(uiManager.isRunning) {
                showToast(R.string.test_toast_running);
                return false;
            }
            //gsManager.sendEvent("Action ST", "Change Unit");
            customDialogUnit.show();
        }

        return super.onOptionsItemSelected(item);
    }

    /*This method converts into a Bitmap a FontelloTextView,
     * the text of the View is the string referenced by the parameter "iconResId"
     * then it sets the Bitmap as the icon for the menu item passed as the parameter "item"*/
    private void setMenuItemIcon(MenuItem item, int iconResId){
        FontelloTextView actionBarTextLogo = (FontelloTextView) getLayoutInflater().inflate(R.layout.template_toolbar_action_icon, null);
        actionBarTextLogo.setText(getString(iconResId));
        actionBarTextLogo.measure(0, 0);
        actionBarTextLogo.layout(0, 0, actionBarTextLogo.getMeasuredWidth(),actionBarTextLogo.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(actionBarTextLogo.getMeasuredWidth(),actionBarTextLogo.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        actionBarTextLogo.draw(canvas);
        BitmapDrawable actionBarLogo = new BitmapDrawable(getResources(), bitmap);
        item.setIcon(actionBarLogo);
    }

    /*---------------------------------|
    |   OTHER OVERRIDE METHODS         |
    |----------------------------------*/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d("Bit45_TheSpeedTester", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
     /* When the user presses the back button and the NavDrawer is opened,
      * this will close the NavDrawer instead of closing the app*/
        if(mDrawerLayout.isDrawerOpen(mDrawerList)){
            mDrawerLayout.closeDrawer(mDrawerList);
            return;
        }
        super.onBackPressed();
    }

    /*---------------------------------|
    |    NAVIGATION DRAWER METHODS     |
    |----------------------------------*/
    private void initializeNavDrawer() {
//      Getting an XML toolbar instead of the default actionbar
//      this is done to support older API's that didn't have actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//      set the inflated toolbar as the support actionbar
        setSupportActionBar(toolbar);

//      Initializing NavDrawer (DrawerLayout from XML)
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//      Initializing the ListView of de NavDrawer (from XML)
        mDrawerList = (ListView) findViewById(R.id.list_view);

//      Setting the actionbar title
        mTitle = mDrawerTitle = getString(R.string.app_name);

//      Setting the shadow that appears at the border of the NavDrawer when it opens (its a Drawable resource)
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
//      Adding items to the NavDrawer, with icons and text
        prepareNavigationDrawerItems();
//      Setting the Header and Adapter for the NavDrawer
        setAdapter();
//      Setting the actions for every item on the NavDrawer
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

//      Initializing the button that opens the NavDrawer (Needs to Sync in onPostCreate Method)
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                if(getSupportActionBar()!=null)
                    getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                if(getSupportActionBar()!=null)
                    getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void prepareNavigationDrawerItems() {
//      Initialize a list to hold the Items
        mDrawerItems = new ArrayList<>();
//      Adding items with icon, text and tag
        mDrawerItems.add(
                new DrawerItem(
                        R.string.icon_drawer_star,
                        R.string.label_drawer_rate,
                        DrawerItem.DRAWER_ITEM_TAG_RATE));
        mDrawerItems.add(
                new DrawerItem(
                        R.string.icon_drawer_help,
                        R.string.label_drawer_help,
                        DrawerItem.DRAWER_ITEM_TAG_HELP));
        mDrawerItems.add(
                new DrawerItem(
                        R.string.icon_drawer_tour,
                        R.string.label_drawer_tour,
                        DrawerItem.DRAWER_ITEM_TAG_TOUR));
        mDrawerItems.add(
                new DrawerItem(
                        R.string.icon_drawer_about,
                        R.string.label_drawer_about,
                        DrawerItem.DRAWER_ITEM_TAG_ABOUT));
    }

    private void setAdapter() {

//      Initializing the Header for the NavDrawer (Inflated from separate XML file)
        View headerView = getLayoutInflater().inflate(R.layout.header_navigation_drawer_1, mDrawerList, false);

//      Initializing the adapter for the list
        BaseAdapter adapter = new DrawerAdapter(this, mDrawerItems);

//      Setting Adapter and Header for the list
//      Header needs to be added first for pre-KitKat APIs
        mDrawerList.addHeaderView(headerView);
        mDrawerList.setAdapter(adapter);
    }

    /*Class to set actions for the NavDrawers items*/
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /*Method that actually has the code of the actions for every NavDrawerItem*/
    private void selectItem(int position) {
//      because we have header, we skip clicking on it
        if (position < 1) {
            return;
        }

//      minus 1 because we have header that has 0 position inside adapter
//      (header counts inside the adapter but was never added to "mDrawerItems")
        int itemTag = mDrawerItems.get(position - 1).getTag();

        /*RATE THIS APP*/
        if(itemTag==DrawerItem.DRAWER_ITEM_TAG_RATE){
            //URL to open market app
            Uri uri = Uri.parse("market://details?id=" + getPackageName()); //Must be called from main package (com.gfsoftware.thespeedtester)
            //Initializing intent
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            /* To count with Play market backstack, After pressing back button,
            * to taken back to our application, we need to add following flags to intent.*/
            //This flag needs to be version checked
            int flagNewDoc = (apiVersion>=21)? Intent.FLAG_ACTIVITY_NEW_DOCUMENT : Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    flagNewDoc |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            //Try launching market activity
            try {
                startActivity(goToMarket);
                //gsManager.sendEvent("Action ST", "Rate App");
            }
            //If market app is not installed (WTF), we open web browser
            catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
            }
        }
        /*QUICK TOUR*/
        else if(itemTag==DrawerItem.DRAWER_ITEM_TAG_TOUR){
            //Show productTour without checking for first time
            showProductTour(false);
            //gsManager.sendEvent("Action ST", "Open Tour");
        }
        else if (itemTag==DrawerItem.DRAWER_ITEM_TAG_ABOUT){
            Intent i = new Intent(SpeedTestActivity.this, AboutActivity.class);
            startActivity(i);
            //gsManager.sendEvent("Action ST", "Open About");
        }
        else if (itemTag==DrawerItem.DRAWER_ITEM_TAG_HELP){
            Intent i = new Intent(SpeedTestActivity.this, HelpActivity.class);
            startActivity(i);
            //gsManager.sendEvent("Action ST", "Open Help");
        }

        //mDrawerList.setItemChecked(position, true);
        //setTitle(mDrawerItems.get(position - 1).getTitle());
        mDrawerLayout.closeDrawer(mDrawerList);
    }


    /*---------------------------------|
    |    DATABASE RELATED METHODS      |
    |----------------------------------*/
    private boolean saveResult(){
        //Get the current progress and time from the Threads
        float progress = downloader.getProgress(); progress = progress / 1024; //Convert to KB
        int taskTimeCount = uiManager.getTime();
        //Calculating speed according to unit
        float speed = unitSelected==0? progress*8 : progress; speed = speed / taskTimeCount;

        //Set result values to the data model
        resultModel.speed = (double) speed; //calculate speed
        resultModel.data = (double) progress;
        resultModel.duration = taskTimeCount;

//      Get current date initializing a GregorianCalendar Object
        GregorianCalendar calendar = new GregorianCalendar();
//      Set timezone to have accurate hour
        calendar.setTimeZone(TimeZone.getDefault());
//      Get formatted date and time strings
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a"); // AM - PM format

//      Set Date and Time values to the data model
        resultModel.date = dateFormat.format(calendar.getTime());
        resultModel.time = timeFormat.format(calendar.getTime());
        resultModel.unit = unitSelected;

//      Execute the save and return the result
        return MyDatabaseManager.insertResult(dbCreator, resultModel);
    }

    /*---------------------------------|
    |    SPEEDTEST RELATED METHODS     |
    |----------------------------------*/
    private void initializePrefs() {
//      Initializing preferences
        prefs = getSharedPreferences("SpeedTestPrefs", SpeedTestActivity.MODE_PRIVATE);
        prefsEditor = prefs.edit();
//      Getting saved duration index
        durationSelected = prefs.getInt("duration", 1);
//      Getting saved data limit index
        dataSelected = prefs.getInt("data", 4);
//      Getting saved data unit index
        unitSelected = prefs.getInt("unit", 0);
//      Checking if user has been prompted with choosing data unit
        choseUnit = prefs.getBoolean("choseUnit", false);

        /*Assigning duration depending on the index*/
        switch (durationSelected){
            case 0:
                duration = 10;
                break;
            case 1:
                duration = 20;
                break;
            case 2:
                duration = 40;
                break;
            case 3:
                duration = 60;
                break;
        }

        /*Assigning data limit depending on the index*/
        switch (dataSelected){
            case 0:
                dataSize = 1;
                break;
            case 1:
                dataSize = 10;
                break;
            case 2:
                dataSize = 50;
                break;
            case 3:
                dataSize = 100;
                break;
            case 4:
                dataSize = 1000;
                break;
        }
}
    private void checkVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            String savedVersion = prefs.getString("version", version);

            if(savedVersion.equals(version)) isUpdate = false;
            else isUpdate = true;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            isUpdate = false;
        }
    }
    private void initializeViews(){
        /*Initializing labels*/
        flConnection = (FontelloTextView) findViewById(R.id.flConnection);
        rbConnection = (RobotoTextView) findViewById(R.id.rbConnectionInfo);
        rbSpeed = (RobotoTextView) findViewById(R.id.rbSpeedInfo);
        rbData = (RobotoTextView) findViewById(R.id.rbData);
        rbInfo = (RobotoTextView) findViewById(R.id.rbInfo);
        rbChartAxisY = (RobotoTextView) findViewById(R.id.label_chart_axisY);
        rbDots = (DotsTextView) findViewById(R.id.dots);

//      Initializing Chart
        chart = (LineChartView) findViewById(R.id.linechart);

        /*Initializing Buttons*/
        btnStart = (RobotoTextView) findViewById(R.id.btnStart);
        btnCancel = (RobotoTextView) findViewById(R.id.btnCancel);
        btnDuration = (FontelloTextView) findViewById(R.id.btnDuration);
        btnData = (FontelloTextView) findViewById(R.id.btnData);

        /*Setting the actions (listeners) for all buttons*/
        btnStart.setOnClickListener(getStartAction());
        btnCancel.setOnClickListener(getCancelAction());
        btnDuration.setOnClickListener(getDurationAction());
        btnData.setOnClickListener(getDataAction());
    }
    private void initializeManagers() {
//      Initializing ChartView and CharManager values
        chartManager = new MyChartManager(this, chart, duration);
//      Show ChartView
        chartManager.show();

//      Initialize DownloadManager (includes download AsyncTask)
        downloader = new MyDownloadManager(SpeedTestActivity.this);
//      Initialize UIManager (includes Timer Thread)
        uiManager = new MyUIManager(SpeedTestActivity.this, downloader, chartManager);

        /*Managers need to communicate, so references to each other are assigned*/
        downloader.uiManager = uiManager;

        /*UI manager needs to access UI components, so references to these are assigned*/
        uiManager.flConnection = flConnection;
        uiManager.rbConnection = rbConnection;
        uiManager.rbSpeed = rbSpeed;
        uiManager.rbData = rbData;
        uiManager.rbInfo = rbInfo;
        uiManager.rbDots = rbDots;
        uiManager.btnStart = btnStart;
        uiManager.btnCancel = btnCancel;
        uiManager.btnDuration = btnDuration;
        uiManager.btnData = btnData;
    }

    /*This method starts (or not) the ProductTourActivity*/
    private void showProductTour(boolean checkFirstTime){
//      Get the value that indicates if its the first time the app has been opened
        boolean firstTime = prefs.getBoolean("first_time", true);
//      Don't do anything if its not the first time AND if the method is supposed to check for first time
        if(!firstTime && checkFirstTime) return;
//      Show Tour
        Intent i = new Intent(SpeedTestActivity.this, ProductTourActivity.class);
        startActivity(i);
    }

    /*This method creates the DURATION dialog, sets the listeners and returns it*/
    private CustomSingleChoiceDialog createDialogDuration(){
//      Creating the dialog
        final CustomSingleChoiceDialog dialog = new CustomSingleChoiceDialog(
                                            //Context for dialog
                                                SpeedTestActivity.this,
                                            //Custom style for dialog
                                                R.style.dlg_single_choice,
                                            //Text values for items in the dialog
                                                new String[]{
                                                        "10 "+getString(R.string.dlg_label_seconds),
                                                        "20 "+getString(R.string.dlg_label_seconds),
                                                        "40 "+getString(R.string.dlg_label_seconds),
                                                        "60 "+getString(R.string.dlg_label_seconds)},
                                            //Selected position index
                                                durationSelected,
                                            //Dialog Title Text
                                                getString(R.string.dlg_title_maxduration),
                                            //Dialog Title Icon
                                                R.string.icon_label_duration
                                                );

        /*Actions to take when an item in the dialog is clicked*/
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long itemId) {
                /*Assigning duration depending on clicked item position*/
                durationSelected = position;
                if (durationSelected == 0) duration = 10;
                else if (durationSelected == 1) duration = 20;
                else if (durationSelected == 2) duration = 40;
                else if (durationSelected == 3) duration = 60;

                /*Saving duration to shared preferences*/
                prefsEditor.putInt("duration", durationSelected);
                prefsEditor.apply(); //Asynchronous

//              Resetting data model values and UI labels
                resetDownloadValues();
//              Resetting Chart UI
                chartManager.resetChart(duration);
//              Resetting Thread values
                uiManager.reset(duration, unitSelected);

//              Changing dialog UI to show item as selected
                customDialogDuration.changeSelectedUI(durationSelected);

//              Closing dialog
                customDialogDuration.dismiss();
            }
        });
        return dialog;
    }

    /*This method creates the DATA LIMIT dialog, sets the listeners and returns it*/
    private CustomSingleChoiceDialog createDialogData() {
//      Creating the dialog
        final CustomSingleChoiceDialog dialog = new CustomSingleChoiceDialog(
                                            //Context for dialog
                                                SpeedTestActivity.this,
                                            //Custom style for dialog
                                                R.style.dlg_single_choice,
                                            //Text values for items in the dialog
                                                new String[]{"1 " + getString(R.string.label_MB),
                                                        "10 " + getString(R.string.label_MB),
                                                        "50 " + getString(R.string.label_MB),
                                                        "100 " + getString(R.string.label_MB),
                                                        "1 " + getString(R.string.label_GB)},
                                            //Selected position index
                                                dataSelected,
                                            //Dialog Title Text
                                                getString(R.string.dlg_title_maxdata),
                                            //Dialog Title Icon
                                                R.string.icon_label_data
        );

        /*Actions to take when an item in the dialog is clicked*/
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long itemId) {
                /*Assigning data limit depending on clicked item position*/
                dataSelected = position;
                if (dataSelected == 0) dataSize = 1;
                else if (dataSelected == 1) dataSize = 10;
                else if (dataSelected == 2) dataSize = 50;
                else if (dataSelected == 3) dataSize = 100;
                else if (dataSelected == 4) dataSize = 1024;

                /*Saving data limit to shared preferences*/
                prefsEditor.putInt("data", dataSelected);
                prefsEditor.apply(); //Asynchronous

//              Changing dialog UI to show item as selected
                customDialogData.changeSelectedUI(dataSelected);

//              Closing dialog
                customDialogData.dismiss();
            }
        });
        return dialog;
    }

    /*This method creates the CHANGE UNIT dialog, sets the listeners and returns it*/
    private CustomSingleChoiceDialog createDialogUnit() {
//      Creating the dialog
        final CustomSingleChoiceDialog dialog = new CustomSingleChoiceDialog(
                //Context for dialog
                SpeedTestActivity.this,
                //Custom style for dialog
                R.style.dlg_single_choice,
                //Text values for items in the dialog
                new String[]{getString(R.string.label_Bits),
                             getString(R.string.label_Bytes)},
                //Selected position index
                unitSelected,
                //Dialog Title Text
                getString(R.string.dlg_title_unit),
                //Dialog Title Icon
                R.string.icon_label_action_unit
        );

        /*Actions to take when an item in the dialog is clicked*/
        dialog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long itemId) {
                /*Assigning data limit depending on clicked item position*/
                unitSelected = position;

                /*Saving data unit to shared preferences*/
                prefsEditor.putInt("unit", unitSelected);
                prefsEditor.apply(); //Asynchronous

//              Resetting data model values and UI labels
                resetDownloadValues();
//              Resetting Chart UI
                chartManager.resetChart(duration);
//              Resetting Thread values
                uiManager.reset(duration, unitSelected);
//                Changing chart Y-axis label
                rbChartAxisY.setText(unitSelected==0?
                        getString(R.string.label_Mb)
                        :getString(R.string.label_MB));

//              Changing dialog UI to show item as selected
                customDialogUnit.changeSelectedUI(unitSelected);

//              Closing dialog
                customDialogUnit.dismiss();
            }
        });
        return dialog;
    }

    /*START BUTTON ACTION, STARTs SPEEDTEST*/
    public View.OnClickListener getStartAction(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If Test in progress stop method
                if(uiManager.isRunning) return;
                //Indicate that Test is in progress
                else uiManager.isRunning = true;

                //Reset data model and UI labels
                resetDownloadValues();

                Log.d("Bit45_TheSpeedTester", "START");
                //Check internet connection
                boolean isConnected = checkConnection();

                /*If not connected, indicate test is not running and return*/
                if(!isConnected) {
                    rbInfo.setText(getString(R.string.label_not_connected));
                    uiManager.isRunning = false;
                    return;
                }

                //gsManager.sendEvent("Action ST", "Start Test");

                /*Disable buttons*/
                btnStart.setEnabled(false);
                btnDuration.setEnabled(false);
                btnData.setEnabled(false);

                /*Start Download and Timer*/
                downloader.startDownload(dataSize);
                uiManager.startDownload(duration , unitSelected);

            }
        };
    }

    /*CANCEL BUTTON ACTION, STOPs SPEEDTEST*/
    public View.OnClickListener getCancelAction(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              If test NOT in progress, RETURN
                if (!uiManager.isRunning) return;

                //gsManager.sendEvent("Action ST", "Stop Test");

                /*Stop background tasks*/
                downloader.cancel();
                uiManager.cancel();
            }
        };
    }

    /*Action for button duration, Opens DURATION dialog*/
    public View.OnClickListener getDurationAction(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogDuration.show();
                //gsManager.sendEvent("Action ST", "Open Duration");
            }
        };
    }
    /*Action for button data limit, Opens DATA dialog*/
    public View.OnClickListener getDataAction(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialogData.show();
                //gsManager.sendEvent("Action ST", "Open Data");
            }
        };
    }

    /*Checks internet connection and updates values and UI*/
    private boolean checkConnection(){
//      Checking internet connection
        boolean isConnected = ConnectivityScanner.isConnected(SpeedTestActivity.this);
//      NOT connected
        if(!isConnected){
            /*Update connection related UI labels*/
            flConnection.setText(getResources().getString(R.string.icon_label_connection_zero));
            rbConnection.setText(getString(R.string.label_not_connected));
            /*Setting data model connection values*/
            resultModel.connection = "No Connection";
            resultModel.network = "None";
            return false;
        }

//      Checking connection TYPE
        boolean isWifi = ConnectivityScanner.isConnectedWifi(SpeedTestActivity.this);

        /*WIFI connection*/
        if(isWifi) {
//          Updating connection label icon
            flConnection.setText(getResources().getString(R.string.icon_label_connection_wifi));
            /*Getting wifi network name*/
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            String netWorkName = wifiManager.getConnectionInfo().getSSID();
//          Updating connection label text
            rbConnection.setText(getString(R.string.label_connection_wifi)+" "+netWorkName);
            /*Setting data model connection values*/
            resultModel.connection = getString(R.string.label_connection_wifi);
            resultModel.network = netWorkName;
        }
        /*MOBILE connection*/
        else {
//          Updating connection label icon
            flConnection.setText(getResources().getString(R.string.icon_label_connection_mobile));
//          Getting mobile connection type
            String connectionType = ConnectivityScanner.getConnectionType(SpeedTestActivity.this);
            /*Getting mobile carrier name*/
            TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            String carrierName = manager.getNetworkOperatorName();
//          Updating connection label text
            rbConnection.setText(connectionType+" \""+carrierName+"\"");
            /*Setting data model connection values*/
            resultModel.connection = connectionType;
            resultModel.network = carrierName;
        }

        return true;
    }

    /*Manages toasts so they wont stack up*/
    private void showToast(int strResId){
//      Create toast if doesn't exist
        if (mToast == null)
            mToast = Toast.makeText(this, getString(strResId), Toast.LENGTH_SHORT);
//      Changes toast text
        else mToast.setText(getString(strResId));
//      show toast immediately regardless of a previous one being showed (doesn't stack)
        mToast.show();
    }

    private void resetDownloadValues(){
        rbSpeed.setText(R.string.label_variable_zero);
        rbData.setText(R.string.label_variable_zero);
        resultModel.reset();
    }
}
