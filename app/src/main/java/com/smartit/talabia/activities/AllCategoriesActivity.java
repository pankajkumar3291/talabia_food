package com.smartit.talabia.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.adapters.AllCategoryMenuAdapter;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.expabdable.EOAllCategoryList;
import com.smartit.talabia.expabdable.EOAllCategoryPayload;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.DividerItemDecoration;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.ALL_CATEGORIES;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class AllCategoriesActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_cross;
    private TalabiaTextView tv_page_title, tv_no_items_display;
    private RecyclerView menuRecyclerView;
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

        setContentView(R.layout.activity_all_categories);

        this.initVie();
        this.setOnClickListener();
        this.dataToView();
        this.getAllCategories();
    }

    private void initVie() {
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
        this.iv_cross = this.toolbarLayout.findViewById(R.id.iv_cross);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);
        this.menuRecyclerView = this.findViewById(R.id.menuRecyclerView);
        this.tv_no_items_display = this.findViewById(R.id.tv_no_items_display);
    }

    private void setOnClickListener() {
        this.iv_cross.setOnClickListener(this);
    }

    private void dataToView() {
        this.iv_back_arrow.setVisibility(View.GONE);
        this.iv_cross.setVisibility(View.VISIBLE);
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "All Categories" : "جميع الفئات");
        }
    }

    private void getAllCategories() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.getAllCategories(ALL_CATEGORIES.concat(languageId)).enqueue(new Callback<EOAllCategoryList>() {
                @Override
                public void onResponse(Call<EOAllCategoryList> call, Response<EOAllCategoryList> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOAllCategoryList allCategoryList = response.body();
                        if (!ObjectUtil.isEmpty(allCategoryList)) {
                            if (allCategoryList.getIsSuccess()) {
                                //Toast.makeText(AllCategoriesActivity.this, "" + allCategoryList.getMessage(), Toast.LENGTH_SHORT).show();
                                if (!ObjectUtil.isEmpty(allCategoryList.getPayload())) {
                                    tv_no_items_display.setVisibility(View.GONE);
                                    menuRecyclerView.setVisibility(View.VISIBLE);
                                    menuRecyclerView.setHasFixedSize(true);
                                    menuRecyclerView.setLayoutManager(new LinearLayoutManager(AllCategoriesActivity.this, LinearLayoutManager.VERTICAL, false));
                                    menuRecyclerView.addItemDecoration(new DividerItemDecoration(AllCategoriesActivity.this));
                                    menuRecyclerView.setAdapter(new AllCategoryMenuAdapter(AllCategoriesActivity.this, (ArrayList<EOAllCategoryPayload>) allCategoryList.getPayload()));
                                }
                            } else {
                                progress.hideProgressBar();
                                tv_no_items_display.setVisibility(View.VISIBLE);
                                menuRecyclerView.setVisibility(View.GONE);
                                Toast.makeText(AllCategoriesActivity.this, "" + allCategoryList.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<EOAllCategoryList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(AllCategoriesActivity.this, false, true) {
                            public void onDefault() {
                                super.onDefault();
                            }
                        }.show(R.string.server_is_under_maintenance);
                    }
                }
            });
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.iv_cross) {
            this.finish();
        }
    }

}
