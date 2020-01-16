package com.smartit.talabia.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.widget.Toast;

import com.facebook.appevents.AppEventsLogger;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.util.Constants;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

public class GlobalApplication extends Application {

    private SessionSecuredPreferences languagePref;
    private SessionSecuredPreferences loginPref;

    private String packageName;
    private Typeface fontAwesomeTypeface;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext ( LocalizationHelper.onAttach ( base, "en" ) );
    }

    @Override
    public void onCreate() {
        super.onCreate ( );

        //TODO for facebook login
        AppEventsLogger.activateApp ( this );

        this.setAppInstance ( );
    }

    protected void setAppInstance() {
        ApplicationHelper.setApplicationObj ( this );
    }

    public Typeface getTypeface() {
        if(this.fontAwesomeTypeface == null) {
            this.initIcons ( );
        }
        return this.fontAwesomeTypeface;
    }

    public void initIcons() {
        this.fontAwesomeTypeface = Typeface.createFromAsset ( this.getAssets ( ), Constants.ICON_FILE );
    }


    public Context getContext() {
        return this.getApplicationContext ( );
    }

    public String phoneFormat() {
        return Constants.phoneFormat;
    }

    public SessionSecuredPreferences languagePreferences(String preferenceName) {
        if(this.languagePref == null) {
            this.languagePref = new SessionSecuredPreferences ( this.getBaseContext ( ), this.getBaseContext ( ).getSharedPreferences ( preferenceName, Context.MODE_PRIVATE ) );
        }
        return this.languagePref;
    }

    public SessionSecuredPreferences loginPreferences(String preferenceName) {
        if(this.loginPref == null) {
            this.loginPref = new SessionSecuredPreferences ( this.getBaseContext ( ), this.getBaseContext ( ).getSharedPreferences ( preferenceName, Context.MODE_PRIVATE ) );
        }
        return this.loginPref;
    }

    public int getResourceId(String resourceName, String defType) {
        return getResourceId ( resourceName, defType, this.packageName ( ) );
    }

    public int getResourceId(String resourceName, String defType, String packageName) {
        return (!ObjectUtil.isNumber ( resourceName ) && ObjectUtil.isNonEmptyStr ( resourceName )) ? this.getResources ( ).getIdentifier ( resourceName, defType, packageName ) : 0;
    }

    public PackageInfo packageInfo() {
        PackageManager manager = this.getPackageManager ( );
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo ( this.getPackageName ( ), 0 );
        } catch (PackageManager.NameNotFoundException e2) {
            Toast.makeText ( this.getBaseContext ( ), "Package Not Found : " + e2, Toast.LENGTH_SHORT ).show ( );
        }
        return info;
    }

    public String packageName() {
        if(this.packageName == null) {
            PackageInfo info = this.packageInfo ( );
            this.packageName = info != null ? info.packageName : "com.smartit.talabia";
        }
        return this.packageName;
    }

    @Override
    public void onTerminate() {
        super.onTerminate ( );
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged ( newConfig );
        LocalizationHelper.setLocale ( this );
    }

}
