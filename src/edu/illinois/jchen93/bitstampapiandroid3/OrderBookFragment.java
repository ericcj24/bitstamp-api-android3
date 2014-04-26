package edu.illinois.jchen93.bitstampapiandroid3;

import java.util.ArrayList;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.graphics.Color;
import android.graphics.DashPathEffect;


public class OrderBookFragment extends Fragment{
	private final static String TAG="OrderBookActivity";
	
	private AlarmManager alarmMgr;
	private BroadcastReceiver receiver;
	private int REQUEST_CODE = 102;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		Log.i(TAG, "on start");
		
		LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(OrderBookUpdateService.ORDERBOOK_RESULT));
		
		String CHOICE = "1";
		Intent intent = new Intent(OrderBookFragment.this, OrderBookUpdateService.class);
		intent.setData(Uri.parse(CHOICE));
		PendingIntent pendingIntent = PendingIntent.getService(OrderBookFragment.this, REQUEST_CODE, intent, 0);
		alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
	    alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 7000, pendingIntent);
	    
	    receiver = new BroadcastReceiver(){
	    	@Override
	    	public void onReceive(Context context, Intent intent){
	    		//String intentAction = intent.getAction();
	        	//Log.i(TAG, "this is "+intentAction);
	    		//if(intentAction == OrderBookUpdateService.ORDERBOOK_RESULT){		  
	        		ArrayList<Price_Amount> s = intent.getParcelableArrayListExtra(OrderBookUpdateService.ORDERBOOK_RESULT);   
	        		if (s==null){ Log.i(TAG, "order book broadcast received null, failed broadcast");}
	        		else{
	        			Log.i(TAG, "orderbook broadcast receive correctly, arraylist size is: "+Integer.toString(s.size()));
	        			plotOrderBook(s);
	        		}
	        	//}
	    	}
	    };
	}
	
	@Override
    protected void onStop() {
    	super.onStop();
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    	
        if (alarmMgr != null)
        {Log.i(TAG, "on stop");
        String CHOICE = "1";
        Intent intent = new Intent(OrderBookFragment.this, TransactionUpdateService.class);
        intent.setData(Uri.parse(CHOICE));
        //pendingIntent.cancel();
        PendingIntent pendingIntent = PendingIntent.getService(OrderBookFragment.this, REQUEST_CODE, intent, 0);
        alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(pendingIntent);}
    }
	
	
	private void plotOrderBook(ArrayList<Price_Amount> ob){
		XYPlot plot1 = (XYPlot) findViewById(R.id.chart);
		plot1.clear();
		
		int size = ob.size();
		int nask = Integer.parseInt(ob.get(size-1).getPrice());
		int nbid = Integer.parseInt(ob.get(size-1).getAmount());
		Number[] x1 = new Number[nask];
		Number[] y1 = new Number[nask];
		int i=0;
		for(i=0; i<nask; i++){
			x1[i] = Double.parseDouble(ob.get(i).getPrice());
			y1[i] = Double.parseDouble(ob.get(i).getAmount());
		}
		Log.i(TAG, "nask is "+nask +"and i is "+i);
		i--;
		Number[] x2 = new Number[nbid];
		Number[] y2 = new Number[nbid];
		for(int j=0; j<nbid; j++){
			x2[j] = Double.parseDouble(ob.get(i+j).getPrice());
			y2[j] = Double.parseDouble(ob.get(i+j).getAmount());
		}
		XYSeries series1 = new SimpleXYSeries(Arrays.asList(x1),Arrays.asList(y1),"Asks");
		XYSeries series2 = new SimpleXYSeries(Arrays.asList(x2),Arrays.asList(y2),"Bids");
		
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
        LineAndPointFormatter format1 = new LineAndPointFormatter(
                Color.RED,                   // line color
                null,        				// point color
                Color.RED, null);                // fill color
        LineAndPointFormatter format2 = new LineAndPointFormatter(
                Color.YELLOW,                   // line color
                null,          					 // point color
                Color.YELLOW, null);                	// fill color
        
        plot1.getGraphWidget().setPaddingRight(2);
        plot1.addSeries(series1, format1);
        plot1.addSeries(series2, format2);

        // customize our domain/range labels
        plot1.setDomainLabel("Price");
        plot1.setRangeLabel("Amount");
        
        
        plot1.redraw();
		plot1.setVisibility(1);
		plot1.bringToFront();
	}
}