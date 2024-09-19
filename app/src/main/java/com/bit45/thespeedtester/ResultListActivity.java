package com.bit45.thespeedtester;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.bit45.thespeedtester.Database.DatabaseCreator;
import com.bit45.thespeedtester.ListViews.TestResultListAdapter;
import com.bit45.thespeedtester.ListViews.TestResultItem;
import com.bit45.thespeedtester.managers.GoogleServicesManager;
import com.bit45.thespeedtester.managers.MyDatabaseManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;

import java.util.ArrayList;

public class ResultListActivity extends AppCompatActivity {

    public static final int DETAIL_RESULT_REQUEST = 1992;

    SharedPreferences prefs;
    int unitSelected;

    SQLiteDatabase db;
    DatabaseCreator dbHelper;
    Cursor c = null;

    private GoogleServicesManager gsManager;

    private DynamicListView mDynamicListView;
    private TestResultListAdapter listAdapter;

    boolean cancelThread = false;

    /*Toast*/
    private Toast mToast = null;
    private ArrayList<TestResultItem> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_list);

//      initialize Google services manager (used for Admob and Google Analytics)
        //gsManager = new GoogleServicesManager(this);
//      initialize G-Analytics Tracker
        //gsManager.initializeTracker();
//      initialize AdMob Banner
        //gsManager.initializeBanner(R.id.adView);

//      Initializing preferences
        prefs = getSharedPreferences("SpeedTestPrefs", SpeedTestActivity.MODE_PRIVATE);
        unitSelected = prefs.getInt("unit", 0);

//      Getting an XML toolbar instead of the default actionbar
//      this is done to support older API's that didn't have actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//      set the inflated toolbar as the support actionbar
        setSupportActionBar(toolbar);

        //Display the home button in the actionbar
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Initializing ListView from XML
        mDynamicListView = (DynamicListView) findViewById(R.id.dynamic_listview);

        //Assign an animation and adapter to the list
        //And the item click listener
        setUpListView();

    }

    @Override
    protected void onStart() {
        //CALLED AFTER onCreate BUT BEFORE HAVING ACCESS TO THE UI
        super.onStart();
        //gsManager.reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        //APP HAS OFFICIALLY LEFT THE SCREEN
        super.onStop();
        //gsManager.reportActivityStop(this);
    }

    @Override
    protected void onDestroy() {
        cancelThread = true;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_result_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Home button in the action bar closes the activity
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**This method handles the case where an activity (started for result by this activity) finishes and returns a result;
     * this activity is listening for ResultDetailActivity, if it returns an id it means the user deleted a record*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to (ResultDetailActivity)
        if (requestCode == DETAIL_RESULT_REQUEST) {
            // Make sure the request was successful (The user deleted a record)
            if (resultCode == RESULT_OK) {
                //Get the id of the deleted record
                Long id = data.getLongExtra("id", -1);
                //Remove item from ListView
                listAdapter.remove(listAdapter.getPositionById(id));
                //Notify user
                showToast(R.string.test_toast_delete_success);
            }
        }
    }

    private void setUpListView() {
        /*We run the database code outside the main thread*/
        new Thread(new Runnable() {
            @Override
            public void run() {
//              Get data from database
                loadData();

                if(cancelThread) return;

//              Create itemList for listView
                getItemList();

                if(cancelThread) return;

                /*we go back to the main thread to update the UI*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*If there are no results in the database*/
                        if(itemList.size()==0){
                            /*Make dots invisible*/
                            View dots = findViewById(R.id.dots);
                            dots.setVisibility(View.GONE);
                            /*Change loading text*/
                            TextView loadingText = (TextView) findViewById(R.id.rbInfo);
                            loadingText.setText(getString(R.string.label_info_noresults));
                        }
                        else{
                            /*Make info layout invisible*/
                            View loadingLayout = findViewById(R.id.layout_loading);
                            loadingLayout.setVisibility(View.GONE);
    //                      Make listview visible
                                mDynamicListView.setVisibility(View.VISIBLE);
    //                      SetUp ListView
                                appearanceAnimate(4);
    //                      Setup item listeners
                                setItemListeners();
                        }
                    }
                });
            }
        }).start();
    }

    private void loadData(){
        //Open the database 'DBTester'
        dbHelper = new DatabaseCreator(ResultListActivity.this, "DBTester", null);

        //get object used to execute queries
        db = dbHelper.getReadableDatabase();

        //Execute a query to get ALL test results
        c = db.rawQuery("SELECT * FROM TestResults ORDER BY _id DESC", null);

        //Make sure we got at least one record from the query
        if (!c.moveToFirst()) c = null;
    }

    private void getItemList() {
        ArrayList<TestResultItem> list = new ArrayList<>();

        if(c==null){
            itemList = list;
            return;
        }

        do {
            int id = c.getInt(c.getColumnIndex("_id"));
            String connection  = c.getString(c.getColumnIndex("connection"));
            String network  = c.getString(c.getColumnIndex("network"));
            double speed  = c.getDouble(c.getColumnIndex("speed"));
            double data  = c.getDouble(c.getColumnIndex("data"));
            int duration = c.getInt(c.getColumnIndex("duration"));
            String date = c.getString(c.getColumnIndex("date"));
            String time = c.getString(c.getColumnIndex("time"));
            int unit = c.getInt(c.getColumnIndex("unit"));

            int iconResConnection = MyDatabaseManager.getResConnection(connection);

            String labelSpeed = MyDatabaseManager.getFormattedSpeed(this, speed, unit, unitSelected);
            String labelData = MyDatabaseManager.getFormattedData(this, data);
            String labelDuration = duration + getString(R.string.label_list_item_seconds);
            String labelDate = MyDatabaseManager.getFormattedLabelDateShort(this, date);

            list.add(new TestResultItem(
                    id,
                    iconResConnection,
                    connection,
                    network,
                    labelSpeed,
                    labelData,
                    labelDuration,
                    labelDate,
                    time));
        }while (c.moveToNext());
        //Release the reference to the database and cursor
        c.close();
        db.close(); //NEVER use cursor after db is closed

        itemList = list;
    }

    private void appearanceAnimate(int key) {
        listAdapter = new TestResultListAdapter(this, itemList);
        AnimationAdapter animAdapter;
        switch (key) {
            default:
            case 0:
                animAdapter = new AlphaInAnimationAdapter(listAdapter);
                break;
            case 1:
                animAdapter = new ScaleInAnimationAdapter(listAdapter);
                break;
            case 2:
                animAdapter = new SwingBottomInAnimationAdapter(listAdapter);
                break;
            case 3:
                animAdapter = new SwingLeftInAnimationAdapter(listAdapter);
                break;
            case 4:
                animAdapter = new SwingRightInAnimationAdapter(listAdapter);
                break;
        }
        animAdapter.setAbsListView(mDynamicListView);
        mDynamicListView.setAdapter(animAdapter);
    }

    private void setItemListeners(){
        /*Set the click action for the items in the list (open a ResultDetailActivity)*/
        mDynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(ResultListActivity.this, ResultDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putLong("id", id);
                i.putExtras(extras);
                //Start for result so we can handle the case where the user deletes the record (see onActivityResult method)
                startActivityForResult(i, DETAIL_RESULT_REQUEST);
                //gsManager.sendEvent("Action RL", "Open Result Detail");
            }
        });
    }

    private void showToast(int strResId){
        if (mToast == null)
            mToast = Toast.makeText(this, getString(strResId), Toast.LENGTH_SHORT);

        else mToast.setText(getString(strResId));

        mToast.show();
    }
}
