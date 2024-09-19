package com.bit45.thespeedtester.NavDrawer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bit45.thespeedtester.R;

import java.util.List;

public class DrawerAdapter extends BaseAdapter {
	
	private List<DrawerItem> mDrawerItems;
	private LayoutInflater mInflater;
	
	public DrawerAdapter(Context context, List<DrawerItem> items) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mDrawerItems = items;
	}

	@Override
	public int getCount() {
		return mDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mDrawerItems.get(position).getTag();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_item_nav_drawer, parent, false);
			holder = new ViewHolder();
			holder.icon = (TextView) convertView.findViewById(R.id.icon);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		DrawerItem item = mDrawerItems.get(position);

		holder.icon.setText(item.getIcon());
		holder.title.setText(item.getTitle());
		
		return convertView;
	}
	
	private static class ViewHolder {
		public /*Fontello*/TextView icon;
		public /*Roboto*/TextView title;
	}
}
