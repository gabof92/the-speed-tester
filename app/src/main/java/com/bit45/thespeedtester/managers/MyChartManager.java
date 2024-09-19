package com.bit45.thespeedtester.managers;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.Log;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.elastic.ElasticEaseOut;
import com.db.chart.view.animation.style.DashAnimation;
import com.bit45.thespeedtester.R;

import java.text.DecimalFormat;

/**
 * This class handles most of the chart related code
 * from chart creation and setup, to data updates
 */
public class MyChartManager {

    //Object representation of the XML chart
    private LineChartView chart;

    //Straight line that joins Circles
    //(represents download for every second)
    private LineSet dataSet;

    //Dashed line with animation
    //(represents the average speed)
    private LineSet dataSetAvg;

    //The "brush" that paints the grid lines in the chart
    private Paint gridPaint;
    //Labels at the X axis of the chart (seconds)
    private String labels[];
    //Values for the Y axis (for dataSet)
    private float valuesCurrent[];
    //Values for the Y axis (for dataSetAvg)
    private float valuesAvg[];
    //Activity where the chart is showed
    private Context activity;
    //Min value for Y axis
    int minY = 0;
    //Max value for X axis
    int maxY = 1;
    //Number of values between labeled values in Y axis
    int step = 1;
    //Number of X axis values, (duration of the test)
    int maxX = 10;
    private Animation anim;

    /*Constructor*/
    public MyChartManager(Context activity, LineChartView chart, int duration){
        this.chart = chart;
        this.activity = activity;

        maxX = duration;

        /*Setting up the 'brush' that paints the grid*/
        gridPaint = new Paint();
        gridPaint.setColor(activity.getResources().getColor(R.color.line_grid));
        gridPaint.setPathEffect(new DashPathEffect(new float[] {5,5}, 0));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.75f));
        //Setup dataset appearance animations
        initializeAnimation();
        //Setup chart values and UI
        initializeChart();
        //Setup values and UI for datasets
        initializeDataSet(maxX);
    }

    /*Sets up animation parameters that all datasets in the chart will follow*/
    private void initializeAnimation(){
        anim = new Animation()
                //Datasets will NOT (-1) fade in
                .setAlpha(-1)
                //Type of motion, elasticEaseOut bounces a little bit
                .setEasing(new ElasticEaseOut())
                //not sure?
                .setOverlap(1)
                //Determines where the datasets come from (bottom-left corner)
                .setStartPoint(0, 0);
    }

    /*Sets parameters that define the UI of the chart*/
    private void initializeChart(){
        chart.setBorderSpacing(Tools.fromDpToPx(4))
                //Assign the 'brush' for the grid
                .setGrid(LineChartView.GridType.FULL, gridPaint)
                //Show X axis line
                .setXAxis(true)
                //Position X axis labels outside of the chart (under the line)
                .setXLabels(XController.LabelPosition.OUTSIDE)
                //Show Y axis line
                .setYAxis(true)
                //Position Y axis labels outside of the char (left of the line)
                .setYLabels(YController.LabelPosition.OUTSIDE)
                //Set the bottom and top values for Y axis and the space
                .setAxisBorderValues(minY, maxY, step)
                //Format for Y axis labels (only numbers, at least 2 digits)
                .setLabelsFormat(new DecimalFormat("##"))
                //Set font size for axis
                .setFontSize(activity.getResources().getDimensionPixelSize(R.dimen.linechart_text));
    }

    /*Sets the values, labels and UI parameters for both datasets (Lines) of the chart*/
    private void initializeDataSet(int dataSize){
        maxX = dataSize;

        /*Both datasets will have as many values as the duration of the test*/
        valuesCurrent = new float[maxX];
        valuesAvg = new float[maxX];

        //Same for the labels
        labels = new String[maxX];

        /*Assigning the labels for the X axis*/
        for (int i = 0; i < valuesCurrent.length; i++) {
            int length = valuesCurrent.length;

            /*Assign 0 to all Y values of the datasets*/
            valuesCurrent[i] = 0;
            valuesAvg[i] = 0;

            //+1 because label at position 0 represents the 1st second
            int index = i+1;

            /*If there are 10 values we assign a label to each value*/
            if(length<=10)
                labels[i] = (index) + "";
            /*If there are 20 values we assign a label every 2 values*/
            else if(length==20 && (index%2==0))
                labels[i] = (index) + "";
            /*If there are 20 values we assign a label every 5 values*/
            else if(length==40 && (index%5==0))
                labels[i] = (index) + "";
            /*If there are 20 values we assign a label every 10 values*/
            else if(length==60 && (index%10==0))
                labels[i] = (index) + "";
            else
                labels[i] = "";
        }

        /*Initializing datasets*/
        dataSet = new LineSet();
        dataSetAvg = new LineSet();

        /*Assign X values (labels) and Y values (numbers) to datasets*/
        dataSet.addPoints(labels, valuesCurrent);
        dataSetAvg.addPoints(labels, valuesAvg);

        /*UI values for the 'circles' of the first dataset
        * and for the line joining those circles*/
        //radius of the circle
        int radius = activity.getResources().getDimensionPixelSize(R.dimen.linechart_big_radius);
        //width of the line surrounding the circles (the border)
        int stroke = activity.getResources().getDimensionPixelSize(R.dimen.linechart_big_stroke);
        //width of the line that joins the dots
        int thickness = activity.getResources().getDimensionPixelSize(R.dimen.linechart_big_thickness);
        /*Change the size of circles and lines depending on
        * how many values have to fit in the chart*/
        if(dataSize>20){
            radius = activity.getResources().getDimensionPixelSize(R.dimen.linechart_small_radius);
            stroke = activity.getResources().getDimensionPixelSize(R.dimen.linechart_small_stroke);
            thickness = activity.getResources().getDimensionPixelSize(R.dimen.linechart_small_thickness);
        }

        /*Dataset parameters*/
                //Indicate that the line will have dots (circles) where the X and Y values meet,
                //instead of just showing a line going through those points
        dataSet.setDots(true)
                //fill color for the circles
                .setDotsColor(activity.getResources().getColor(R.color.line_bg))
                //size of the circles
                .setDotsRadius(radius)
                //size of the border of the circles
                .setDotsStrokeThickness(stroke)
                //color of the border of the circles
                .setDotsStrokeColor(activity.getResources().getColor(R.color.dot_line))
                //color of the line that joins the circles
                .setLineColor(activity.getResources().getColor(R.color.line))
                //size of the line that joins the circles
                .setLineThickness(thickness)
                //This defines what values (circles) are showing in the chart
                //at the beginning  ony the first value is showing (at the start point)
                .beginAt(0).endAt(1);

                //Color for the dashed line
        dataSetAvg.setLineColor(activity.getResources().getColor(R.color.line))
                //Size of dashed line
                .setLineThickness(thickness)
                //Indicates that the line wont go straight from point to point,
                //instead it will curve smoothly depending on the trajectory
                .setSmooth(true)
                //Indicate the line will be dashed
                .setDashed(true)
                //Same as before, only the first value is shown
                //(since its just a line it doesn't really show anything)
                .beginAt(0).endAt(1);

        /*Adding datasets to the chart*/
        chart.addData(dataSet);
        chart.addData(dataSetAvg);
        //Animating the dashed line
        chart.animateSet(1, new DashAnimation());
    }

    /*Resets the chart to its initial state*/
    public void resetChart(int dataSize){
        //Discard datasets
        chart.reset();
        //Create new datasets
        initializeDataSet(dataSize);
        maxY = 1;
        step = 1;
        //Initialize chart parameter again
        initializeChart();
        //Animation must be initialized again with the new data size
        //or it will crash if the new size is larger than previous one
        initializeAnimation();
        //Show chart with reinitialized animation
        chart.show(anim);
    }

    /*Self explaining code*/
    public void show(){
        chart.show(anim);
    }

    /*Adds a new value to both datasets and updates chart UI*/
    public boolean addValue(float newValue, float newValueAvg){

        //Preventing NullPointerException (that shouldn't happen anyway)
        if(dataSet==null) return false;
        //Preventing IndexOutOfBoundsException (also shouldn't happen)
        if (dataSet.getEnd()== valuesCurrent.length) return false;

        //Logging new values (for debug)
        Log.d("Bit45_TheSpeedTester", "ADD VALUE " + newValue + " | " + newValueAvg);

        int end = dataSet.getEnd();
        /*If it is the first value, just change the values on the arrays
        * because the circle of the first value is already showing*/
        if(valuesCurrent[0]==0) {
            valuesCurrent[0] = newValue;
            valuesAvg[0] = newValueAvg;
        }
        /*If it isn't the first value we change the end of both datasets
        * so they will show one more circle and we change the array values*/
        else {
            dataSet.endAt(end + 1);
            dataSetAvg.endAt(end + 1);
            valuesCurrent[end] = newValue;
            valuesAvg[end] = newValueAvg;
        }

        /*we pass the arrays with the new values to the chart (for each dataset)*/
        chart.updateValues(0, valuesCurrent);
        chart.updateValues(1, valuesAvg);

        /*If the new value is higher than the top of the Y axis,
        * we need to set it higher but still close to the new value*/
        if (maxY <= newValue){
            //top border will be raised at least 30% higher than the new value
            int auxBorderRaise = (int)(newValue*0.3);
            //since its an integer we must make sure its not less than 1
            if (auxBorderRaise<1) auxBorderRaise = 1;
            //Raise the top of the Y axis
            maxY =  (int) newValue + auxBorderRaise;

            //We make the top axis a multiple of 10 to make it easier
            // to find an appropriate step between 0 and that number
            roundMaxY();

            //step is 10% the value of top border (that's why we made it multiple of 10)
            step = (int)(maxY *0.1);

            /*Making sure it meets williamchart library's
            * requirements to be a valid step value*/
            if(step==0) step = 1;
            calculateStep(minY, maxY);

            //Set the new Y axis values
            chart.setAxisBorderValues(minY, maxY, step);
            //Call show to update chart UI
            chart.show();
        }
        //Notify data change to update chart UI
        chart.notifyDataUpdate();
        return true;
    }

    private void roundMaxY() {
        while(maxY % 5 != 0) maxY++;
    }

    private void calculateStep(int minValue, int maxValue){
        if((maxValue - minValue) % step == 0) return;
        while ((maxValue - minValue) % step != 0) step++;
    }

}
