package com.bit45.thespeedtester.Database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Data model for the Database table 'TestResults'
 */
public class TestResultModel {

    public int id = -1;
    public String connection;
    public String network;
    public double speed;
    public double data;
    public int duration;
    public String date;
    public String time;
    public int unit;

    public TestResultModel(){
        id = -1;
        connection = "No Info";
        network = "No Info";
        speed = 0.0;
        data = 0.0;
        duration = 0;
        date = "null";
        time = "null";
        unit = 0;
    }

    public TestResultModel(Cursor c){
        if(c==null) {
            reset();
            return;
        }
        id = c.getInt(c.getColumnIndex("_id"));
        connection = c.getString(c.getColumnIndex("connection"));
        network = c.getString(c.getColumnIndex("network"));
        speed = c.getDouble(c.getColumnIndex("speed"));
        data = c.getDouble(c.getColumnIndex("data"));
        duration = c.getInt(c.getColumnIndex("duration"));
        date = c.getString(c.getColumnIndex("date"));
        time = c.getString(c.getColumnIndex("time"));
        unit = c.getInt(c.getColumnIndex("unit"));
    }

    public void reset(){
        id = -1;
        connection = "No Info";
        network = "No Info";
        speed = 0.0;
        data = 0.0;
        duration = 0;
        date = "null";
        time = "null";
        unit = 0;
    }

    public ContentValues getValues(boolean withID){
        ContentValues values = new ContentValues();
        if(withID) values.put("_id", id);
        values.put("connection", connection);
        values.put("network", network);
        values.put("speed", speed);
        values.put("data", data);
        values.put("duration", duration);
        values.put("date", date);
        values.put("time", time);
        values.put("unit", unit);
        return values;
    }

}
