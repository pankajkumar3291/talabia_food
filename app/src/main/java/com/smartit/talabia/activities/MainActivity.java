package com.smartit.talabia.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.adapters.MenuAdapter;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.account.EOUserDetails;
import com.smartit.talabia.fragment.DashboardFragment;
import com.smartit.talabia.fragment.OffersFragment;
import com.smartit.talabia.fragment.OrdersFragment;
import com.smartit.talabia.fragment.SearchFragment;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.USER_DETAILS;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;
import static com.smartit.talabia.util.Constants.SELECTED_USER_ID;
import static com.smartit.talabia.util.Constants.SOCIAL_LOGED_IN;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_menu_icon, iv_search_icon, iv_filter_icon, iv_cart_icon, iv_down_arrow;
    private TalabiaTextView tv_all_categories, tv_profile_name;
    private TextView tv_profile_rewards;
    private DrawerLayout drawer;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private SessionSecuredPreferences loginPreferences;
    private int userId;
    private BottomNavigationView bottomNavigationView;
    private CircleImageView profileImage;
    private RecyclerView menuRecyclerView;
    private RelativeLayout layout_logout;
    private String[] title_menu_array;
    public static WeakReference<MainActivity> mainActivity;

    private int[] image_menu_array = {R.drawable.ic_location, R.drawable.ic_menu_profile, R.drawable.ic_order, R.drawable.ic_category,
            R.drawable.ic_email, R.drawable.ic_chat, R.drawable.ic_setting, R.drawable.ic_menu_profile, R.drawable.ic_question};

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

        setContentView(R.layout.activity_main);
        mainActivity = new WeakReference<>(this);
        this.initView();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.apiInterface = RestClient.getClient();
        this.progress = new GlobalProgressDialog(this);
        this.title_menu_array = this.getResources().getStringArray(R.array.drawerMenuTitles);

        //TODO here toolbar layout
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_menu_icon = this.toolbarLayout.findViewById(R.id.iv_menu_icon);
        this.tv_all_categories = this.toolbarLayout.findViewById(R.id.tv_all_categories);
        this.iv_search_icon = this.toolbarLayout.findViewById(R.id.iv_search_icon);
        this.iv_filter_icon = this.toolbarLayout.findViewById(R.id.iv_filter_icon);
        this.iv_cart_icon = this.toolbarLayout.findViewById(R.id.iv_cart_icon);
        this.iv_down_arrow = this.toolbarLayout.findViewById(R.id.iv_down_arrow);

        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.userId = loginPreferences.getInt(SELECTED_USER_ID, 0);
        this.drawer = this.findViewById(R.id.drawer_layout);
        this.bottomNavigationView = this.findViewById(R.id.navigation);
        this.profileImage = this.findViewById(R.id.profileImage);
        this.tv_profile_name = this.findViewById(R.id.tv_profile_name);
        this.tv_profile_rewards = this.findViewById(R.id.tv_profile_rewards);
        this.menuRecyclerView = this.findViewById(R.id.menuRecyclerView);
        this.layout_logout = this.findViewById(R.id.layout_logout);
    }

    private void setOnClickListener() {
        this.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        this.iv_menu_icon.setOnClickListener(this);
        this.layout_logout.setOnClickListener(this);
        this.tv_all_categories.setOnClickListener(this);
        this.iv_down_arrow.setOnClickListener(this);
    }

    private void dataToView() {
        if (this.title_menu_array.length != 0 && this.image_menu_array.length != 0) {
            this.menuRecyclerView.setHasFixedSize(true);
            this.menuRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false));
            this.menuRecyclerView.setAdapter(new MenuAdapter(this, this.title_menu_array, this.image_menu_array, this.drawer));
        }

        //TODO by default home menu is selected
        this.bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        if (!ObjectUtil.isEmpty(this.languageId)) //TODO from here rotate menu icon on change language
            this.iv_menu_icon.setRotation(this.languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID) ? 180.0f : 0.0f);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragment = getSupportFragmentManager().findFragmentByTag("dashboard");
                    if (fragment != null && fragment.isVisible()) {
                        return false;
                    } else {
                        changeFragment(new DashboardFragment(), "dashboard");
                        return true;
                    }
                case R.id.navigation_search:
                    fragment = getSupportFragmentManager().findFragmentByTag("search");
                    if (fragment != null && fragment.isVisible()) {
                        return false;
                    } else {
                        changeFragment(new SearchFragment(), "search");
                        return true;
                    }
                case R.id.navigation_offer:
                    fragment = getSupportFragmentManager().findFragmentByTag("offers");
                    if (fragment != null && fragment.isVisible()) {
                        return false;
                    } else {
                        changeFragment(new OffersFragment(), "offers");
                        return true;
                    }
                case R.id.navigation_shopping_bag:
                    fragment = getSupportFragmentManager().findFragmentByTag("orders");
                    if (fragment != null && fragment.isVisible()) {
                        return false;
                    } else {
                        changeFragment(new OrdersFragment(), "orders");
                        return true;
                    }
            }
            return false;
        }
    };

    private void changeFragment(Fragment fragment, String fragmentTag) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment, fragmentTag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menu_icon:
                this.drawer.openDrawer(Gravity.START);
                break;
            case R.id.layout_logout:
                this.logoutFromApplication();
                break;
            case R.id.tv_all_categories:
            case R.id.iv_down_arrow:
                this.startActivity(new Intent(this, AllCategoriesActivity.class));
                //this.startActivity(new Intent(this, ShowMoreProductsActivity.class));
                break;
        }
    }

    private void logoutFromApplication() {
        new GlobalAlertDialog(this, true, false) {
            @Override
            public void onConfirmation() {
                super.onConfirmation();

                //TODO when user is logout out then clear the login shared preferences
                if (loginPreferences.contains(SELECTED_USER_ID) || loginPreferences.contains(SOCIAL_LOGED_IN)) {

                    loginPreferences.edit().clear().apply();
                    if (drawer.isDrawerOpen(Gravity.END)) {
                        drawer.closeDrawer(Gravity.END);
                    }
                    MainActivity.this.finish();

                    //TODO from here clear facebook token
                    AccessToken accessToken = AccessToken.getCurrentAccessToken();
                    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
                    if (isLoggedIn) {
                        LoginManager.getInstance().logOut();
                    }

//                    //TODO from here clear google token
//                    GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder ( getApplicationContext ( ) ).build ( );
//                    if(mGoogleApiClient.isConnected ( )) {
//                        Auth.GoogleSignInApi.signOut ( mGoogleApiClient );
//                    }

                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    MainActivity.this.startActivity(loginIntent);
                }
            }
        }.show(R.string.are_you_sure_you_want_to_logout);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (this.drawer.isDrawerOpen(GravityCompat.START)) {
            this.drawer.closeDrawers();
        }

        if (bottomNavigationView.getSelectedItemId() == R.id.navigation_home) {
            super.onBackPressed();
            finish();
        } else {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();

        if (!ObjectUtil.isEmpty(languageId) && userId != 0) {
            getUserDetails();
        }
    }

    private void getUserDetails() {
        //progress.showProgressBar();
        this.apiInterface.userDetails(USER_DETAILS.concat(this.languageId), String.valueOf(this.userId)).enqueue(new Callback<EOUserDetails>() {
            @Override
            public void onResponse(Call<EOUserDetails> call, Response<EOUserDetails> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {
                    EOUserDetails userDetails = response.body();
                    if (!ObjectUtil.isEmpty(userDetails)) {
                        if (userDetails.getIsSuccess()) {
                            if (!ObjectUtil.isEmpty(userDetails.getPayload().getThumbnail()))
                                loadImages(userDetails.getPayload().getThumbnail(), profileImage);
                            if (!ObjectUtil.isEmpty(userDetails.getPayload().getFirstName()) && !ObjectUtil.isEmpty(userDetails.getPayload().getLastName()))
                                tv_profile_name.setText(userDetails.getPayload().getFirstName().concat(" ").concat(userDetails.getPayload().getLastName()));
                            if (ObjectUtil.isEmpty(userDetails.getPayload().getRewardPoint())) {
                                tv_profile_rewards.setText(" 0");
                            } else {
                                tv_profile_rewards.setText(userDetails.getPayload().getRewardPoint());
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "" + userDetails.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOUserDetails> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    new GlobalAlertDialog(MainActivity.this, false, true) {
                        @Override
                        public void onDefault() {
                            super.onDefault();
                        }
                    }.show(R.string.server_is_under_maintenance);
                }
            }
        });
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.ic_profile)
                .fit()
                .into(imageView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (drawer.isDrawerOpen(Gravity.END)) {
            drawer.closeDrawer(Gravity.END);
        }
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

}
