package edu.illinois.jchen93.bitstampapiandroid3;

import java.util.ArrayList;

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


public class OrderBookReceiver extends BroadcastReceiver{
	public static final String TAG = "OrderBookReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent){
		//String intentAction = intent.getAction();
    	//Log.i(TAG, "this is "+intentAction);
		//if(intentAction == OrderBookUpdateService.ORDERBOOK_RESULT){		  
    		ArrayList<Price_Amount> s = intent.getParcelableArrayListExtra(OrderBookUpdateService.ORDERBOOK_RESULT);   
    		if (s==null){ Log.i(TAG, "broadcast received null, failed broadcast");}
    		else{
    			Log.i(TAG, "orderbook broadcast receive correctly, arraylist size is: "+Integer.toString(s.size()));
    			plotOrderBook(s);
    		}
    	//}
	}
	
	private void plotOrderBook(ArrayList<Price_Amount> ob){
		XYPlot plot1 = findViewById(R.id.chart);
		plot1.getLayoutManager().remove(plot1.getDomainLabelWidget());
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