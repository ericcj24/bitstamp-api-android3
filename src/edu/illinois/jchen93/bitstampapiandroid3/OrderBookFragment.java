package edu.illinois.jchen93.bitstampapiandroid3;

import java.util.ArrayList;


import java.util.List;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.LoaderManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.graphics.DashPathEffect;


public class OrderBookFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
	
	private final static String TAG = OrderBookFragment.class.getSimpleName();
	
	private AlarmManager alarmMgr;
	private int REQUEST_CODE = 102;
	// Identifies a particular Loader being used in this component
    private static final int ORDERBOOK_LOADER = 0;    
	
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
		getLoaderManager().initLoader(ORDERBOOK_LOADER, null, this);
		
        return inflater.inflate(R.layout.fragment_orderbook_chart, container, false);
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
		
		String CHOICE = "1";
		Intent intent = new Intent(getActivity(), OrderBookUpdateService.class);
		intent.setData(Uri.parse(CHOICE));
		PendingIntent pendingIntent = PendingIntent.getService(getActivity(), REQUEST_CODE, intent, 0);
        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, 0, 10000, pendingIntent);
        	
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Log.i(TAG, "on pause");
		
		String CHOICE = "1";
		Intent intent = new Intent(getActivity(), OrderBookUpdateService.class);
		intent.setData(Uri.parse(CHOICE));
		boolean alarmUp = (PendingIntent.getService(getActivity(), REQUEST_CODE, intent, PendingIntent.FLAG_NO_CREATE) != null);
		
		if(alarmUp){
			Log.i(TAG, "alarm is up, cancelling");
	        //Intent intent = new Intent(getActivity(), OrderBookUpdateService.class);
	        //pendingIntent.cancel();
	        PendingIntent pendingIntent = PendingIntent.getService(getActivity(), REQUEST_CODE, intent, 0);
	        alarmMgr = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
	        alarmMgr.cancel(pendingIntent);
		}
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
	        case ORDERBOOK_LOADER:
	            // Returns a new CursorLoader
	        	String[] projection = {OrderBookProviderContract.ORDERBOOK_TIMESTAMP_COLUMN,
	        						OrderBookProviderContract.ORDERBOOK_KIND_COLUMN,
	        						OrderBookProviderContract.ORDERBOOK_PRICE_COLUMN,
	        						OrderBookProviderContract.ORDERBOOK_AMOUNT_COLUMN};
	        	String sortOrder = OrderBookProviderContract.ORDERBOOK_TIMESTAMP_COLUMN + " DESC" + " LIMIT " + 3000;
	            return new CursorLoader(
	                        getActivity(),   // Parent activity context
	                        OrderBookProviderContract.ORDERBOOKURL_TABLE_CONTENTURI, // Table to query
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
		if(returnCursor!=null){
			//Log.i(TAG, "fancy sth?");
			plotOrderBook(returnCursor);
        }      
        //mAdapter.changeCursor(returnCursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		// Sets the Adapter's backing data to null. This prevents memory leaks.
        //mAdapter.changeCursor(null);
	}
	
	
	private void plotOrderBook(Cursor cursor){
		XYPlot plot1 = (XYPlot) getView().findViewById(R.id.orderbookchart);
		plot1.clear();
		
		List<Double> x1 = new ArrayList<Double>();
		List<Double> y1 = new ArrayList<Double>();
		List<Double> x2 = new ArrayList<Double>();
		List<Double> y2 = new ArrayList<Double>();
		
		//int nask = Integer.parseInt(ob.get(size-1).getPrice());
		//int nbid = Integer.parseInt(ob.get(size-1).getAmount());
		Log.i(TAG, String.valueOf(cursor.getCount()));
		cursor.moveToFirst();	
		while(cursor.isAfterLast() == false){
			String type = cursor.getString(cursor.getColumnIndex(OrderBookProviderContract.ORDERBOOK_KIND_COLUMN));
			if(type.equals("ASK")){
				String temp = cursor.getString(cursor.getColumnIndex(OrderBookProviderContract.ORDERBOOK_PRICE_COLUMN));
				x1.add(Double.parseDouble(temp));
				y1.add(Double.parseDouble(cursor.getString(cursor.getColumnIndex(OrderBookProviderContract.ORDERBOOK_AMOUNT_COLUMN))));
			}
			if(type.equals("BID")){
				x2.add(Double.parseDouble(cursor.getString(cursor.getColumnIndex(OrderBookProviderContract.ORDERBOOK_PRICE_COLUMN))));
				y2.add(Double.parseDouble(cursor.getString(cursor.getColumnIndex(OrderBookProviderContract.ORDERBOOK_AMOUNT_COLUMN))));
			}
			cursor.moveToNext();
		}

		XYSeries series1 = new SimpleXYSeries(x1,y1,"Asks");
		XYSeries series2 = new SimpleXYSeries(x2,y2,"Bids");
		
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