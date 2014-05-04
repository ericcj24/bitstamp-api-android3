package edu.illinois.jchen93.bitstampapiandroid3;

import java.net.HttpURLConnection;
import java.net.URL;
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


public class TransactionUpdateService extends IntentService{
	static final String TAG = "TransactionUpdateService";
	
	static final String TPATH = "https://www.bitstamp.net/api/transactions/";
	static final String TRANSACTION_TABLE_NAME = "transactions";
	static final String NAME = "edu.illinois.jchen93.bitstampapiandroid2.TransactionUpdateService";
	static final public String TRANSACTION_RESULT = "edu.illinois.jchen93.bitstampapiandroid2.TransactionUpdateService.PROCESSED";	
	static boolean isFirst = true;
	static long databaseDate = 0;
	
	
	/**
	   * A constructor is required, and must call the super IntentService(String)
	   * constructor with a name for the worker thread.
	   */
	public TransactionUpdateService() {
		super("TransactionUpdateService");
	}
		
	@Override
    protected void onHandleIntent(Intent workIntent) {
		Log.i(TAG, this.toString());
		// Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        
		int count = fetchTransactions();
		Log.i(TAG, "get new count : " + Long.toString(count));
		Log.i(TAG, "isFirst " + isFirst);
		
	}
	
	
	private int fetchTransactions(){
		int count = 0;
		
        try {
        	URL url=new URL(TPATH);
            HttpURLConnection c=(HttpURLConnection)url.openConnection();
            c.setRequestMethod("GET");
        	c.setReadTimeout(15000);
        	c.connect();
            
        	int responseCode = c.getResponseCode();
        	Log.i(TAG, "response code: " + Integer.toString(responseCode));
        	if (responseCode == 200){
        		ObjectMapper mapper = new ObjectMapper();
        		List<Transaction> transactionList = mapper.readValue(c.getInputStream(), new TypeReference<ArrayList<Transaction>>() { });
        		
        		ArrayList<Transaction> rt = new ArrayList<Transaction>();
        		rt.addAll(transactionList);
        		count = addNewTransaction(rt);
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
	
	private int addNewTransaction(ArrayList<Transaction> lt){
		int count = 0;
		
		long newDate = Long.parseLong(lt.get(0).getDate());
				
		if(newDate > databaseDate){
			ContentResolver cr = getContentResolver();
			// find all rows that has date bigger than newDate
			String selection = TransactionProviderContract.TRANSACTION_DATE_COLUMN + " = " + newDate;
			String[] projection = null;
			String[] selectionArgs = null;
			String sortOrder = null;
			Cursor cursor = cr.query(TransactionProviderContract.CONTENT_URI, 
									projection, 
									selection, 
									selectionArgs, 
									sortOrder);
						
			if (cursor.getCount()==0) {
				for (Transaction temp : lt){
					long entryTime = Long.parseLong(temp.getDate());
					if(entryTime <= databaseDate) break;
					ContentValues values = new ContentValues();
					values.put(TransactionProviderContract.TRANSACTION_TID_COLUMN, temp.getTid());
					values.put(TransactionProviderContract.TRANSACTION_DATE_COLUMN, Long.parseLong(temp.getDate()));
					values.put(TransactionProviderContract.TRANSACTION_PRICE_COLUMN, temp.getPrice());
					values.put(TransactionProviderContract.TRANSACTION_AMOUNT_COLUMN, temp.getAmount());
					cr.insert(TransactionProviderContract.CONTENT_URI, values);
					count++;
				}
			}
			cursor.close();
			databaseDate = newDate;
		}
		Log.i(TAG, "count size is: " + count);
		return count;
	}
	

	
	private int fetchTradeBook(){
		int count = 0;
		
		return count;
	}
	
	
}