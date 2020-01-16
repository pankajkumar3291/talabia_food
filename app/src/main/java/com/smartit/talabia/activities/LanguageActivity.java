package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.util.Constants;
import com.smartit.talabia.util.LocalizationHelper;

import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private TextView tv_english, tv_arabic;
    private SessionSecuredPreferences languagePreferences;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocalizationHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO set layout direction on language change
        if (ARABIC_LANGUAGE_ID.equals(LocalizationHelper.getLanguage(this))) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        setContentView(R.layout.activity_language);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.tv_english = this.findViewById(R.id.tv_english);
        this.tv_arabic = this.findViewById(R.id.tv_arabic);
        Typeface englishTypeface = Typeface.createFromAsset(this.getAssets(), "gill_sans_mt.ttf");
        Typeface arabicTypeface = Typeface.createFromAsset(this.getAssets(), "ge_ss_two_Light_arabic.otf");
        this.tv_english.setTypeface(englishTypeface);
        this.tv_arabic.setTypeface(arabicTypeface);
    }

    private void setOnClickListener() {
        this.tv_english.setOnClickListener(this);
        this.tv_arabic.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_english:
                languagePreferences.edit().putString(Constants.SELECTED_LANG_ID, ENGLISH_LANGUAGE_ID).apply();
                languagePreferences.edit().putString(Constants.SELECTED_LANGUAGE, "English").apply();
                LocalizationHelper.setLocale(this, ENGLISH_LANGUAGE_ID);
                this.startActivity(new Intent(this, LoginActivity.class));
                this.finish();
                break;
            case R.id.tv_arabic:
                languagePreferences.edit().putString(Constants.SELECTED_LANG_ID, ARABIC_LANGUAGE_ID).apply();
                languagePreferences.edit().putString(Constants.SELECTED_LANGUAGE, "Arabic").apply();
                LocalizationHelper.setLocale(this, ARABIC_LANGUAGE_ID);
                this.startActivity(new Intent(this, LoginActivity.class));
                this.finish();
                break;
        }

    }

    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

}
