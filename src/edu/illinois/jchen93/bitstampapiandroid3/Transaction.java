package edu.illinois.jchen93.bitstampapiandroid3;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/*
 * Transaction class used to bind JSON data from Transaction API
 * https://www.bitstamp.net/api/transactions/
 * example JSON data:
	{"date": "1395551812", "tid": 4151239, "price": "564.23", "amount": "0.05000000"}
*/
public class Transaction implements Parcelable{
	static final String TAG = "transaction implements parcelable";
	
    private String date;
    private long tid;
    private String price;
    private String amount;
    public Transaction() {
    }
    public Transaction(String date, long tid, String price, String amount) {
      this.date = date;
      this.tid = tid;
      this.price = price;
      this.amount = amount;
    }
    @Override
    public String toString() {
      return String.format("(date=%s, tid=%d, price=%s, amount=%s)", date, tid, price, amount);
    }
    
    public String getDate(){
    	return date;
    }
    public long getTid(){
    	return tid;
    }
    public String getPrice(){
    	return price;
    }
    public String getAmount(){
    	return amount;
    }
    
    // implementing parcelable
    public Transaction(Parcel in) {
        //super(); 
        //readFromParcel(in);
    	date = in.readString();
		tid = in.readLong();
		price = in.readString();
		amount = in.readString();
		Log.i(TAG,"1");
    }
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		Log.i(TAG,"2");
		return 0;		
	}
	@Override
	public void writeToParcel(Parcel dest, int flag) {
		// TODO Auto-generated method stub
		dest.writeString(date);
        dest.writeLong(tid);  
        dest.writeString(price);
		dest.writeString(amount);
		Log.i(TAG,"3");
	}
	
	public static final Parcelable.Creator<Transaction> CREATOR = new Parcelable.Creator<Transaction>() {
        public Transaction createFromParcel(Parcel in) {
        	Log.i(TAG,"4");
            return new Transaction(in);
        }

        public Transaction[] newArray(int size) {
        	Log.i(TAG,"5");
            return new Transaction[size];
        }
    };
	
}