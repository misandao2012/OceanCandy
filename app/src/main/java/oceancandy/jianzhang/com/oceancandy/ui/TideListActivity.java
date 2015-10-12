package oceancandy.jianzhang.com.oceancandy.ui;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import oceancandy.jianzhang.com.oceancandy.constants.Constants;
import oceancandy.jianzhang.com.oceancandy.constants.IntentExtra;
import oceancandy.jianzhang.com.oceancandy.domainobjects.Tide;
import oceancandy.jianzhang.com.oceancandy.network.WebService;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import oceancandy.jianzhang.com.oceancandy.R;

public class TideListActivity extends Activity {
	
	private List<Tide> mLowTideList;
	private List<Tide> mHighTideList;
	private ProgressBar mProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tide_list_activity);		
				
		mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);		
		String stateId = getIntent().getStringExtra(IntentExtra.STATE_ID);
				
		if(WebService.networkConnected(this)){
			GetStationsTask getStationsTask = new GetStationsTask();
			getStationsTask.execute(stateId);
		} else {
			WebService.showNetworkDialog(this);
		}
	}

	//get the tide info list for the low or high
	private List<Tide> getTideList(String jsonData, String lowOrHigh) throws JSONException{
		List<Tide> tideList = new ArrayList<Tide>();
		JSONObject jTideObj = new JSONObject(jsonData);
		JSONArray jTideArr = jTideObj.getJSONArray(lowOrHigh);
		
		for (int i=0; i<jTideArr.length(); i++) {
			JSONObject jTide = jTideArr.getJSONObject(i);
			Tide tide = new Tide();
			tide.setTime(jTide.getString("time"));
			tide.setFeet(jTide.getString("feet"));
			tideList.add(tide);
		}		
		return tideList;
	}
	
	private class GetStationsTask extends AsyncTask<String, Void, String> {		
		@Override
        protected void onPreExecute() {
			mProgressBar.setVisibility(View.VISIBLE);
        }
		
		@Override
		protected String doInBackground(String... stateId) {
			return WebService.httpGet(Constants.OCEAN_CANDY_BASE_URL+"/"+stateId[0]);
		}

		@Override
		protected void onPostExecute(final String jsonData) {
			super.onPostExecute(jsonData);
			mProgressBar.setVisibility(View.GONE);
			try {				
				mLowTideList = getTideList(jsonData, "Low");
				mHighTideList = getTideList(jsonData, "High");
			} catch (JSONException e) {				
				e.printStackTrace();
			}	
			setupTideList();
		}
	}
	
	private void setupTideList(){
		ListView mLvLowTide = (ListView) findViewById(R.id.list_low_tide);
		ListView mLvHighTide = (ListView) findViewById(R.id.list_high_tide);
		TideListAdapter tideListAdapter = new TideListAdapter(this);
		if(mLowTideList != null){
			tideListAdapter.addAll(mLowTideList);			
			mLvLowTide.setAdapter(tideListAdapter);
		}
				
		tideListAdapter = new TideListAdapter(this);
		if(mHighTideList != null) {
			tideListAdapter.addAll(mHighTideList);			
			mLvHighTide.setAdapter(tideListAdapter);			
		}		
	}
	
	private static class ViewHolder{
		TextView itemTime;
		TextView itemFeet;
	}
	
	private class TideListAdapter extends ArrayAdapter<Tide> {
		public TideListAdapter(Context context) {
			super(context, 0);
		}
		
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
			 ViewHolder holder;
	            if (convertView == null) {
	                convertView = getLayoutInflater()
	                        .inflate(R.layout.tide_list_item, parent, false);
	                holder = new ViewHolder();
	                holder.itemTime = (TextView)convertView.findViewById(R.id.tv_time);
	                holder.itemFeet = (TextView)convertView.findViewById(R.id.tv_feet);
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder)convertView.getTag();
	            }	            
	            Tide tide = getItem(position);
	            holder.itemTime.setText(parseTideTime(tide.getTime()));
	            holder.itemFeet.setText(tide.getFeet());
			
			return convertView;			
		}
	}

	//change the time format
	private String parseTideTime(String original){
		SimpleDateFormat dateFormat = null;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
		Date date = null;
		Calendar calendar = Calendar.getInstance();
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            date = dateFormat.parse(original);
        } catch (ParseException e) {
            e.printStackTrace();
        }        
        dateFormat = new SimpleDateFormat("MM/dd/yy, h:mm a", Locale.US);
        dateFormat.setTimeZone(calendar.getTimeZone());
        return dateFormat.format(date);
	}
}
