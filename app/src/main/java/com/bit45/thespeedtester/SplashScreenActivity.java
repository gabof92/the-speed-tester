package com.bit45.thespeedtester;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bit45.thespeedtester.kbv.KenBurnsView;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

/*This activity is used to showcase the app logo a few seconds
* before starting but it can be skipped by touching the screen*/
public class SplashScreenActivity extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    // Animation parameters
    private static int LOGO_ANIMATION_DURATION = 600;
    private static int TEXT_ANIMATION_DURATION = 700;
    private static int LOGO_ANIMATION_DELAY = 200;
    private static int TEXT_ANIMATION_DELAY = 100;

    private KenBurnsView mKenBurns;
    private ImageView mLogo;
    private ImageView welcomeText;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE); //Removing ActionBar
        setContentView(R.layout.activity_splash_screen);

        //Initialize KenBurnsView (moving background)
        mKenBurns = (KenBurnsView) findViewById(R.id.ken_burns_images);
        mKenBurns.setImageResource(R.drawable.splash_screen_background);
        //Initialize logo and text
        mLogo = (ImageView) findViewById(R.id.logo);
        welcomeText = (ImageView) findViewById(R.id.welcome_text);

        //Setting text and logo transparent (to make them appear later with animation).
        //We use ninoldandroids ViewHelper class for backwards compatibility
        ViewHelper.setAlpha(mLogo, 0.0F);
        ViewHelper.setAlpha(welcomeText, 0.0F);

        //Set and Start text and logo animations
        setAnimation();

        /* Showing splash screen with a timer. This will
        * be useful to show case the app logo*/
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                startMainApp();
            }
        };

        //If screen is touched start main app
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.splash_content_layout);
        contentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel postDelayed task so the
                //main app wont open twice
                mHandler.removeCallbacks(mRunnable);
                startMainApp();
            }
        });
        //Execute main activity a few seconds later
        mHandler.postDelayed(mRunnable, SPLASH_TIME_OUT);
    }

    @Override
    protected void onDestroy() {
        //cancel postDelayed task so the
        //main app wont open after exiting
        mHandler.removeCallbacks(mRunnable);
        super.onDestroy();
    }

    private void startMainApp(){
        //close this activity first, if we open the other one
        // before closing, the kenburnsview gets a little stuck
        finish();
        //Start new activity
        Intent i = new Intent(SplashScreenActivity.this, SpeedTestActivity.class);
        startActivity(i);
    }

    private void setAnimation() {
            animation1();
            //animation2();
            animation3();
    }

    //Logo starts big and then shrinks to the center of the screen
    private void animation1() {
        ObjectAnimator scaleXAnimation = ObjectAnimator.ofFloat(mLogo, "scaleX", 5.0F, 1.0F);
        scaleXAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleXAnimation.setDuration(LOGO_ANIMATION_DURATION);
        ObjectAnimator scaleYAnimation = ObjectAnimator.ofFloat(mLogo, "scaleY", 5.0F, 1.0F);
        scaleYAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleYAnimation.setDuration(LOGO_ANIMATION_DURATION);
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(mLogo, "alpha", 0.0F, 1.0F);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setDuration(LOGO_ANIMATION_DURATION);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleXAnimation).with(scaleYAnimation).with(alphaAnimation);
        //We set a delay so the animation wont start right away
        animatorSet.setStartDelay(LOGO_ANIMATION_DELAY);
        animatorSet.start();
    }

    //Logo comes from top of the screen to the center
    private void animation2() {
        ViewHelper.setAlpha(mLogo,1.0F);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate_top_to_center);
        mLogo.startAnimation(anim);
    }

    //Text fades in
    private void animation3() {
        ObjectAnimator alphaAnimation = ObjectAnimator.ofFloat(welcomeText, "alpha", 0.0F, 1.0F);
        //we add the logo duration to the delay so the text animation comes after the logo
        alphaAnimation.setStartDelay(LOGO_ANIMATION_DURATION + TEXT_ANIMATION_DELAY);
        alphaAnimation.setDuration(TEXT_ANIMATION_DURATION);
        alphaAnimation.start();
    }
}
