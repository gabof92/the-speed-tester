package com.bit45.thespeedtester.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bit45.thespeedtester.Database.DatabaseCreator;
import com.bit45.thespeedtester.Database.TestResultModel;
import com.bit45.thespeedtester.R;

import java.text.DecimalFormat;

/**
 * This class handles database related methods
 * such as CRUD methods and formatting data model values
 */
public class MyDatabaseManager {

    /*INSERT ONE RECORD*/
    public static boolean insertResult(DatabaseCreator dbHelper, TestResultModel model){

        //We get a reference to the database in Write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //Check if we got a valid reference
        if(db != null)
        {
            //Insert the record with the values in the data model
            model.id = (int) db.insert("TestResults", null, model.getValues(false));

            //Release the reference to the database
            db.close();
        }

        return true;
    }

    /*DELETE ONE RECORD*/
    public static int deleteResult(DatabaseCreator dbHelper, int id) {
        //We get a reference to the database in Write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int affectedRows = 0;
        //Check if we got a valid reference
        if(db != null)
        {
            //Delete the record that contains that id
            affectedRows = db.delete("TestResults", "_id=?", new String[]{""+id});

            //Release the reference to the database
            db.close();
        }

        //return number of records deleted (should be 1)
        return affectedRows;
    }

    /*This method gets a date in 'yyyy-mm-dd' format and converts it to '22 NOVEMBER 1992' format*/
    public static String getFormattedLabelDate(Context context, String date) {
        if(date==null) return "No date";
        if(date.equals("null")) return "No date";
        String splitDate[] = date.split("-");
        if(splitDate.length<3) return "No date";

        String labelMonth = splitDate[1];
        switch (splitDate[1]) {
            case "01":
                labelMonth = context.getString(R.string.label_month_january);
                break;
            case "02":
                labelMonth = context.getString(R.string.label_month_february);
                break;
            case "03":
                labelMonth = context.getString(R.string.label_month_march);
                break;
            case "04":
                labelMonth = context.getString(R.string.label_month_april);
                break;
            case "05":
                labelMonth = context.getString(R.string.label_month_may);
                break;
            case "06":
                labelMonth = context.getString(R.string.label_month_june);
                break;
            case "07":
                labelMonth = context.getString(R.string.label_month_july);
                break;
            case "08":
                labelMonth = context.getString(R.string.label_month_august);
                break;
            case "09":
                labelMonth = context.getString(R.string.label_month_september);
                break;
            case "10":
                labelMonth = context.getString(R.string.label_month_october);
                break;
            case "11":
                labelMonth = context.getString(R.string.label_month_november);
                break;
            case "12":
                labelMonth = context.getString(R.string.label_month_december);
                break;
        }

        //Should return something like '2 SEPTEMBER 2015'
        return splitDate[2] + " " + labelMonth + " " + splitDate[0];
    }

    /*This method gets a date in 'yyyy-mm-dd' format and converts it to '22 NOV 1992' format*/
    public static String getFormattedLabelDateShort(Context context, String date) {
        if(date==null) return "No date";
        if(date.equals("null")) return "No date";
        String splitDate[] = date.split("-");
        if(splitDate.length<3) return "No date";

        String labelMonth = splitDate[1];
        switch (splitDate[1]) {
            case "01":
                labelMonth = context.getString(R.string.label_month_january_short);
                break;
            case "02":
                labelMonth = context.getString(R.string.label_month_february_short);
                break;
            case "03":
                labelMonth = context.getString(R.string.label_month_march_short);
                break;
            case "04":
                labelMonth = context.getString(R.string.label_month_april_short);
                break;
            case "05":
                labelMonth = context.getString(R.string.label_month_may_short);
                break;
            case "06":
                labelMonth = context.getString(R.string.label_month_june_short);
                break;
            case "07":
                labelMonth = context.getString(R.string.label_month_july_short);
                break;
            case "08":
                labelMonth = context.getString(R.string.label_month_august_short);
                break;
            case "09":
                labelMonth = context.getString(R.string.label_month_september_short);
                break;
            case "10":
                labelMonth = context.getString(R.string.label_month_october_short);
                break;
            case "11":
                labelMonth = context.getString(R.string.label_month_november_short);
                break;
            case "12":
                labelMonth = context.getString(R.string.label_month_december_short);
                break;
        }

        return splitDate[2] + " " + labelMonth + " " + splitDate[0];
    }

    /*This method gets a float value and converts it to '0.00 KB/s' or '0.00 MB/s' format*/
    public static String getFormattedSpeed(Context context, double speed, int unit, int unitSelected){

        if(unit!=unitSelected){
            if(unitSelected==0) speed *= 8;
            else speed /= 8;
        }
        String speedUnit = unitSelected==0?
                context.getString(R.string.label_variable_Kbps)
                :context.getString(R.string.label_variable_KBps);
        if(speed>=1024){
            speedUnit = unitSelected==0?
                    context.getString(R.string.label_variable_Mbps)
                    :context.getString(R.string.label_variable_MBps);
            speed /= 1024;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(speed) + " " + speedUnit;
    }

    /*This method gets a float value and converts it to '0.00 KB' or '0.00 MB' format*/
    public static String getFormattedData(Context context, double data) {
        String dataUnit = context.getString(R.string.label_KB);
        if(data>=1024){
            dataUnit = context.getString(R.string.label_MB);
            data /= 1024;
        }
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(data) + " " + dataUnit;
    }

    /*This method recieves a string and returns the resource id
    * for the string referencing either the WIFI or SIGNAL icon*/
    public static int getResConnection(String connection) {
        return connection.equalsIgnoreCase("wifi")? R.string.icon_label_connection_wifi : R.string.icon_label_connection_mobile;
    }

}
