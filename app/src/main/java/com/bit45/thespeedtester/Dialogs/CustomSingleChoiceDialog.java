package com.bit45.thespeedtester.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.bit45.thespeedtester.R;
import com.bit45.thespeedtester.font.FontelloTextView;

public class CustomSingleChoiceDialog extends Dialog{
    private final int selectedPosition;
    private Context context;
    private ListView listView = null;
    private String itemValues[];
    private AdapterView.OnItemClickListener itemAction;
    private String title;
    private int titleIconRes;

    public CustomSingleChoiceDialog(Context context, int theme, String[] itemValues, int selected, String titleText, int titleIcon) {
        super(context, theme);
        this.context = context;
        this.itemValues = itemValues;
        this.selectedPosition = selected;
        this.title = titleText;
        titleIconRes = titleIcon;
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        itemAction = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_single_choice);
        listView = (ListView) findViewById(R.id.dlg_single_choice_list);

        FontelloTextView titleIcon = (FontelloTextView) findViewById(R.id.dlg_title_icon);
        titleIcon.setText(titleIconRes);
        TextView tvTitle = (TextView) findViewById(R.id.dlg_title_text);
        tvTitle.setText(title);

        DialogSingleChoiceAdapter adapter = new DialogSingleChoiceAdapter(context, itemValues, selectedPosition);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(itemAction);

        View btnCancel = findViewById(R.id.dlg_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void changeSelectedUI(int position) {
        DialogSingleChoiceAdapter adapter = (DialogSingleChoiceAdapter)listView.getAdapter();
        adapter.changeSelected(position);
    }
}
