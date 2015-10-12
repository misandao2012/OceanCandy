package oceancandy.jianzhang.com.oceancandy.ui;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import oceancandy.jianzhang.com.oceancandy.constants.Constants;
import oceancandy.jianzhang.com.oceancandy.constants.IntentExtra;
import oceancandy.jianzhang.com.oceancandy.database.StationDatabaseHelper;
import oceancandy.jianzhang.com.oceancandy.database.StationDatabaseHelper.StationCursor;
import oceancandy.jianzhang.com.oceancandy.domainobjects.Station;
import oceancandy.jianzhang.com.oceancandy.network.WebService;


import oceancandy.jianzhang.com.oceancandy.R;

public class StateListActivity extends AppCompatActivity implements AbsListView.OnItemClickListener{

    private StationDatabaseHelper mHelper;
    private ListView mLvStates;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view_activity);

        mLvStates = (ListView) findViewById(R.id.list_view);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mHelper = new StationDatabaseHelper(this);

        if(WebService.networkConnected(this)){
            GetStationsTask getStationsTask = new GetStationsTask();
            getStationsTask.execute();
        } else {
            WebService.showNetworkDialog(this);
        }
        mLvStates.setOnItemClickListener(this);
    }

    //get the Stations Json Data and insert the data into the database
    private void setupStationDatabase(String jsonData)
            throws JSONException {
        JSONArray jStationArr = new JSONArray(jsonData);
        mHelper.deleteAllData();
        for (int i = 0; i < jStationArr.length(); i++) {
            JSONObject jStation = jStationArr.getJSONObject(i);
            Station station = new Station();
            station.setName(jStation.getString("name"));
            station.setStateName(jStation.getString("state_name"));
            station.setStationId(jStation.getString("id"));
            station.setId(mHelper.insertStation(station));
        }
    }

    private class GetStationsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            return WebService.httpGet(Constants.OCEAN_CANDY_BASE_URL);
        }

        @Override
        protected void onPostExecute(final String jsonData) {
            super.onPostExecute(jsonData);
            mProgressBar.setVisibility(View.GONE);
            try {
                setupStationDatabase(jsonData);
                StationCursor cursor = mHelper.queryStationsGroupByState();
                StationCursorAdapter adapter = new StationCursorAdapter(StateListActivity.this,
                        cursor);
                mLvStates.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private static class StationCursorAdapter extends CursorAdapter {

        private StationCursor mStationCursor;

        public StationCursorAdapter(Context context, StationCursor cursor) {
            super(context, cursor, 0);
            mStationCursor = cursor;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return inflater.inflate(android.R.layout.simple_list_item_1,
                    parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            Station station = mStationCursor.getStation();
            TextView tvStateName = (TextView) view;
            tvStateName.setText(station.getStateName());
            view.setTag(station.getStateName());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String stateName = (String)view.getTag();
        Intent intent = new Intent(this, StationListActivity.class);
        intent.putExtra(IntentExtra.STATE_NAME, stateName);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }
}
