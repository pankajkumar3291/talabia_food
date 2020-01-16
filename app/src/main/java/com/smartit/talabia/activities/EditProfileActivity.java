package com.smartit.talabia.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.fxn.pix.Pix;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.FontAwesomeIcon;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EOForgetPassword;
import com.smartit.talabia.entity.account.EOProfileImage;
import com.smartit.talabia.entity.account.EOUserPayload;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;
import com.smartit.talabia.util.StringUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.UPDATE_PROFILE_PIC;
import static com.smartit.talabia.util.Apies.UPDATE_USER_PROFILE;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.REQUEST_CODE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title, tv_cancel, tv_save_profile;
    private CircleImageView profileImage;
    private FontAwesomeIcon icon_click_image;
    private TalabiaEditText et_first_name, et_last_name, et_email_id, et_phone_number;
    private static TalabiaEditText et_date_of_birth;
    private RadioGroup radio_btn_group;
    private RadioButton radio_male_btn, radio_female_btn;
    private String gender;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private EOUserPayload userDetails;

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

        setContentView(R.layout.activity_edit_profile);

        if (!ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("userDetails"))) {
            this.userDetails = (EOUserPayload) this.getIntent().getSerializableExtra("userDetails");
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
        //TODO here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);
        this.profileImage = this.findViewById(R.id.profileImage);
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        et_date_of_birth = this.findViewById(R.id.et_date_of_birth);
        this.tv_cancel = this.findViewById(R.id.tv_cancel);
        this.tv_save_profile = this.findViewById(R.id.tv_save_profile);

        this.icon_click_image = this.findViewById(R.id.icon_click_image);
        this.radio_btn_group = this.findViewById(R.id.radio_btn_group);
        this.radio_male_btn = this.findViewById(R.id.radio_male_btn);
        this.radio_female_btn = this.findViewById(R.id.radio_female_btn);
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.tv_cancel.setOnClickListener(this);
        this.tv_save_profile.setOnClickListener(this);
        et_date_of_birth.setOnClickListener(this);
        this.icon_click_image.setOnClickListener(this);
        this.radio_btn_group.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    private void dataToView() {
        this.tv_page_title.setText(StringUtil.getStringForID(R.string.edit_profile));
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "Edit Profile" : "تعديل الملف الشخصي");
        }
        this.tv_cancel.setText(ObjectUtil.getTextFromView(this.tv_cancel).toUpperCase());

        if (!ObjectUtil.isEmpty(this.userDetails)) {
            if (!ObjectUtil.isEmpty(userDetails.getFirstName()))
                et_first_name.setText(userDetails.getFirstName());
            if (!ObjectUtil.isEmpty(userDetails.getLastName()))
                et_last_name.setText(userDetails.getLastName());
            if (!ObjectUtil.isEmpty(userDetails.getEmail()))
                et_email_id.setText(userDetails.getEmail());
            if (!ObjectUtil.isEmpty(userDetails.getMobileNo()))
                et_phone_number.setText(userDetails.getMobileNo());
            if (!ObjectUtil.isEmpty(userDetails.getGender())) {
                if (userDetails.getGender().equalsIgnoreCase("M"))
                    radio_male_btn.setChecked(true);
                else
                    radio_female_btn.setChecked(true);
            }
            if (!ObjectUtil.isEmpty(userDetails.getDob())) {
                String dob = userDetails.getDob();
                et_date_of_birth.setText(dob.replace("-", "/"));
            }
            if (!ObjectUtil.isEmpty(userDetails.getThumbnail()))
                loadImages(userDetails.getThumbnail(), profileImage);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.et_date_of_birth:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.icon_click_image:
                openGalleryAndCamera();
                break;
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.tv_cancel:
                this.finish();
                break;
            case R.id.tv_save_profile:
                if (isValidFields()) {
                    if (!ObjectUtil.isEmpty(this.userDetails)) {
                        if (this.userDetails.getUserId() != 0)
                            updateUserProfile(this.userDetails.getUserId());
                    }
                }
                break;
        }
    }

    private void updateUserProfile(int userId) {
        if (!ObjectUtil.isEmpty(this.languageId) && !ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_first_name)) && !ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_last_name))
                && !ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_email_id)) && !ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_phone_number))
                && !ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_date_of_birth))) {
            progress.showProgressBar();
            this.apiInterface.updateProfile(UPDATE_USER_PROFILE.concat(this.languageId), String.valueOf(userId), ObjectUtil.getTextFromView(et_first_name),
                    ObjectUtil.getTextFromView(et_last_name), ObjectUtil.getTextFromView(et_phone_number), ObjectUtil.getTextFromView(et_date_of_birth), gender).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword userDetails = response.body();
                        if (!ObjectUtil.isEmpty(userDetails)) {
                            if (userDetails.getIsSuccess()) {
                                Toast.makeText(EditProfileActivity.this, "" + userDetails.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(EditProfileActivity.this, "" + userDetails.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(EditProfileActivity.this, false, true) {
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

        if (ObjectUtil.isEmptyStr(gender) || ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_first_name)) || ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_last_name))
                || ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_email_id)) || ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_phone_number))) {
            errorMsg = this.getString(R.string.all_fields_required);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void openGalleryAndCamera() {
        Pix.start(EditProfileActivity.this, REQUEST_CODE, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ObjectUtil.isEmpty(data) && resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            File file = new File(resultArray.get(0));
            Bitmap bitmap = new BitmapDrawable(getApplication().getResources(), file.getAbsolutePath()).getBitmap();
            uploadImageOnServer(persistImage(bitmap));
        }
    }

    private void uploadImageOnServer(File path) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), path);
        MultipartBody.Part body = MultipartBody.Part.createFormData("thumbnail", path.getName(), reqFile);

        if (!ObjectUtil.isEmpty(this.userDetails)) {
            if (!ObjectUtil.isEmpty(this.languageId) && !ObjectUtil.isEmpty(body) && !ObjectUtil.isEmpty(this.userDetails.getUserId())) {
                progress.showProgressBar();
                this.apiInterface.uploadProfileImage(UPDATE_PROFILE_PIC.concat(this.languageId), String.valueOf(this.userDetails.getUserId()), body).enqueue(new Callback<EOProfileImage>() {
                    @Override
                    public void onResponse(Call<EOProfileImage> call, Response<EOProfileImage> response) {
                        progress.hideProgressBar();

                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOProfileImage eoProfileImage = response.body();
                            if (!ObjectUtil.isEmpty(profileImage)) {
                                if (eoProfileImage.getIsSuccess()) {
                                    Toast.makeText(EditProfileActivity.this, "" + eoProfileImage.getMessage(), Toast.LENGTH_SHORT).show();
                                    if (!ObjectUtil.isEmpty(eoProfileImage.getPayLoad()))
                                        loadImages(eoProfileImage.getPayLoad(), profileImage);
                                } else {
                                    Toast.makeText(EditProfileActivity.this, "" + eoProfileImage.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOProfileImage> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            new GlobalAlertDialog(EditProfileActivity.this, false, true) {
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
    }

    private File persistImage(Bitmap bitmap) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, StringUtil.getStringForID(R.string.app_name) + ".jpg");
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.ic_profile)
                .fit()
                .into(imageView);
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            RadioButton checkedRadioButton = group.findViewById(checkedId);
            switch (checkedRadioButton.getId()) {
                case R.id.radio_male_btn:
                    gender = "M";
                    return;
                case R.id.radio_female_btn:
                    gender = "F";
            }
        }
    };

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
