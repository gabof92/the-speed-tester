package com.bit45.thespeedtester;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bit45.thespeedtester.Database.DatabaseCreator;
import com.bit45.thespeedtester.Database.TestResultModel;
import com.bit45.thespeedtester.managers.GoogleServicesManager;
import com.bit45.thespeedtester.managers.MyDatabaseManager;

/**
 * This class is used to show all the data of a result saved in the database
 * it only shows ONE test result and looks like a dialog but it is a full activity*/
public class ResultDetailActivity extends Activity {

    SQLiteDatabase db;
    DatabaseCreator dbHelper;
    TestResultModel model;
    private GoogleServicesManager gsManager;
    SharedPreferences prefs;
    int unitSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //remove actionbar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_detail);

//      initialize Google services manager (used for Admob and Google Analytics)
        //gsManager = new GoogleServicesManager(this);
//      initialize G-Analytics Tracker
        //gsManager.initializeTracker();

//      Initializing preferences
        prefs = getSharedPreferences("SpeedTestPrefs", SpeedTestActivity.MODE_PRIVATE);
        unitSelected = prefs.getInt("unit", 0);

        //Get the record id from the intent extras (was put there by the previous activity)
        Bundle extras = getIntent().getExtras();
        long id = extras.getLong("id", -1);

        //Open the database 'DBTester'
        dbHelper = new DatabaseCreator(ResultDetailActivity.this, "DBTester", null);

        //get object used to execute queries
        db = dbHelper.getReadableDatabase();

        //Execute a query to get the test result
        Cursor c = db.rawQuery("SELECT * FROM TestResults WHERE _id = '"+id+"'", null);

        //Make sure we got a record from the query
        if (!c.moveToFirst()) c = null;

        //Dump all the cursor info into our data model
        //(if its null it will assign default values)
        model = new TestResultModel(c);

        //Release the reference to the database record
        if (c != null) c.close();
        //Release the reference to the database
        db.close(); //NEVER use cursor after db is closed

        /*Put model data into local variables*/
        String connection  = model.connection;
        String network  = model.network;
        double speed  = model.speed;
        double data  = model.data;
        int duration = model.duration;
        String date = model.date;
        String time = model.time;
        int unit = model.unit;

        //Getting connection icon depending on connection type
        int iconResConnection = MyDatabaseManager.getResConnection(connection);

        /*Formatting speed, data, duration and date values*/
        String labelSpeed = MyDatabaseManager.getFormattedSpeed(this, speed, unit, unitSelected);
        String labelData = MyDatabaseManager.getFormattedData(this, data);
        String labelDuration = duration +" "+ getString(R.string.dlg_label_seconds);
        String labelDate = MyDatabaseManager.getFormattedLabelDate(this, date);

        /*Assigning text values to the actual labels*/
        setLabelText(R.id.rbNetworkInfo, network);
        setLabelText(R.id.icon_connection, getString(iconResConnection));
        setLabelText(R.id.rbConnectionInfo, connection);
        setLabelText(R.id.rbSpeedInfo, labelSpeed);
        setLabelText(R.id.rbDownloadedInfo, labelData);
        setLabelText(R.id.rbDurationInfo, labelDuration);
        setLabelText(R.id.rbDateInfo, labelDate);
        setLabelText(R.id.rbTimeInfo, time);

        /*Assigning the action for the OK button of the dialog*/
        View btnDone = findViewById(R.id.result_detail_btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResultDetailActivity.this.setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        /*Assigning the action for the DELETE button of the dialog*/
        View btnDelete = findViewById(R.id.result_detail_btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Delete record from the database
                MyDatabaseManager.deleteResult(dbHelper, model.id);

                /*Putting the id of the deleted record as the result of the activity*/
                Bundle resultData = new Bundle();
                resultData.putLong("id", model.id);
                Intent i = new Intent();
                i.putExtras(resultData);
                ResultDetailActivity.this.setResult(Activity.RESULT_OK, i);

                //gsManager.sendEvent("Action RD", "Delete Record");
                //closing activity
                finish();
            }
        });

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

    private void setLabelText(int resId, String text) {
        TextView label = (TextView) findViewById(resId);
        label.setText(text);
    }


}
