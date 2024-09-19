package com.bit45.thespeedtester.ListViews;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bit45.thespeedtester.R;
import com.bit45.thespeedtester.font.FontelloTextView;
import com.bit45.thespeedtester.font.RobotoTextView;

import java.util.ArrayList;

public class TestResultListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private ArrayList<TestResultItem> mTestResultItemList;
	
	public TestResultListAdapter(Context context, ArrayList<TestResultItem> testResultItemList) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTestResultItemList = testResultItemList;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public int getCount() {
		return mTestResultItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return mTestResultItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mTestResultItemList.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_result, parent, false);
			holder = new ViewHolder();
			holder.icon = (FontelloTextView) convertView.findViewById(R.id.icon);
            holder.text_speed = (RobotoTextView) convertView.findViewById(R.id.text_speed);
			holder.text_network = (RobotoTextView) convertView.findViewById(R.id.text_network);
			holder.text_connection = (RobotoTextView) convertView.findViewById(R.id.text_connection);
			holder.text_duration = (RobotoTextView) convertView.findViewById(R.id.text_duration);
			holder.text_data = (RobotoTextView) convertView.findViewById(R.id.text_data);
			holder.text_date = (RobotoTextView) convertView.findViewById(R.id.text_date);
			holder.text_time = (RobotoTextView) convertView.findViewById(R.id.text_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		TestResultItem dm = mTestResultItemList.get(position);

        holder.icon.setText(mContext.getString(dm.getIconRes()));
        holder.text_speed.setText(dm.mTextSpeed);
		holder.text_network.setText(dm.mTextNetwork);
		holder.text_connection.setText(dm.mTextConnection);
		holder.text_data.setText(dm.mTextData);
		holder.text_duration.setText(dm.mTextDuration);
		holder.text_date.setText(dm.mTextDate);
		holder.text_time.setText(dm.mTextTime);
		return convertView;
	}

	public int getPositionById(long id) {
		for (int i = 0; i < mTestResultItemList.size(); i++) {
			if(id== mTestResultItemList.get(i).getId()) return i;
		}
		return -1;
	}

	private static class ViewHolder {
		public FontelloTextView icon;
        public RobotoTextView text_network;
        public RobotoTextView text_speed;
		public RobotoTextView text_connection;
		public RobotoTextView text_data;
		public RobotoTextView text_duration;
		public RobotoTextView text_date;
		public RobotoTextView text_time;
	}


	public void remove(int position) {
		if(position<0 || position> mTestResultItemList.size()){
			Log.e("Bit45_TheSpeedTester", "inexOutOfBoundsException!!!");
			return;
		}
		mTestResultItemList.remove(position);
		notifyDataSetChanged();
	}
}
