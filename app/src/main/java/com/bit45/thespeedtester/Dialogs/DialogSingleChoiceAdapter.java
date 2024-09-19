package com.bit45.thespeedtester.Dialogs;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bit45.thespeedtester.R;
import com.bit45.thespeedtester.font.RobotoTextView;

/**
 * Custom adapter for the ListView in my Single Choice Dialog
 */
public class DialogSingleChoiceAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    Context context;
    String items[];
    int selectedPosition;

    public DialogSingleChoiceAdapter(Context context, String[] items, int selectedPosition){
        this.items = items;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.selectedPosition = selectedPosition;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_dialog_single_choice, parent, false);
            holder = new ViewHolder();
            holder.layout = convertView.findViewById(R.id.dlg_item_layout);
            holder.icon = (ImageView) convertView.findViewById(R.id.item_single_choice_icon);
            holder.text_label = (RobotoTextView) convertView.findViewById(R.id.item_single_choice_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(position==selectedPosition){
            if(Build.VERSION.SDK_INT >= 16)
                holder.icon.setBackground(context.getResources().getDrawable(R.drawable.check_on_dark));
            else
                holder.icon.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.check_on_dark));
        }
        else{
            if(Build.VERSION.SDK_INT >= 16)
                holder.icon.setBackground(context.getResources().getDrawable(R.drawable.check_off_dark));
            else
                holder.icon.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.check_off_dark));
        }

        if(position%2!=0)
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.alt_background2));
        else if(position%2==0)
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.alt_background));

        holder.text_label.setText(items[position]);

        return convertView;
    }

    public void changeSelected(int selectedPosition){
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public View layout;
        public ImageView icon;
        public RobotoTextView text_label;
    }

}
