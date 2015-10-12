package oceancandy.jianzhang.com.oceancandy.ui;

import oceancandy.jianzhang.com.oceancandy.constants.IntentExtra;
import oceancandy.jianzhang.com.oceancandy.database.StationDatabaseHelper;
import oceancandy.jianzhang.com.oceancandy.database.StationDatabaseHelper.StationCursor;
import oceancandy.jianzhang.com.oceancandy.domainobjects.Station;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import oceancandy.jianzhang.com.oceancandy.R;

public class StationListActivity extends Activity implements AbsListView.OnItemClickListener{
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view_activity);

		ListView lvStations = (ListView) findViewById(R.id.list_view);
		StationDatabaseHelper helper = new StationDatabaseHelper(this);		
		String stateName = getIntent().getStringExtra(IntentExtra.STATE_NAME);
		StationCursor cursor = helper.queryStationsByState(stateName);
		StationCursorAdapter adapter = new StationCursorAdapter(StationListActivity.this, cursor);
		lvStations.setAdapter(adapter);		
		lvStations.setOnItemClickListener(this);
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
			TextView tvStationName = (TextView) view;
			tvStationName.setText(station.getName());
			view.setTag(station.getStationId());
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String stateId = (String)view.getTag();
		Intent intent = new Intent(this, TideListActivity.class);
		intent.putExtra(IntentExtra.STATE_ID, stateId);
        startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}
}
