package com.bit45.thespeedtester;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.bit45.thespeedtester.ListViews.AnimatedExpandableListView;
import com.bit45.thespeedtester.ListViews.HelpExpandableListAdapter;
import com.bit45.thespeedtester.ListViews.HelpItemGroup;
import com.bit45.thespeedtester.ListViews.HelpItemChild;
import com.bit45.thespeedtester.font.FontelloTextView;
import com.bit45.thespeedtester.managers.GoogleServicesManager;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {

    private GoogleServicesManager gsManager;

    int apiVersion = android.os.Build.VERSION.SDK_INT;
    private AnimatedExpandableListView listView;
    private HelpExpandableListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

//      initialize Google services manager (used for Admob and Google Analytics)
        //gsManager = new GoogleServicesManager(this);
//      initialize G-Analytics Tracker
        //gsManager.initializeTracker();

//      initialize AdMob Banner
        //AdView mAdView = (AdView) findViewById(R.id.adView);
        //AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        //Setup custom toolbar
        setupToolbar();

        //Populate list and set adapter
        setupList();

        //Set up expand animation
        setupExpandAnimation();

    }

    @Override
    protected void onStart() {
        //CALLED AFTER onCreate BUT BEFORE HAVING ACCESS TO THE UI
        super.onStart();
        //gsManager.reportActivityStart(this);
    }

    @Override
    protected void onStop() {
        //APP HAS OFFICIALLY LEFT THE SCREEN
        super.onStop();
        //gsManager.reportActivityStop(this);
    }

    private void setupToolbar(){
//      Getting an XML toolbar instead of the default actionbar
//      this is done to support older API's that didn't have actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//      set the inflated toolbar as the support actionbar
        setSupportActionBar(toolbar);

        //Display the home button in the actionbar
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupList() {

        List<HelpItemGroup> items = new ArrayList<HelpItemGroup>();

        /*How to start*/
        HelpItemGroup groupStart = createGroupItem(R.string.icon_help_start, R.string.label_help_title_start);
        groupStart.items.add(createChildItem(R.layout.help_image_start_1, R.string.label_help_text_start_1, true));
        groupStart.items.add(createChildItem(R.layout.help_image_start_2, R.string.label_help_text_start_2, true));
        items.add(groupStart);
        /*Connection*/
        HelpItemGroup groupConnection = createGroupItem(R.string.icon_help_connection, R.string.label_help_title_connection);
        groupConnection.items.add(createChildItem(R.layout.help_image_connection_1, R.string.label_help_text_connection_1, true));
        groupConnection.items.add(createChildItem(R.layout.help_image_connection_2, R.string.label_help_text_connection_2, true));
        groupConnection.items.add(createChildItem(R.layout.help_image_connection_3, R.string.label_help_text_connection_3, true));
        items.add(groupConnection);
        /*The Graph*/
        HelpItemGroup groupGraph = createGroupItem(R.string.icon_help_graph, R.string.label_help_title_graph);
        groupGraph.items.add(createChildItem(R.drawable.help_image_graph_1, R.string.label_help_text_graph_1, false));
        groupGraph.items.add(createChildItem(R.drawable.help_image_graph_2, R.string.label_help_text_graph_2, false));
        groupGraph.items.add(createChildItem(R.drawable.help_image_graph_3, R.string.label_help_text_graph_3, false));
        groupGraph.items.add(createChildItem(R.drawable.help_image_graph_4, R.string.label_help_text_graph_4, false));
        groupGraph.items.add(createChildItem(R.drawable.help_image_graph_5, R.string.label_help_text_graph_5, false));
        items.add(groupGraph);
        /*Results*/
        HelpItemGroup groupResults = createGroupItem(R.string.icon_help_results, R.string.label_help_title_results);
        groupResults.items.add(createChildItem(R.layout.help_image_results_1, R.string.label_help_text_results_1, true));
        groupResults.items.add(createChildItem(R.layout.help_image_results_2, R.string.label_help_text_results_2, true));
        items.add(groupResults);
        /*Configuration*/
        HelpItemGroup groupConfiguration = createGroupItem(R.string.icon_help_configuration, R.string.label_help_title_configuration);
        groupConfiguration.items.add(createChildItem(R.drawable.help_image_configuration_1, R.string.label_help_text_configuration_1, false));
        groupConfiguration.items.add(createChildItem(R.layout.help_image_configuration_2, R.string.label_help_text_configuration_2, true));
        groupConfiguration.items.add(createChildItem(R.layout.help_image_configuration_3, R.string.label_help_text_configuration_3, true));
        items.add(groupConfiguration);
        /*Save*/
        HelpItemGroup groupSave = createGroupItem(R.string.icon_help_save, R.string.label_help_title_save);
        groupSave.items.add(createChildItem(R.drawable.help_image_save_1, R.string.label_help_text_save_1, false));
        items.add(groupSave);
        /*Browse*/
        HelpItemGroup groupBrowse = createGroupItem(R.string.icon_help_browse, R.string.label_help_title_browse);
        groupBrowse.items.add(createChildItem(R.drawable.help_image_browse_1, R.string.label_help_text_browse_1, false));
        groupBrowse.items.add(createChildItem(R.layout.help_image_browse_2, R.string.label_help_text_browse_2, true));
        groupBrowse.items.add(createChildItem(R.layout.help_image_browse_3, R.string.label_help_text_browse_3, true));
        items.add(groupBrowse);
        /*Units*/
        HelpItemGroup groupUnits = createGroupItem(R.string.icon_help_units, R.string.label_help_title_units);
        groupUnits.items.add(createChildItem(R.layout.help_image_units_1, R.string.label_help_text_units_1, true));
        groupUnits.items.add(createChildItem(R.layout.help_image_units_2, R.string.label_help_text_units_2, true));
        items.add(groupUnits);

        adapter = new HelpExpandableListAdapter(this);
        adapter.setData(items);

        listView = (AnimatedExpandableListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
    }

    private HelpItemGroup createGroupItem(int icon, int title){
        HelpItemGroup groupItem = new HelpItemGroup();

        groupItem.icon = getString(icon);
        groupItem.title = getString(title);

        return groupItem;
    }

    private HelpItemChild createChildItem(int image, int text, boolean createImage){

        HelpItemChild childItem = new HelpItemChild();

        if(createImage) childItem.image = createImageFromLayout(image);
        else childItem.image = getImageFromResource(image);

        childItem.text = getString(text);

        return childItem;
    }

    @SuppressLint("NewApi")
    private Drawable getImageFromResource(int resID){
        if(apiVersion>=21)
            return getDrawable(resID);
        else
            return getResources().getDrawable(resID);
    }

    /*This method converts a Layout into a Bitmap,
     * the id of the layout is the parameter "resID"*/
    private Drawable createImageFromLayout(int resID){
        View layout = getLayoutInflater().inflate(resID, null);
        layout.measure(0, 0);
        layout.layout(0, 0, layout.getMeasuredWidth(),layout.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(layout.getMeasuredWidth(),layout.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);
        return new BitmapDrawable(getResources(), bitmap);
    }

    private void setupExpandAnimation() {
        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // We call collapseGroupWithAnimation(int) and
                // expandGroupWithAnimation(int) to animate group
                // expansion/collapse.
                if (listView.isGroupExpanded(groupPosition)) {
                    listView.collapseGroupWithAnimation(groupPosition);
                } else {
                    listView.expandGroupWithAnimation(groupPosition);
                }
                return true;
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_help, menu);
        setMenuItemIcon(menu.findItem(R.id.action_help_collapse), R.string.icon_label_action_collapse);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Home button in the action bar closes the activity
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        else if(id == R.id.action_help_collapse){
            collapseAll();
        }
        return super.onOptionsItemSelected(item);
    }

    private void collapseAll() {
        for (int i = 0; i < adapter.getGroupCount(); i++) {
            listView.collapseGroup(i);
            adapter.notifyDataSetChanged();
        }
    }

    /*This method converts into a Bitmap a FontelloTextView,
     * the text of the View is the string referenced by the parameter "iconResId"
     * then it sets the Bitmap as the icon for the menu item passed as the parameter "item"*/
    private void setMenuItemIcon(MenuItem item, int iconResId){
        FontelloTextView actionBarTextLogo = (FontelloTextView) getLayoutInflater().inflate(R.layout.template_toolbar_action_icon, null);
        actionBarTextLogo.setText(getString(iconResId));
        actionBarTextLogo.measure(0, 0);
        actionBarTextLogo.layout(0, 0, actionBarTextLogo.getMeasuredWidth(),actionBarTextLogo.getMeasuredHeight());
        Bitmap bitmap = Bitmap.createBitmap(actionBarTextLogo.getMeasuredWidth(),actionBarTextLogo.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        actionBarTextLogo.draw(canvas);
        BitmapDrawable actionBarLogo = new BitmapDrawable(getResources(), bitmap);
        item.setIcon(actionBarLogo);
    }
}
