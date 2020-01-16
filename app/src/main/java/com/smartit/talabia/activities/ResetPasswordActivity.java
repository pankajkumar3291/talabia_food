package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EOForgetPassword;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.GlobalUtil;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.FORGET_PASSWORD;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title, tv_reset;
    private TalabiaEditText et_email_address, et_phone_number;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;

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

        setContentView(R.layout.activity_reset_password);

        this.initView();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.apiInterface = RestClient.getClient();
        this.progress = new GlobalProgressDialog(this);
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        //TODO here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);

        this.et_email_address = this.findViewById(R.id.et_email_address);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.tv_reset = this.findViewById(R.id.tv_reset);

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.tv_reset.setOnClickListener(this);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "Reset Password" : "إعادة تعيين كلمة المرور");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.tv_reset:
                if (isValidFields()) {
                    this.forgetPassword();
                }
                break;
        }
    }

    private void forgetPassword() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.forgetPassword(FORGET_PASSWORD.concat(this.languageId), et_email_address.getText().toString().trim()).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword forgetPassword = response.body();
                        if (!ObjectUtil.isEmpty(forgetPassword)) {
                            if (forgetPassword.getIsSuccess()) {
                                Toast.makeText(ResetPasswordActivity.this, "" + forgetPassword.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent recoveryIntent = new Intent(ResetPasswordActivity.this, RecoveryCodeActivity.class);
                                recoveryIntent.putExtra("userData", forgetPassword.getPayLoad());
                                startActivity(recoveryIntent);
                                ResetPasswordActivity.this.finish();
                            } else {
                                Toast.makeText(ResetPasswordActivity.this, "" + forgetPassword.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(ResetPasswordActivity.this, false, true) {
                            @Override
                            public void onDefault() {
                                super.onDefault();
                            }
                        }.show(R.string.server_is_under_maintenance);
                    }
                }
            });
        }
    }

    private boolean isValidFields() {
        String errorMsg = null;

        String emailId = ObjectUtil.getTextFromView(et_email_address);
        String phoneNumber = ObjectUtil.getTextFromView(et_phone_number);

        if (ObjectUtil.isEmptyStr(emailId) /*|| ObjectUtil.isEmptyStr ( phoneNumber )*/) {
            errorMsg = this.getString(R.string.enter_email_or_mobile);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        }
        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
