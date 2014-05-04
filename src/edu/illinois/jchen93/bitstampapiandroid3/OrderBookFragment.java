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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.DashPathEffect;


public class OrderBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	private final static String TAG="OrderBookActivity";
	
	private AlarmManager alarmMgr;

	private int REQUEST_CODE = 101;
	// Identifies a particular Loader being used in this component
    private static final int TRANSACTION_LOADER = 0;
    
    
	
	public OrderBookFragment() {
        // Empty constructor required for fragment subclasses
    }
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "on create");       
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
		Log.i(TAG, "on createView");
		
		/*
         * Initializes the CursorLoader. The URL_LOADER value is eventually passed
         * to onCreateLoader().
         */
		getLoaderManager().initLoader(TRANSACTION_LOADER, null, this);
		
        return inflater.inflate(R.layout.fragment_chart, container, false);
    }
	
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		Log.i(TAG, "on attach");
	}
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i(TAG, "on resume");				
		
		String CHOICE = "0";
		Intent intent = new Intent(getActivity(), TransactionUpdateService.class);
		intent.setData(Uri.parse(CHOICE));
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(), REQUEST_CODE, intent, 0);
        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 2500, pendingIntent);
        	
	}
	
	@Override
	public void onPause(){
		super.onPause();
		if (alarmMgr != null)
        {Log.i(TAG, "on pause");
        String CHOICE = "0";
        Intent intent = new Intent(getActivity(), TransactionUpdateService.class);
        intent.setData(Uri.parse(CHOICE));
        //pendingIntent.cancel();
        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), REQUEST_CODE, intent, 0);
        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(pendingIntent);}
	}
	
	@Override
	public void onDetach() {
    	super.onDetach();
    	Log.i(TAG, "on detach");
    }


	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
        // sample only has one Loader, so we don't care about the ID.
        // First, pick the base URI to use depending on whether we are
        // currently filtering.
		/*
	     * Takes action based on the ID of the Loader that's being created
	     */
	    switch (id) {
	        case TRANSACTION_LOADER:
	            // Returns a new CursorLoader
	        	String[] projection = {TransactionProviderContract.TRANSACTION_DATE_COLUMN,
	        						TransactionProviderContract.TRANSACTION_TID_COLUMN,
	        						TransactionProviderContract.TRANSACTION_PRICE_COLUMN,
	        						TransactionProviderContract.TRANSACTION_AMOUNT_COLUMN};
	        	String sortOrder = TransactionProviderContract.TRANSACTION_TID_COLUMN + " DESC" +" LIMIT " + 700;
	            return new CursorLoader(
	                        getActivity(),   // Parent activity context
	                        TransactionProviderContract.TRANSACTIONURL_TABLE_CONTENTURI, // Table to query
	                        projection,      // Projection to return
	                        null,            // No selection clause
	                        null,            // No selection arguments
	                        sortOrder        // Default sort order
	            			);
	        default:
	            // An invalid id was passed in
	            return null;
	   }
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor returnCursor) {
		
		/*
         * Moves the query results into the adapter, causing the
         * ListView fronting this adapter to re-display
         */            
        plotTransaction(returnCursor);
        
        //mAdapter.changeCursor(returnCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Sets the Adapter's backing data to null. This prevents memory leaks.
        //mAdapter.changeCursor(null);
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