package com.henallux.digiway;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;

public class LoginActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        View logo = findViewById(R.id.app_start_logo);

        TranslateAnimation animLogo = new TranslateAnimation(logo.getX(), 0 , logo.getY() + 200, 0 );

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);

        AnimationSet animSetLogo = new AnimationSet(true);

        animSetLogo.setDuration(1000);

        alphaAnimation.setDuration(1250);

        animSetLogo.addAnimation(alphaAnimation);
        animSetLogo.addAnimation(animLogo);

        logo.startAnimation(animSetLogo);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
    }
}
