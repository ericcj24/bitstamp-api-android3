package edu.illinois.jchen93.bitstampapiandroid3;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

// too many data, insertion database toooooo slow, consider trim the data upon receiving it

public class OrderBookUpdateService extends IntentService{
	static final String TAG = OrderBookUpdateService.class.getSimpleName();
	
	static final String TPATH = "https://www.bitstamp.net/api/order_book/";
	
	/**
	   * A constructor is required, and must call the super IntentService(String)
	   * constructor with a name for the worker thread.
	   */
	public OrderBookUpdateService() {
		super("OrderBookUpdateService");
	}
	
	
	@Override
    protected void onHandleIntent(Intent workIntent) {
		
		// Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        int choice = Integer.parseInt(dataString);
        
        switch (choice){
        	case 0:    		
        		break;
        	case 1:
        		int count = fetchOrderBook();
        		Log.i(TAG, "count is: "+ count);
        		break;
        	default:
        		break;
        
        }
	}
	
	
	private int fetchOrderBook(){
		int count = 0;        
        try {
        	URL url=new URL(TPATH);
            HttpURLConnection c=(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
        	c.setReadTimeout(15000);
        	c.connect();
            
        	int responseCode = c.getResponseCode();
        	Log.i(TAG, "order book response code: " + Integer.toString(responseCode));
        	if (responseCode == 200){
        		ObjectMapper mapper = new ObjectMapper();
                OrderBook ob = mapper.readValue(c.getInputStream(), OrderBook.class);

        		count = addNewOrderBook(ob);
        	}           
        }catch(java.net.ConnectException e){
        	Log.e(TAG, e.toString());        	
        }catch(java.net.UnknownHostException e){
        	Log.e(TAG, e.toString());
        }catch (Exception e) {
			// TODO Auto-generated catch block
        	Log.e(TAG, e.toString());
		}finally{
			//c.disconnect();
		}       
		return count;
	}
	
	private int addNewOrderBook(OrderBook orderBook){
		int count = 0;		
		ContentResolver cr = getContentResolver();
	
		String timestamp = orderBook.getTimestamp();
		Long timeNow = Long.parseLong(timestamp);		
		Log.i(TAG, " time now is: "+ timestamp);

		int askSize = orderBook.getAsks().size();
		int bidSize = orderBook.getBids().size();
		Log.i(TAG, " ask size is: "+ askSize);
		Log.i(TAG, " bid size is: "+ bidSize);
		ArrayList<ArrayList<String>> askList = orderBook.getAsks();
		for(int i=0; i<askList.size()/10; i++){		
				ContentValues values = new ContentValues();
				values.put(OrderBookProviderContract.ORDERBOOK_TIMESTAMP_COLUMN, timeNow);
				values.put(OrderBookProviderContract.ORDERBOOK_KIND_COLUMN, "ASK");
				values.put(OrderBookProviderContract.ORDERBOOK_PRICE_COLUMN, askList.get(i).get(0));
				values.put(OrderBookProviderContract.ORDERBOOK_AMOUNT_COLUMN, askList.get(i).get(1));
				cr.insert(OrderBookProviderContract.CONTENT_URI, values);
				count++;			
		}

		ArrayList<ArrayList<String>> bidList = orderBook.getBids();
		for(int i=0; i<bidList.size()/10; i++){			
				ContentValues values = new ContentValues();
				values.put(OrderBookProviderContract.ORDERBOOK_TIMESTAMP_COLUMN, timeNow);
				values.put(OrderBookProviderContract.ORDERBOOK_KIND_COLUMN, "BID");
				values.put(OrderBookProviderContract.ORDERBOOK_PRICE_COLUMN, bidList.get(i).get(0));
				values.put(OrderBookProviderContract.ORDERBOOK_AMOUNT_COLUMN, bidList.get(i).get(1));
				cr.insert(OrderBookProviderContract.CONTENT_URI, values);
				count++;		
		}				
		return count;
	}
	
	
	/**private ArrayList<Price_Amount> fetchOrderBookTemp(){
	ArrayList<Price_Amount> rt = new ArrayList<Price_Amount>();
	 try {
        	URL url=new URL(TPATH);
            HttpURLConnection c=(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
        	c.setReadTimeout(15000);
        	c.connect();    	
            
        	int responseCode = c.getResponseCode();
        	Log.i(TAG, "order book response code: " + Integer.toString(responseCode));
        	if (responseCode == 200){
        		ObjectMapper mapper = new ObjectMapper();
                OrderBook ob = mapper.readValue(c.getInputStream(), OrderBook.class);
                
                
                int askSize = 0;
				int bidSize = 0;
                for(ArrayList<String> temp : ob.getAsks()){
                	if(Double.parseDouble(temp.get(0)) > 300 &&  Double.parseDouble(temp.get(0))< 650 && Double.parseDouble(temp.get(1)) < 5){
                		Price_Amount newAsk = new Price_Amount(temp.get(0), temp.get(1));
                		rt.add(newAsk);
                		askSize++;}
                }
                for(ArrayList<String> temp : ob.getBids()){
                	if(Double.parseDouble(temp.get(0)) > 300 &&  Double.parseDouble(temp.get(0))< 650 && Double.parseDouble(temp.get(1)) < 5){
                		Price_Amount newBid = new Price_Amount(temp.get(0), temp.get(1));
                		rt.add(newBid);
                		bidSize++;}
                }
                rt.add(new Price_Amount(String.valueOf(askSize), String.valueOf(bidSize)));

        	}
	 }catch(java.net.ConnectException e){
		 Log.e(TAG, e.toString());        	
     }catch(java.net.UnknownHostException e){
    	 Log.e(TAG, e.toString());
     }catch (Exception e) {
    	 // TODO Auto-generated catch block
    	 Log.e(TAG, e.toString());
     }finally{
    	 //c.disconnect();
     }
	 return rt;
}*/
}