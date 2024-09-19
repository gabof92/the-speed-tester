package com.bit45.thespeedtester.ListViews;

public class TestResultItem {
	
	private long mId;
	private int mIconRes;
	public String mTextConnection;
	public String mTextSpeed;
    public String mTextNetwork;
    public String mTextData;
    public String mTextDuration;
    public String mTextDate;
    public String mTextTime;

	public TestResultItem(int id,
						  int iconResConnection,
						  String labelConnection,
						  String labelNetwork,
						  String labelSpeed,
						  String labelData,
						  String labelDuration,
						  String labelDate,
						  String labelTime) {

		mId = id;
		mIconRes = iconResConnection;
        mTextConnection = labelConnection;
        mTextSpeed = labelSpeed;
        mTextNetwork = labelNetwork;
        mTextData = labelData;
        mTextDuration = labelDuration;
        mTextDate = labelDate;
        mTextTime = labelTime;
	}

	public long getId() {
		return mId;
	}

	public void setId(long id) {
		mId = id;
	}

	@Override
	public String toString() {
		return mTextSpeed;
	}

	public int getIconRes() {
		return mIconRes;
	}

}
