package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.util.Constants;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import java.util.Objects;

import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANGUAGE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title;
    private LinearLayout layout_my_profile, layout_terms_conditions, layout_privacy_policy, layout_share_app, layout_contact_us_feedback;
    private Spinner spinner_language;
    private Switch switch_notification;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    boolean userIsInteracting = false;

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

        setContentView(R.layout.activity_settings);

        this.initVie();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initVie() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        //TODO here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);

        this.layout_my_profile = this.findViewById(R.id.layout_my_profile);
        this.spinner_language = this.findViewById(R.id.spinner_language);
        this.switch_notification = this.findViewById(R.id.switch_notification);
        this.layout_terms_conditions = this.findViewById(R.id.layout_terms_conditions);
        this.layout_privacy_policy = this.findViewById(R.id.layout_privacy_policy);
        this.layout_share_app = this.findViewById(R.id.layout_share_app);
        this.layout_contact_us_feedback = this.findViewById(R.id.layout_contact_us_feedback);
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.layout_my_profile.setOnClickListener(this);
        this.layout_terms_conditions.setOnClickListener(this);
        this.layout_privacy_policy.setOnClickListener(this);
        this.layout_share_app.setOnClickListener(this);
        this.layout_contact_us_feedback.setOnClickListener(this);
        this.switch_notification.setOnCheckedChangeListener(this.onCheckedChangeListener);
        this.spinner_language.setOnItemSelectedListener(onLanguageSelectedListener);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "Settings" : "الإعدادات");
        }

        if (Objects.requireNonNull(languagePreferences.getString(SELECTED_LANGUAGE, "")).equalsIgnoreCase("Arabic")) {
            this.spinner_language.setSelection(1);
        } else {
            this.spinner_language.setSelection(0);
        }
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                Toast.makeText(SettingsActivity.this, "Notification is : " + buttonView.isChecked(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Notification is  : " + buttonView.isChecked(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    AdapterView.OnItemSelectedListener onLanguageSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

            if (userIsInteracting) {
                String lang = position == 0 ? "en" : "ar";
                LocalizationHelper.setLocale(SettingsActivity.this, lang);
                SessionSecuredPreferences languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
                languagePreferences.edit().putString(Constants.SELECTED_LANGUAGE, parentView.getSelectedItem().toString()).apply();
                languagePreferences.edit().putString(Constants.SELECTED_LANG_ID, lang).apply();
                MainActivity.mainActivity.get().finish(); //TODO here finish mainActivity for refresh while language change
                Intent intent = new Intent(SettingsActivity.this, SplashScreenActivity.class);
                startActivity(intent);
                SettingsActivity.this.finish();

//                Intent intent = getBaseContext ( ).getPackageManager ( ).getLaunchIntentForPackage ( getBaseContext ( ).getPackageName ( ) );
//                intent.addFlags ( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//                startActivity ( intent );
//                Runtime.getRuntime ( ).exit ( 0 );

            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.layout_my_profile:
                this.startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.layout_terms_conditions:
                break;
            case R.id.layout_privacy_policy:
                break;
            case R.id.layout_share_app:
                break;
            case R.id.layout_contact_us_feedback:
                break;
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        userIsInteracting = true;
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
