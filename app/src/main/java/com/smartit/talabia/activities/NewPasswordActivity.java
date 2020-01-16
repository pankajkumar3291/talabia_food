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
import com.smartit.talabia.entity.account.EOForgetPayload;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.RESET_PASSWORD;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;
import static com.smartit.talabia.util.Constants.SELECTED_USER_ID;

public class NewPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title, tv_done;
    private TalabiaEditText et_password, et_confirm_password;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private SessionSecuredPreferences loginPreferences;
    private int userId;
    private EOForgetPayload eoForgetPayload;

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

        setContentView(R.layout.activity_new_password);

        if (!ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("userData"))) {
            this.eoForgetPayload = (EOForgetPayload) this.getIntent().getSerializableExtra("userData");
        }

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
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.userId = loginPreferences.getInt(SELECTED_USER_ID, 0);
        //TODO here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);

        this.et_password = this.findViewById(R.id.et_password);
        this.et_confirm_password = this.findViewById(R.id.et_confirm_password);
        this.tv_done = this.findViewById(R.id.tv_done);
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.tv_done.setOnClickListener(this);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "New Password" : "كلمة السر الجديدة");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.tv_done:
                if (isValidFields()) {
                    if (!ObjectUtil.isEmpty(this.eoForgetPayload)) {
                        setNewPassword();
                    }
                }
                break;
        }
    }

    private void setNewPassword() {
        if (!ObjectUtil.isEmpty(this.languageId) && !ObjectUtil.isEmpty(this.eoForgetPayload.getEmail())) {
            progress.showProgressBar();
            this.apiInterface.resetPassword(RESET_PASSWORD.concat(this.languageId), this.eoForgetPayload.getEmail(), et_password.getText().toString().trim()).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword forgetPassword = response.body();
                        if (!ObjectUtil.isEmpty(forgetPassword)) {
                            if (forgetPassword.getIsSuccess()) {
                                Toast.makeText(NewPasswordActivity.this, "" + forgetPassword.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent loginIntent = new Intent(NewPasswordActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                                NewPasswordActivity.this.finish();
                            } else {
                                Toast.makeText(NewPasswordActivity.this, "" + forgetPassword.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(NewPasswordActivity.this, false, true) {
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

        String password = ObjectUtil.getTextFromView(et_password);
        String confirmPassword = ObjectUtil.getTextFromView(et_confirm_password);

        if (ObjectUtil.isEmptyStr(password) || ObjectUtil.isEmptyStr(confirmPassword)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (password.length() < 8) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (confirmPassword.length() < 8) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (!password.equals(confirmPassword)) {
            errorMsg = this.getString(R.string.confirm_password_not_matched);
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
