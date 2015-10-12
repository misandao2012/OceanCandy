package oceancandy.jianzhang.com.oceancandy.domainobjects;

public class Station {
	private long mId;
	private String mStateName;
	private String mStationId;
	private String mName;
	
	public String getStateName() {
		return mStateName;
	}

	public void setStateName(String stateName) {
		mStateName = stateName;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getStationId() {
		return mStationId;
	}

	public void setStationId(String stationId) {
		mStationId = stationId;
	}
	
}
