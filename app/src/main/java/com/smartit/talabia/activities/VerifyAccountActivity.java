package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.smartit.talabia.entity.account.EOVerifyUser;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;
import com.smartit.talabia.util.UIUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.VERIFY_CUSTOMER;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;
import static com.smartit.talabia.util.Constants.SELECTED_USER_ID;

public class VerifyAccountActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title, tv_verify;
    private TalabiaEditText et_text_1, et_text_2, et_text_3, et_text_4;
    private int userId;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private SessionSecuredPreferences loginPreferences;

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

        setContentView(R.layout.activity_verify_account);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("userId", 0))) {
            this.userId = this.getIntent().getIntExtra("userId", 0);
        }

        this.initView();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiInterface = RestClient.getClient();
        this.progress = new GlobalProgressDialog(this);
        //TODO here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);

        this.et_text_1 = this.findViewById(R.id.et_text_1);
        this.et_text_2 = this.findViewById(R.id.et_text_2);
        this.et_text_3 = this.findViewById(R.id.et_text_3);
        this.et_text_4 = this.findViewById(R.id.et_text_4);
        this.tv_verify = this.findViewById(R.id.tv_verify);

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.tv_verify.setOnClickListener(this);

        this.et_text_1.addTextChangedListener(textChangeListener1);
        this.et_text_2.addTextChangedListener(textChangeListener2);
        this.et_text_3.addTextChangedListener(textChangeListener3);
        this.et_text_4.addTextChangedListener(textChangeListener4);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "Verify Account" : "التحقق من الحساب");
        }
        UIUtil.showKeyBoard(this, this.et_text_1);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.tv_verify:
                if (isValidAccount()) {
                    if (this.userId != 0) {
                        String finalOtp = ObjectUtil.getTextFromView(et_text_1).concat(ObjectUtil.getTextFromView(et_text_2))
                                .concat(ObjectUtil.getTextFromView(et_text_3)).concat(ObjectUtil.getTextFromView(et_text_4));
                        verifyUser(finalOtp);
                    }
                }
                break;
        }
    }

    private void verifyUser(String otp) {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.verifyCustomer(VERIFY_CUSTOMER.concat(this.languageId), otp, String.valueOf(this.userId)).enqueue(new Callback<EOVerifyUser>() {
                @Override
                public void onResponse(Call<EOVerifyUser> call, Response<EOVerifyUser> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOVerifyUser eoVerifyUser = response.body();
                        if (!ObjectUtil.isEmpty(eoVerifyUser)) {
                            if (eoVerifyUser.getIsSuccess()) {
                                Toast.makeText(VerifyAccountActivity.this, "" + eoVerifyUser.getMessage(), Toast.LENGTH_SHORT).show();
                                //TODO save user_id in shared preference
                                loginPreferences.edit().putInt(SELECTED_USER_ID, userId).apply();
                                Intent mainIntent = new Intent(VerifyAccountActivity.this, MainActivity.class);
                                VerifyAccountActivity.this.startActivity(mainIntent);
                                VerifyAccountActivity.this.finish();
                            } else {
                                Toast.makeText(VerifyAccountActivity.this, "" + eoVerifyUser.getMessage(), Toast.LENGTH_SHORT).show();
                                clearEditText();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOVerifyUser> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(VerifyAccountActivity.this, false, true) {
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

    private boolean isValidAccount() {
        String errorMsg = null;
        String text1 = ObjectUtil.getTextFromView(et_text_1);
        String text2 = ObjectUtil.getTextFromView(et_text_2);
        String text3 = ObjectUtil.getTextFromView(et_text_3);
        String text4 = ObjectUtil.getTextFromView(et_text_4);

        if (ObjectUtil.isEmptyStr(text1) || ObjectUtil.isEmptyStr(text2) || ObjectUtil.isEmptyStr(text3)
                || ObjectUtil.isEmptyStr(text4)) {
            errorMsg = this.getString(R.string.all_fields_required);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private TextWatcher textChangeListener1 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_text_1.getText().toString().length() == 1) {
                et_text_2.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textChangeListener2 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_text_2.getText().toString().length() == 1) {
                et_text_3.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextWatcher textChangeListener3 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_text_3.getText().toString().length() == 1) {
                et_text_4.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    private TextWatcher textChangeListener4 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (et_text_4.getText().toString().length() == 1) {
                et_text_4.setCursorVisible(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_text_4.getWindowToken(), 0);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void clearEditText() {
        this.et_text_1.setText("");
        this.et_text_2.setText("");
        this.et_text_3.setText("");
        this.et_text_4.setText("");

        this.et_text_1.requestFocus();
        UIUtil.showKeyBoard(this, this.et_text_1);

        this.et_text_1.addTextChangedListener(textChangeListener1);
        this.et_text_2.addTextChangedListener(textChangeListener2);
        this.et_text_3.addTextChangedListener(textChangeListener3);
        this.et_text_4.addTextChangedListener(textChangeListener4);
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
