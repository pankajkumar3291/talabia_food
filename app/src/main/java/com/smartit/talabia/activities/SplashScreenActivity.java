package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.util.Constants;
import com.smartit.talabia.util.LocalizationHelper;

import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_USER_ID;

public class SplashScreenActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.initView();
    }

    private void initView() {
        SessionSecuredPreferences languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        SessionSecuredPreferences loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);

        //TODO first time to check langId or currencyId contains in shared preferences or not
        if (languagePreferences.contains(Constants.SELECTED_LANG_ID)) {

            if (loginPreferences.contains(SELECTED_USER_ID)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                        finish();
                    }
                }, SPLASH_TIME_OUT);
            }

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreenActivity.this, LanguageActivity.class));
                    finish();
                }
            }, SPLASH_TIME_OUT);
        }
    }

}
