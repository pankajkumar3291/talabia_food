package com.smartit.talabia.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.adapters.ShowAllProductsAdapter;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.allproducts.EOAllProducts;
import com.smartit.talabia.entity.allproducts.EOAllProductsPayloadData;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.DASHBOARD_PRODUCT_CATEGORY;
import static com.smartit.talabia.util.Apies.DASHBOARD_VIEW_ALL;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class ShowAllProductsActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow, iv_cart_icon;
    private TalabiaTextView tv_page_title, tv_bag_count, tv_no_items_display;
    private RecyclerView productsRecyclerView;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private int productCategoryId;
    private GridLayoutManager linearLayoutManager;
    private int currentPage = 1;
    private Integer lastPage;
    private String pageTitle;
    private static final String PER_PAGE_ITEMS = "6";
    private boolean isCategoryTile;
    private ShowAllProductsAdapter showAllProductsAdapter;
    private ArrayList<EOAllProductsPayloadData> productsPayloadDataArrayList = new ArrayList<>();

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

        setContentView(R.layout.activity_show_all_products);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("productCategoryId", 0)) || !ObjectUtil.isEmpty(this.getIntent().getStringExtra("pageTitle"))
                || !ObjectUtil.isEmpty(this.getIntent().getBooleanExtra("isCategoryTile", false))) {
            this.productCategoryId = this.getIntent().getIntExtra("productCategoryId", 0);
            this.pageTitle = this.getIntent().getStringExtra("pageTitle");
            this.isCategoryTile = this.getIntent().getBooleanExtra("isCategoryTile", false);
        }

        this.initView();
        this.setOnClickListener();
        this.dataToView();
        this.getAllProductsData();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progress = new GlobalProgressDialog(this);
        this.apiInterface = RestClient.getClient();
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        //TODO here toolbar layout view
        this.toolbarLayout = this.findViewById(R.id.toolbarLayout);
        this.setSupportActionBar(this.toolbarLayout);
        this.iv_back_arrow = this.toolbarLayout.findViewById(R.id.iv_back_arrow);
        this.iv_cart_icon = this.toolbarLayout.findViewById(R.id.iv_cart_icon);
        this.tv_page_title = this.toolbarLayout.findViewById(R.id.tv_page_title);
        this.tv_bag_count = this.toolbarLayout.findViewById(R.id.tv_bag_count);

        this.productsRecyclerView = this.findViewById(R.id.productsRecyclerView);
        this.tv_no_items_display = this.findViewById(R.id.tv_no_items_display);
    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
    }

    private void dataToView() {
        this.iv_cart_icon.setVisibility(View.VISIBLE);
        this.tv_bag_count.setVisibility(View.GONE);
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.pageTitle);
        }
    }

    private void getAllProductsData() {
        progress.showProgressBar();
        showAllProductsApi().enqueue(new Callback<EOAllProducts>() {
            @Override
            public void onResponse(Call<EOAllProducts> call, Response<EOAllProducts> response) {
                progress.hideProgressBar();
                if (!ObjectUtil.isEmpty(response.body())) {

                    EOAllProducts eoAllProducts = response.body();
                    if (!ObjectUtil.isEmpty(eoAllProducts)) {
                        if (eoAllProducts.getIsSuccess()) {
                            lastPage = eoAllProducts.getPayload().getLastPage();

                            //TODO At first time load by default first page
                            productsPayloadDataArrayList.addAll(eoAllProducts.getPayload().getData());
                            if (!ObjectUtil.isEmpty(productsPayloadDataArrayList)) {
                                tv_no_items_display.setVisibility(View.GONE);
                                productsRecyclerView.setVisibility(View.VISIBLE);

                                productsRecyclerView.setHasFixedSize(true);
                                showAllProductsAdapter = new ShowAllProductsAdapter(ShowAllProductsActivity.this, productsPayloadDataArrayList);
                                linearLayoutManager = new GridLayoutManager(ShowAllProductsActivity.this, 2);
                                productsRecyclerView.setLayoutManager(linearLayoutManager);
                                productsRecyclerView.setItemAnimator(new DefaultItemAnimator());
                                productsRecyclerView.setAdapter(showAllProductsAdapter);
                            }

                            //TODO when user scroll then this api will call again & again
                            productsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                    super.onScrollStateChanged(recyclerView, newState);
                                }

                                @Override
                                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == productsPayloadDataArrayList.size() - 1) {
                                        currentPage += 1;
                                        loadNextPage();
                                    }
                                }
                            });
                        } else {
                            progress.hideProgressBar();
                            tv_no_items_display.setVisibility(View.VISIBLE);
                            productsRecyclerView.setVisibility(View.GONE);
                            Toast.makeText(ShowAllProductsActivity.this, "" + eoAllProducts.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<EOAllProducts> call, Throwable t) {
                if (t.getMessage() != null) {
                    progress.hideProgressBar();
                    new GlobalAlertDialog(ShowAllProductsActivity.this, false, true) {
                        @Override
                        public void onDefault() {
                            super.onDefault();
                        }
                    }.show(R.string.server_is_under_maintenance);
                }
            }
        });
    }

    private void loadNextPage() {
        if (currentPage <= lastPage) {
            progress.showProgressBar();
            showAllProductsApi().enqueue(new Callback<EOAllProducts>() {
                @Override
                public void onResponse(Call<EOAllProducts> call, Response<EOAllProducts> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOAllProducts eoAllProducts = response.body();
                        if (!ObjectUtil.isEmpty(eoAllProducts)) {
                            if (eoAllProducts.getIsSuccess()) {
                                productsPayloadDataArrayList.addAll(eoAllProducts.getPayload().getData());
                                if (!ObjectUtil.isEmpty(productsPayloadDataArrayList)) {
                                    tv_no_items_display.setVisibility(View.GONE);
                                    productsRecyclerView.setVisibility(View.VISIBLE);
                                    showAllProductsAdapter.notifyDataSetChanged();
                                }
                            } else {
                                progress.hideProgressBar();
                                tv_no_items_display.setVisibility(View.VISIBLE);
                                productsRecyclerView.setVisibility(View.GONE);
                                Toast.makeText(ShowAllProductsActivity.this, "" + eoAllProducts.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOAllProducts> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(ShowAllProductsActivity.this, false, true) {
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

    private Call<EOAllProducts> showAllProductsApi() {
        String productsUrl;
        if (!ObjectUtil.isEmpty(this.languageId)) {
            if (isCategoryTile) {
                productsUrl = DASHBOARD_PRODUCT_CATEGORY.concat(languageId).concat("?").concat("page=").concat(String.valueOf(currentPage));
            } else {
                productsUrl = DASHBOARD_VIEW_ALL.concat(languageId).concat("?").concat("page=").concat(String.valueOf(currentPage));
            }
            return apiInterface.dashboardProductCategory(productsUrl, String.valueOf(productCategoryId), PER_PAGE_ITEMS);
        }
        return null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
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
