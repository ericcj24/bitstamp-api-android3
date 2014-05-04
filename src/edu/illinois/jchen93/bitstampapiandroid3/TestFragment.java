package edu.illinois.jchen93.bitstampapiandroid3;

import android.app.AlarmManager;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TestFragment extends Fragment{
	
	private final static String TAG="TestFragment";   
	
	public TestFragment() {
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
		
        return inflater.inflate(R.layout.test, container, false);
    }
	
	@Override
	public void onResume(){
		super.onResume();
		Log.i(TAG, "on resume");				
		
		TextView v = (TextView) getView().findViewById(R.id.testtext);
		v.setText("Hello World");
        	
	}
}