package edu.illinois.jchen93.bitstampapiandroid3;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TransactionFragment extends Fragment{
	
	private final static String TAG="TransactionFragment";
	private AlarmManager alarmMgr;
	private BroadcastReceiver receiver;
	private int REQUEST_CODE = 101;

	public TransactionFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		Log.i(TAG, "on createView");
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }
	
	@Override
	public void onStart(){
		super.onStart();
		Log.i(TAG, "on start");

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), new IntentFilter(TransactionUpdateService.TRANSACTION_RESULT));		
		
		String CHOICE = "0";
		Intent intent = new Intent(getActivity(), TransactionUpdateService.class);
		intent.setData(Uri.parse(CHOICE));
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(), REQUEST_CODE, intent, 0);
        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 2500, pendingIntent);
        
        receiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	        	//String intentServiceType = intent.getData().toString();
	        	//Log.i(TAG, "this is "+intentServiceType);
        		ArrayList<Transaction> s = intent.getParcelableArrayListExtra(TransactionUpdateService.TRANSACTION_RESULT);            
        		if (s==null){ Log.i(TAG, "broadcast received null, failed broadcast");}
        		else{
        			Log.i(TAG, "transaction broadcast receive correctly, arraylist size is: "+Integer.toString(s.size()));
        			plotTransaction(s);
        		}	        	
	        }
	    };
	}
	
	
	@Override
	public void onStop() {
    	super.onStop();
        
    	LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
    	
        if (alarmMgr != null)
        {Log.i(TAG, "on stop");
        String CHOICE = "0";
        Intent intent = new Intent(getActivity(), TransactionUpdateService.class);
        intent.setData(Uri.parse(CHOICE));
        //pendingIntent.cancel();
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), REQUEST_CODE, intent, 0);
        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(pendingIntent);}
    }
	
	
	private void plotTransaction(ArrayList<Transaction> transactionArray){
		XYPlot plot1 = (XYPlot) getView().findViewById(R.id.chart);
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