package com.bit45.thespeedtester;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bit45.thespeedtester.ProductTour.ProductTourFragment;
import com.bit45.thespeedtester.managers.GoogleServicesManager;
import com.nineoldandroids.view.ViewHelper;

/* This activity shows slides with a quick tutorial of the app, every slide contains an image,
* an explanation text with title, and buttons to skip tutorial or navigate to the next slide.
* The code was copied from a github repository and modified to add the specific screens
* (XML Layouts) i wanted for the tour and to save a value to shared preferences*/
public class ProductTourActivity extends AppCompatActivity {

    private GoogleServicesManager gsManager;

    //Number of slides of the tour including an "empty" one at the end
    static final int NUM_PAGES = 7;

    /*This fields were not modified in any way by me*/
    ViewPager pager;
    PagerAdapter pagerAdapter;
    LinearLayout circles;
    Button skip;
    Button done;
    ImageButton next;
    boolean isOpaque = true;

    /*Method modified*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        setContentView(R.layout.activity_tour);

//      initialize Google services manager (used for Admob and Google Analytics)
        //gsManager = new GoogleServicesManager(this);
//      initialize G-Analytics Tracker
        //gsManager.initializeTracker();

        skip = Button.class.cast(findViewById(R.id.skip));
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //gsManager.sendEvent("Action Tour", "SKIP");
                endTutorial();
            }
        });

        next = ImageButton.class.cast(findViewById(R.id.next));
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                //gsManager.sendEvent("Action Tour", "NEXT");
            }
        });

        done = Button.class.cast(findViewById(R.id.done));
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTutorial();
                //gsManager.sendEvent("Action Tour", "DONE");
            }
        });

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
        pager.setPageTransformer(true, new CrossfadePageTransformer());
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == NUM_PAGES - 2 && positionOffset > 0) {
                    if (isOpaque) {
                        pager.setBackgroundColor(Color.TRANSPARENT);
                        isOpaque = false;
                    }
                } else {
                    if (!isOpaque) {
                        pager.setBackgroundColor(getResources().getColor(R.color.primary_material_light));
                        isOpaque = true;
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
                if (position == NUM_PAGES - 2) {
                    skip.setVisibility(View.GONE);
                    next.setVisibility(View.GONE);
                    done.setVisibility(View.VISIBLE);
                } else if (position < NUM_PAGES - 2) {
                    skip.setVisibility(View.VISIBLE);
                    next.setVisibility(View.VISIBLE);
                    done.setVisibility(View.GONE);
                } else if (position == NUM_PAGES - 1) {
                    endTutorial();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        buildCircles();

        /*Set the first_time value to false (in SharedPreferences) to indicate the tour has been seen once*/
        /*SharedPreferences prefs = getSharedPreferences("SpeedTestPrefs", ProductTourActivity.MODE_PRIVATE);
        SharedPreferences.Editor editPref = prefs.edit();
        editPref.putBoolean("first_time", false);
        editPref.apply();*/
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pager!=null){
            pager.clearOnPageChangeListeners();
        }
    }

    private void buildCircles(){
        circles = LinearLayout.class.cast(findViewById(R.id.circles));

        float scale = getResources().getDisplayMetrics().density;
        int padding = (int) (5 * scale + 0.5f);

        for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
            ImageView circle = new ImageView(this);
            circle.setImageResource(R.drawable.ic_swipe_indicator_white_18dp);
            circle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            circle.setAdjustViewBounds(true);
            circle.setPadding(padding, 0, padding, 0);
            circles.addView(circle);
        }

        setIndicator(0);
    }

    private void setIndicator(int index){
        if(index < NUM_PAGES){
            for(int i = 0 ; i < NUM_PAGES - 1 ; i++){
                ImageView circle = (ImageView) circles.getChildAt(i);
                if(i == index){
                    circle.setColorFilter(getResources().getColor(R.color.text_selected));
                }else {
                    circle.setColorFilter(getResources().getColor(android.R.color.transparent));
                }
            }
        }
    }

    private void endTutorial(){
        finish();
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            pager.setCurrentItem(pager.getCurrentItem() - 1);
        }
    }

    /*Method modified in this class*/
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*Method modified*/
        @Override
        public Fragment getItem(int position) {
            ProductTourFragment tp;
            switch(position){
                /*Add the layouts for every slide of the tour*/
                case 0:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment01);
                    break;
                case 1:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment02);
                    break;
                case 2:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment03);
                    break;
                case 3:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment04);
                    break;
                case 4:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment05);
                    break;
                case 5:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment06);
                    break;
                /*The last fragment is just an empty one, use it to have a background while
                 * sliding the last fragment out of the tour, this fragment is taken
                 * into consideration for the NUM_PAGES value*/
                default:
                    tp = ProductTourFragment.newInstance(R.layout.tour_fragment_empty);
                    break;
            }

            return tp;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    /*Method modified in this class*/
    public class CrossfadePageTransformer implements ViewPager.PageTransformer {

        /*Method modified*/
        @Override
        public void transformPage(View page, float position) {
            int pageWidth = page.getWidth();

            View backgroundView = page.findViewById(R.id.welcome_fragment);
            View text_head= page.findViewById(R.id.heading);
            View text_content = page.findViewById(R.id.content);
            /*Instantiate every slide layout*/
            View welcomeImage01 = page.findViewById(R.id.welcome_01);
            View welcomeImage02 = page.findViewById(R.id.welcome_02);
            View welcomeImage03 = page.findViewById(R.id.welcome_03);
            View welcomeImage04 = page.findViewById(R.id.welcome_04);
            View welcomeImage05 = page.findViewById(R.id.welcome_05);
            View welcomeImage06 = page.findViewById(R.id.welcome_06);

            if(0 <= position && position < 1){
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }
            if(-1 < position && position < 0){
                ViewHelper.setTranslationX(page, pageWidth * -position);
            }

            if(position <= -1.0f || position >= 1.0f) {
            } else if( position == 0.0f ) {
            } else {
                if(backgroundView != null) {
                    ViewHelper.setAlpha(backgroundView, 1.0f - Math.abs(position));

                }

                if (text_head != null) {
                    ViewHelper.setTranslationX(text_head, pageWidth * position);
                    ViewHelper.setAlpha(text_head, 1.0f - Math.abs(position));
                }

                if (text_content != null) {
                    ViewHelper.setTranslationX(text_content, pageWidth * position);
                    ViewHelper.setAlpha(text_content, 1.0f - Math.abs(position));
                }

                /*Add this "if" statement code for every slide layout using the right variables*/
                if (welcomeImage01 != null) {
                    ViewHelper.setTranslationX(welcomeImage01, (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage01, 1.0f - Math.abs(position));
                }

                if (welcomeImage02 != null) {
                    ViewHelper.setTranslationX(welcomeImage02, (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage02, 1.0f - Math.abs(position));
                }

                if (welcomeImage03 != null) {
                    ViewHelper.setTranslationX(welcomeImage03, (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage03, 1.0f - Math.abs(position));
                }

                if (welcomeImage04 != null) {
                    ViewHelper.setTranslationX(welcomeImage04, (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage04, 1.0f - Math.abs(position));
                }

                if (welcomeImage05 != null) {
                    ViewHelper.setTranslationX(welcomeImage05, (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage05, 1.0f - Math.abs(position));
                }

                if (welcomeImage06 != null) {
                    ViewHelper.setTranslationX(welcomeImage06, (float) (pageWidth / 2 * position));
                    ViewHelper.setAlpha(welcomeImage06, 1.0f - Math.abs(position));
                }

            }
        }
    }

}
