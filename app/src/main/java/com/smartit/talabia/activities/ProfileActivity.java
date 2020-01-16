package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EOUserDetails;
import com.smartit.talabia.entity.account.EOUserPayload;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;
import com.smartit.talabia.util.StringUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.USER_DETAILS;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;
import static com.smartit.talabia.util.Constants.SELECTED_USER_ID;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title, tv_save, tv_profile_name, tv_change_password, tv_edit_profile;
    private TextView tv_reward_points;
    private CircleImageView profileImage;
    private TalabiaEditText et_email_id, et_phone_number, et_gender, et_date_of_birth;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private SessionSecuredPreferences loginPreferences;
    private int userId;
    private EOUserPayload userPayload;

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

        setContentView(R.layout.activity_profile);

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
        this.profileImage = this.findViewById(R.id.profileImage);
        this.tv_profile_name = this.findViewById(R.id.tv_profile_name);
        this.tv_reward_points = this.findViewById(R.id.tv_reward_points);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.et_gender = this.findViewById(R.id.et_gender);
        this.et_date_of_birth = this.findViewById(R.id.et_date_of_birth);
        this.tv_change_password = this.findViewById(R.id.tv_change_password);
        this.tv_edit_profile = this.findViewById(R.id.tv_edit_profile);

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.tv_change_password.setOnClickListener(this);
        this.tv_edit_profile.setOnClickListener(this);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "My Profile" : "ملفي");
        }
        this.tv_change_password.setText(ObjectUtil.getTextFromView(this.tv_change_password).toUpperCase());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.tv_change_password:
                this.startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
            case R.id.tv_edit_profile:
                Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
                if (!ObjectUtil.isEmpty(this.userPayload))
                    editProfileIntent.putExtra("userDetails", this.userPayload);
                this.startActivity(editProfileIntent);
                break;
        }
    }

    @Override
    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();
        this.getUserDetails();
    }

    private void getUserDetails() {
        if (!ObjectUtil.isEmpty(this.languageId) && this.userId != 0) {
            progress.showProgressBar();
            this.apiInterface.userDetails(USER_DETAILS.concat(this.languageId), String.valueOf(this.userId)).enqueue(new Callback<EOUserDetails>() {
                @Override
                public void onResponse(Call<EOUserDetails> call, Response<EOUserDetails> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOUserDetails userDetails = response.body();
                        if (!ObjectUtil.isEmpty(userDetails)) {
                            if (userDetails.getIsSuccess()) {

                                if (!ObjectUtil.isEmpty(userDetails.getPayload()))
                                    userPayload = userDetails.getPayload();

                                if (!ObjectUtil.isEmpty(userDetails.getPayload().getThumbnail()))
                                    loadImages(userDetails.getPayload().getThumbnail(), profileImage);
                                if (!ObjectUtil.isEmpty(userDetails.getPayload().getFirstName()) && !ObjectUtil.isEmpty(userDetails.getPayload().getLastName()))
                                    tv_profile_name.setText(userDetails.getPayload().getFirstName().concat(" ").concat(userDetails.getPayload().getLastName()));
                                if (!ObjectUtil.isEmpty(userDetails.getPayload().getEmail()))
                                    et_email_id.setText(userDetails.getPayload().getEmail());
                                if (!ObjectUtil.isEmpty(userDetails.getPayload().getMobileNo()))
                                    et_phone_number.setText(userDetails.getPayload().getMobileNo());
                                if (!ObjectUtil.isEmpty(userDetails.getPayload().getGender())) {
                                    String gender = userDetails.getPayload().getGender();
                                    if (gender.equalsIgnoreCase("M"))
                                        et_gender.setText(StringUtil.getStringForID(R.string.male));
                                    else
                                        et_gender.setText(StringUtil.getStringForID(R.string.female));
                                }
                                if (!ObjectUtil.isEmpty(userDetails.getPayload().getDob())) {
                                    String dob = userDetails.getPayload().getDob();
                                    et_date_of_birth.setText(dob.replace("-", "/"));
                                }
                                if (ObjectUtil.isEmpty(userDetails.getPayload().getRewardPoint())) {
                                    tv_reward_points.setText(" 0");
                                } else {
                                    tv_reward_points.setText(userDetails.getPayload().getRewardPoint());
                                }

                            } else {
                                Toast.makeText(ProfileActivity.this, "" + userDetails.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOUserDetails> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(ProfileActivity.this, false, true) {
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

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.ic_profile)
                .fit()
                .into(imageView);
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

}
