package com.smartit.talabia.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.hbb20.CountryCodePicker;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EORegister;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.GlobalUtil;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.REGISTER_CUSTOMER;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private TalabiaEditText et_first_name, et_last_name, et_email_address, et_phone_number, et_password, et_confirm_password;
    private static TalabiaEditText et_date_of_birth;
    private CountryCodePicker countryCodePicker;
    private TalabiaTextView tv_sign_up, tv_sign_in;
    private String countryCode, mobileNumber;
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

        setContentView(R.layout.activity_signup);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        this.apiInterface = RestClient.getClient();
        this.progress = new GlobalProgressDialog(this);
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email_address = this.findViewById(R.id.et_email_address);
        et_date_of_birth = this.findViewById(R.id.et_date_of_birth);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.et_password = this.findViewById(R.id.et_password);
        this.et_confirm_password = this.findViewById(R.id.et_confirm_password);
        this.countryCodePicker = this.findViewById(R.id.countryCodePicker);
        this.tv_sign_up = this.findViewById(R.id.tv_sign_up);
        this.tv_sign_in = this.findViewById(R.id.tv_sign_in);
    }

    private void setOnClickListener() {
        et_date_of_birth.setOnClickListener(this);
        this.tv_sign_up.setOnClickListener(this);
        this.tv_sign_in.setOnClickListener(this);
        this.countryCodePicker.setOnCountryChangeListener(this.onCountryChangeListener);
    }

    private CountryCodePicker.OnCountryChangeListener onCountryChangeListener = new CountryCodePicker.OnCountryChangeListener() {
        @Override
        public void onCountrySelected() {
            countryCode = countryCodePicker.getSelectedCountryCodeWithPlus();
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_date_of_birth:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.tv_sign_up:
                if (isValidRegistration()) {
                    mobileNumber = et_phone_number.getText().toString().trim();
                    if (ObjectUtil.isEmpty(countryCode)) {
                        mobileNumber = countryCodePicker.getDefaultCountryCodeWithPlus().concat(" ").concat(mobileNumber);
                    } else {
                        mobileNumber = countryCode.concat(" ").concat(mobileNumber);
                    }
                    registerUser();
                }
                break;
            case R.id.tv_sign_in:
                this.finish();
                break;
        }
    }

    private void registerUser() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.registerCustomer(REGISTER_CUSTOMER.concat(this.languageId), et_first_name.getText().toString().trim(), et_last_name.getText().toString().trim(),
                    et_email_address.getText().toString().trim(), et_password.getText().toString().trim(), this.mobileNumber, et_date_of_birth.getText().toString().trim()).enqueue(new Callback<EORegister>() {
                @Override
                public void onResponse(Call<EORegister> call, Response<EORegister> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EORegister eoRegister = response.body();
                        if (!ObjectUtil.isEmpty(eoRegister)) {
                            if (eoRegister.getIsSuccess()) {
                                //Toast.makeText(SignupActivity.this, "" + eoRegister.getMessage(), Toast.LENGTH_SHORT).show();
                                new GlobalAlertDialog(SignupActivity.this, false, false) {
                                    @Override
                                    public void onDefault() {
                                        super.onDefault();
                                    }
                                }.show(eoRegister.getMessage());
                                Intent verifyIntent = new Intent(SignupActivity.this, VerifyAccountActivity.class);
                                verifyIntent.putExtra("userId", eoRegister.getPayload().getUser_id());
                                SignupActivity.this.startActivity(verifyIntent);
                                SignupActivity.this.finish();
                            } else {
                                //Toast.makeText(SignupActivity.this, "" + eoRegister.getMessage(), Toast.LENGTH_SHORT).show();
                                new GlobalAlertDialog(SignupActivity.this, false, false) {
                                    @Override
                                    public void onDefault() {
                                        super.onDefault();
                                    }
                                }.show(eoRegister.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EORegister> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(SignupActivity.this, false, true) {
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

    private boolean isValidRegistration() {
        String errorMsg = null;

        String firstName = ObjectUtil.getTextFromView(et_first_name);
        String lastName = ObjectUtil.getTextFromView(et_last_name);
        String emailId = ObjectUtil.getTextFromView(et_email_address);
        String dob = ObjectUtil.getTextFromView(et_date_of_birth);
        String phoneNumber = ObjectUtil.getTextFromView(et_phone_number);
        String password = ObjectUtil.getTextFromView(et_password);
        String confirmPassword = ObjectUtil.getTextFromView(et_confirm_password);

        if (ObjectUtil.isEmptyStr(firstName) || ObjectUtil.isEmptyStr(lastName) || ObjectUtil.isEmptyStr(emailId)
                || ObjectUtil.isEmptyStr(dob) || ObjectUtil.isEmptyStr(phoneNumber) || ObjectUtil.isEmptyStr(password)
                || ObjectUtil.isEmptyStr(confirmPassword)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (phoneNumber.length() != 10) {
            errorMsg = this.getString(R.string.valid_phone_number);
        } else if (!password.equals(confirmPassword)) {
            errorMsg = this.getString(R.string.confirm_password_not_matched);
        } else if (password.length() < 8) {
            errorMsg = this.getString(R.string.password_min_character);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
            dialog.getDatePicker() /*.setMaxDate ( c.getTimeInMillis ( ) )*/;
            return dialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            et_date_of_birth.setClickable(true);
            et_date_of_birth.setText(String.valueOf(year).concat("/").concat(String.valueOf(month + 1)).concat("/").concat(String.valueOf(dayOfMonth)));
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
