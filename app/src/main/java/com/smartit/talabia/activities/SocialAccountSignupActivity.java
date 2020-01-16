package com.smartit.talabia.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.hbb20.CountryCodePicker;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EOFacebook;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.GlobalUtil;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import java.util.Calendar;

import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class SocialAccountSignupActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private GlobalProgressDialog progress;
    private RestClient.APIInterface apiInterface;
    private TalabiaEditText et_first_name, et_last_name, et_email_address, et_phone_number;
    private CountryCodePicker countryCodePicker;
    private static TalabiaEditText et_date_of_birth;
    private TalabiaTextView tv_sign_up;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private String countryCode, mobileNumber;
    private EOFacebook eoFacebook;

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

        setContentView(R.layout.activity_social_account_signup);

        if (!ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("fbObject"))) {
            this.eoFacebook = (EOFacebook) this.getIntent().getSerializableExtra("fbObject");
        }

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
        this.countryCodePicker = this.findViewById(R.id.countryCodePicker);
        this.tv_sign_up = this.findViewById(R.id.tv_sign_up);
    }

    private void setOnClickListener() {
        this.tv_sign_up.setOnClickListener(this);
        et_date_of_birth.setOnClickListener(this);
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
            case R.id.tv_sign_up:
                //this.startActivity ( new Intent ( this, LoginActivity.class ) );

                break;
            case R.id.et_date_of_birth:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
        }
    }


    private boolean isValidRegistration() {
        String errorMsg = null;

        String firstName = ObjectUtil.getTextFromView(et_first_name);
        String lastName = ObjectUtil.getTextFromView(et_last_name);
        String emailId = ObjectUtil.getTextFromView(et_email_address);
        String dob = ObjectUtil.getTextFromView(et_date_of_birth);
        String phoneNumber = ObjectUtil.getTextFromView(et_phone_number);

        if (ObjectUtil.isEmptyStr(firstName) || ObjectUtil.isEmptyStr(lastName) || ObjectUtil.isEmptyStr(emailId)
                || ObjectUtil.isEmptyStr(dob) || ObjectUtil.isEmptyStr(phoneNumber)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (phoneNumber.length() != 10) {
            errorMsg = this.getString(R.string.valid_phone_number);
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
