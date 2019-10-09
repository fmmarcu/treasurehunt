package com.threess.summership.treasurehunt.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.threess.summership.treasurehunt.R;
import com.threess.summership.treasurehunt.view.animations.ProgressBarAnimation;


public class SplashScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //Makes the splash screen fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ProgressBar progressBar;
        progressBar = findViewById(R.id.progressBar);
        ProgressBarAnimation progressBarAnimation = new ProgressBarAnimation(progressBar);
        progressBarAnimation.setDuration(getResources().getInteger(R.integer.progressBarAnimationDuration));

        progressBarAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //NOP
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                //NOP
            }
        });
        progressBar.setAnimation(progressBarAnimation);

    }

}
