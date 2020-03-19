package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.FontAwesomeIcon;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EOFacebook;
import com.smartit.talabia.entity.account.EORegister;
import com.smartit.talabia.networking.JsonParser;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.GlobalUtil;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.LOGIN_CUSTOMER;
import static com.smartit.talabia.util.Apies.LOGIN_VIA_GOOGLE;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;
import static com.smartit.talabia.util.Constants.SELECTED_USER_ID;
import static com.smartit.talabia.util.Constants.SOCIAL_LOGED_IN;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private TalabiaEditText et_email_address, et_password;
    private TalabiaTextView tv_forget_password, tv_sign_in, tv_continue_as_guest, tv_sign_up;
    private FontAwesomeIcon icon_facebook, icon_google_plus;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private SessionSecuredPreferences loginPreferences;

    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 5556;

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

        setContentView(R.layout.activity_login);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        callbackManager = CallbackManager.Factory.create();

        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiInterface = RestClient.getClient();
        this.progress = new GlobalProgressDialog(this);
        this.et_email_address = this.findViewById(R.id.et_email_address);
        this.et_password = this.findViewById(R.id.et_password);
        this.tv_forget_password = this.findViewById(R.id.tv_forget_password);
        this.tv_sign_in = this.findViewById(R.id.tv_sign_in);

        this.icon_facebook = this.findViewById(R.id.icon_facebook);
        this.icon_google_plus = this.findViewById(R.id.icon_google_plus);
        this.tv_continue_as_guest = this.findViewById(R.id.tv_continue_as_guest);
        this.tv_sign_up = this.findViewById(R.id.tv_sign_up);
    }

    private void setOnClickListener() {
        this.tv_forget_password.setOnClickListener(this);
        this.tv_sign_in.setOnClickListener(this);
        this.icon_facebook.setOnClickListener(this);
        this.icon_google_plus.setOnClickListener(this);
        this.tv_continue_as_guest.setOnClickListener(this);
        this.tv_sign_up.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_forget_password:
                this.startActivity(new Intent(this, ResetPasswordActivity.class));
                break;
            case R.id.tv_sign_in:
                if (isValidLogin()) {
                    loginCustomer();
                }
                break;
            case R.id.icon_facebook:
                this.loginViaFacebook();
                break;
            case R.id.icon_google_plus:
                this.loginViaGoogle();
                break;
            case R.id.tv_continue_as_guest:
                //this.startActivity ( new Intent ( this, SocialAccountSignupActivity.class ) );
                break;
            case R.id.tv_sign_up:
                this.startActivity(new Intent(this, SignupActivity.class));
                break;
        }
    }

    private void loginViaGoogle() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            GoogleSignInAccount account = result.getSignInAccount();
            socialLogin(Objects.requireNonNull(account).getGivenName(), account.getFamilyName(), account.getEmail(), account.getId());
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void socialLogin(String fName, String lName, String email, String socialLoginId) {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.socialLoginCustomer(LOGIN_VIA_GOOGLE.concat(this.languageId),
                    fName, lName, email, socialLoginId).enqueue(new Callback<EORegister>() {
                @Override
                public void onResponse(Call<EORegister> call, Response<EORegister> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EORegister eoSocialLogin = response.body();
                        if (!ObjectUtil.isEmpty(eoSocialLogin)) {
                            if (eoSocialLogin.getIsSuccess()) {
                                Toast.makeText(LoginActivity.this, "" + eoSocialLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                //TODO save user_id in shared preference
                                loginPreferences.edit().putInt(SELECTED_USER_ID, eoSocialLogin.getPayload().getUser_id()).apply();
                                loginPreferences.edit().putBoolean(SOCIAL_LOGED_IN, true).apply();
                                Intent dashboardPage = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(dashboardPage);
                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "" + eoSocialLogin.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EORegister> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(LoginActivity.this, false, true) {
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

    private void loginViaFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getFacebookData();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "You cancel the dialog.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(LoginActivity.this, "" + exception, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getFacebookData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                //TODO parse here facebook json response
                EOFacebook eoFacebook = JsonParser.getInstance().getObject(EOFacebook.class, response.getRawResponse());

                if (!ObjectUtil.isEmpty(response) && !ObjectUtil.isEmpty(eoFacebook.getFirstName())
                        && !ObjectUtil.isEmpty(eoFacebook.getLastName()) && !ObjectUtil.isEmpty(eoFacebook.getEmail())) {
                    socialLogin(eoFacebook.getFirstName(), eoFacebook.getLastName(), eoFacebook.getEmail(), eoFacebook.getId());
                } else {
                    Intent signupIntent = new Intent(LoginActivity.this, SignupActivity.class);
                    LoginActivity.this.startActivity(signupIntent);
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields","id,first_name,last_name,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void loginCustomer() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.loginCustomer(LOGIN_CUSTOMER.concat(this.languageId),
                    et_email_address.getText().toString().trim(), et_password.getText().toString().trim()).enqueue(new Callback<EORegister>() {
                @Override
                public void onResponse(Call<EORegister> call, Response<EORegister> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EORegister eoLogin = response.body();
                        if (!ObjectUtil.isEmpty(eoLogin)) {
                            if (eoLogin.getIsSuccess()) {
                                Toast.makeText(LoginActivity.this, "" + eoLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                //TODO save user_id in shared preference
                                loginPreferences.edit().putInt(SELECTED_USER_ID, eoLogin.getPayload().getUser_id()).apply();
                                Intent dashboardPage = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(dashboardPage);
                                LoginActivity.this.finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "" + eoLogin.getMessage(), Toast.LENGTH_SHORT).show();
                                new GlobalAlertDialog(LoginActivity.this, false, false) {
                                    @Override
                                    public void onDefault() {
                                        super.onDefault();
                                    }
                                }.show(eoLogin.getMessage());
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EORegister> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(LoginActivity.this, false, true) {
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

    private boolean isValidLogin() {
        String errorMsg = null;

        String emailId = ObjectUtil.getTextFromView(et_email_address);
        String password = ObjectUtil.getTextFromView(et_password);

        if (ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(password)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (password.length() < 8) {
            errorMsg = this.getString(R.string.password_min_character);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
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
