package edu.illinois.jchen93.bitstampapiandroid3;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;


public class TransactionReceiver extends BroadcastReceiver{
	public static final String TAG = "TransactionReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent){
		//String intentServiceType = intent.getData().toString();
    	//Log.i(TAG, "this is "+intentServiceType);
		ArrayList<Transaction> s = intent.getParcelableArrayListExtra(TransactionUpdateService.TRANSACTION_RESULT);            
		if (s==null){ Log.i(TAG, "broadcast received null, failed broadcast");}
		else{
			Log.i(TAG, "transaction broadcast receive correctly, arraylist size is: "+Integer.toString(s.size()));
			plotTransaction(s);
		}
	}
	
	private void plotTransaction(ArrayList<Transaction> transactionArray){
		XYPlot plot1 = (XYPlot) MainActivity.class.getRootView().findViewById(R.id.chart);
		plot1.clear();
		
		int n = transactionArray.size();
		Log.i(TAG, "ploting transaction size is: "+n);
		Number[] time = new Number[n];
		Number[] y = new Number[n];
		int i = 0;
		for(Transaction temp : transactionArray){
			time[i] = Long.parseLong(temp.getDate());
			y[i] = Double.parseDouble(temp.getPrice());
			i++;
		}

		XYSeries series = new SimpleXYSeries(Arrays.asList(time),Arrays.asList(y),"Transactions");
		
		plot1.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK);
        plot1.getGraphWidget().getDomainGridLinePaint().setColor(Color.WHITE);
        plot1.getGraphWidget().getDomainGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot1.getGraphWidget().getRangeGridLinePaint().setColor(Color.WHITE);
        plot1.getGraphWidget().getRangeGridLinePaint().
                setPathEffect(new DashPathEffect(new float[]{1, 1}, 1));
        plot1.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot1.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);

        // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter format = new LineAndPointFormatter(
                Color.RED,                  // line color
                Color.rgb(0, 100, 0),       // point color
                null, null);                // fill color
        
        plot1.getGraphWidget().setPaddingRight(2);
        plot1.addSeries(series, format);

        // draw a domain tick for each time:
        //plot1.setDomainStep(XYStepMode.SUBDIVIDE, time.length/400);
        plot1.setDomainStepValue(10);
        plot1.setRangeStepValue(9);

        // customize our domain/range labels
        plot1.setDomainLabel("Time");
        plot1.setRangeLabel("Price");      
        
        plot1.setDomainValueFormat(new Format() {

            // create a simple date format that draws on the year portion of our timestamp.
            // see http://download.oracle.com/javase/1.4.2/docs/api/java/text/SimpleDateFormat.html
            // for a full description of SimpleDateFormat.
            private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {

                // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
                // we multiply our timestamp by 1000:
                long timestamp = ((Number) obj).longValue() * 1000;
                Date date = new Date(timestamp);
                return dateFormat.format(date, toAppendTo, pos);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;

            }
        });
       
        plot1.getGraphWidget().getDomainLabelPaint().setTextSize(15);
        plot1.getGraphWidget().getRangeLabelPaint().setTextSize(15);
        //plot1.getGraphWidget().setDomainLabelOrientation(-20);
        //plot1.getGraphWidget().setRangeLabelOrientation(20);
        plot1.getGraphWidget().setPaddingRight(25);
        plot1.getGraphWidget().setPaddingTop(6);
        
        plot1.getGraphWidget().setSize(new SizeMetrics(70, SizeLayoutType.FILL, 50, SizeLayoutType.FILL));

        plot1.redraw();
        plot1.setVisibility(1);
        plot1.bringToFront();
	}
}