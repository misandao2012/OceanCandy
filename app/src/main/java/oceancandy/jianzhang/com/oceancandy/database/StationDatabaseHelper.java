package oceancandy.jianzhang.com.oceancandy.database;

import oceancandy.jianzhang.com.oceancandy.domainobjects.Station;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StationDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "stations.sqlite";
	private static final int VERSION = 1;
	private static final String TABLE_STATION = "station";
	private static final String COLUMN_ID = "_id";
	private static final String COLUMN_STATE_NAME = "state_name";
	private static final String COLUMN_STATION_ID = "id";
	private static final String COLUMN_NAME = "name";

	public StationDatabaseHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table station (_id integer primary key autoincrement, "
				+ "state_name varchar(256), "
				+ "id integer, "
				+ "name varchar(256))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	//clear the all the data from database
	public void deleteAllData()
	{
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_STATION, null, null);
	}

	public long insertStation(Station station) {
		ContentValues contentValue = new ContentValues();
		contentValue.put(COLUMN_STATE_NAME, station.getStateName());
		contentValue.put(COLUMN_STATION_ID, station.getStationId());
		contentValue.put(COLUMN_NAME, station.getName());
		return getWritableDatabase().insert(TABLE_STATION, null, contentValue);
	}

	//get the stations grouped by state name
	public StationCursor queryStationsGroupByState() {
        Cursor wrapped = getReadableDatabase().query(TABLE_STATION,
        		null, null, null,COLUMN_STATE_NAME, null, null);
        return new StationCursor(wrapped);
    }

	//get the stations with the specified state name
	public StationCursor queryStationsByState(String stateName) {
        Cursor wrapped = getReadableDatabase().query(TABLE_STATION,
        		null, COLUMN_STATE_NAME + " = ?", 
                new String[]{ stateName }, null, null, null);
        return new StationCursor(wrapped);
    }

	public static class StationCursor extends CursorWrapper {

		public StationCursor(Cursor c) {
			super(c);
		}

		public Station getStation() {
			if (isBeforeFirst() || isAfterLast())
				return null;
			Station station = new Station();
			station.setId(getLong(getColumnIndex(COLUMN_ID)));
			station.setStateName(getString(getColumnIndex(COLUMN_STATE_NAME)));
			station.setStationId(getString(getColumnIndex(COLUMN_STATION_ID)));
			station.setName(getString(getColumnIndex(COLUMN_NAME)));
			return station;
		}
	}
}
