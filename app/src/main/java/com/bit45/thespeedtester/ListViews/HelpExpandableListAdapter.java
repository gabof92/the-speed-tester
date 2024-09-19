package com.bit45.thespeedtester.ListViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bit45.thespeedtester.R;

import java.util.List;

/**
 * Adapter for the expandable list in the HelpActivity
 */
public class HelpExpandableListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<HelpItemGroup> items;

    public HelpExpandableListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<HelpItemGroup> items) {
        this.items = items;
    }

    /*---------------------------------|
    |      CHILD ITEM METHODS          |
    |----------------------------------*/
    @Override
    public HelpItemChild getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /*GET VIEW - CHILD*/
    @Override
    public View getRealChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
        ChildHolder holder;
        HelpItemChild item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.list_item_help_child, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.helpText);
            holder.image = (ImageView) convertView.findViewById(R.id.helpImage);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.text.setText(item.text);
        holder.image.setImageDrawable(item.image);

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

    /*---------------------------------|
    |      GROUP ITEM METHODS          |
    |----------------------------------*/
    @Override
    public HelpItemGroup getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /*GET VIEW - GROUP*/
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        GroupHolder holder;
        HelpItemGroup item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.list_item_help_group, parent, false);
            holder.icon = (TextView) convertView.findViewById(R.id.helpIcon);
            holder.title = (TextView) convertView.findViewById(R.id.helpText);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.icon.setText(item.icon);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*---------------------------------|
    |      ITEM VIEW HOLDERS           |
    |----------------------------------*/
    private static class ChildHolder {
        TextView text;
        ImageView image;
    }

    private static class GroupHolder {
        TextView title;
        TextView icon;
    }

}