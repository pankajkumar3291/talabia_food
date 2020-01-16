package com.smartit.talabia.dialog;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.FontAwesomeIcon;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.util.UIUtil;


public class NetworkDialog extends GlobalAlertDialog {

    public NetworkDialog(Context context) {
        super(context, true, false);
    }

    @Override
    protected int layoutID() {
        return R.layout.dialog_network;
    }

    @Override
    protected void loadHeader() {
        super.loadHeader();
        FontAwesomeIcon wifiIcon = findViewById(R.id.titleIcon);
        wifiIcon.setText(R.string.icon_wifi);
        wifiIcon.setAnimation( UIUtil.animBlink());
        wifiIcon.setTextColor(UIUtil.getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void loadBody() {

    }

    @Override
    protected void loadFooter() {
        this.setPositiveBtnTxt("Go Settings");
        super.loadFooter();
    }

    @Override
    public void onConfirmation() {
        ApplicationHelper.application().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }

}
