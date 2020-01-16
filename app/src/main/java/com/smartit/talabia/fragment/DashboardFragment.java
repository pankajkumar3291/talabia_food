package com.smartit.talabia.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.smartit.talabia.R;
import com.smartit.talabia.adapters.DashboardBannerAdapter;
import com.smartit.talabia.adapters.DashboardImagesCategoryAdapter;
import com.smartit.talabia.adapters.DynamicCategoryAdapter;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.entity.dashboard.EOBannerImageList;
import com.smartit.talabia.entity.dashboard.EODashboardCategoryList;
import com.smartit.talabia.entity.dashboard.EODashboardCategoryPayload;
import com.smartit.talabia.entity.dashboard.EOImageCategory;
import com.smartit.talabia.entity.dashboard.EOImageCategoryList;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.ObjectUtil;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.BANNER_SLIDER;
import static com.smartit.talabia.util.Apies.CATEGORY_COLLECTIONS;
import static com.smartit.talabia.util.Apies.CATEGORY_IMAGES_SLIDER;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class DashboardFragment extends Fragment {

    private View view;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private ViewPager bannerViewPager;
    private CirclePageIndicator circlePageIndicator;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private RecyclerView categoryDynamicRecyclerView, allCategoriesImagesRecycler;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        this.initView();
        this.getBannerImages();
        this.getImagesCategory();
        this.getCategoryCollections();

        return this.view;
    }

    private void initView() {
        this.apiInterface = RestClient.getClient();
        this.progress = new GlobalProgressDialog(getActivity());
        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");

        this.bannerViewPager = view.findViewById(R.id.bannerViewPager);
        this.circlePageIndicator = view.findViewById(R.id.indicator);
        this.categoryDynamicRecyclerView = view.findViewById(R.id.categoryDynamicRecyclerView);
        this.allCategoriesImagesRecycler = view.findViewById(R.id.allCategoriesImagesRecycler);
    }

    private void getBannerImages() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            progress.showProgressBar();
            this.apiInterface.getBannerImages(BANNER_SLIDER.concat(this.languageId)).enqueue(new Callback<EOBannerImageList>() {
                @Override
                public void onResponse(Call<EOBannerImageList> call, Response<EOBannerImageList> response) {
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOBannerImageList bannerImageList = response.body();
                        if (!ObjectUtil.isEmpty(bannerImageList)) {
                            if (bannerImageList.getIsSuccess()) {

                                bannerViewPager.setAdapter(new DashboardBannerAdapter(getActivity(), (ArrayList<String>) bannerImageList.getPayload().getImage()));
                                circlePageIndicator = view.findViewById(R.id.indicator);
                                circlePageIndicator.setViewPager(bannerViewPager);
                                final float density = getResources().getDisplayMetrics().density;
                                circlePageIndicator.setRadius(5 * density);
                                NUM_PAGES = bannerImageList.getPayload().getImage().size();

                                // Auto start of viewpager
                                final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public void run() {
                                        if (currentPage == NUM_PAGES) {
                                            currentPage = 0;
                                        }
                                        bannerViewPager.setCurrentItem(currentPage++, true);
                                    }
                                };

                                Timer swipeTimer = new Timer();
                                swipeTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, 7000, 7000);


                                // Pager listener over indicator
                                circlePageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                    @Override
                                    public void onPageSelected(int position) {
                                        currentPage = position;
                                    }

                                    @Override
                                    public void onPageScrolled(int pos, float arg1, int arg2) {
                                    }

                                    @Override
                                    public void onPageScrollStateChanged(int pos) {
                                    }
                                });

                            } else {
                                Toast.makeText(getActivity(), "" + bannerImageList.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOBannerImageList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                    }
                }
            });
        }
    }

    private void getImagesCategory() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.apiInterface.getImagesCategory(CATEGORY_IMAGES_SLIDER.concat(this.languageId)).enqueue(new Callback<EOImageCategoryList>() {
                @Override
                public void onResponse(Call<EOImageCategoryList> call, Response<EOImageCategoryList> response) {
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOImageCategoryList imageCategoryList = response.body();
                        if (!ObjectUtil.isEmpty(imageCategoryList)) {
                            if (imageCategoryList.getIsSuccess()) {
                                //Toast.makeText(getContext(),"Response = "+imageCategoryList.getPayload().getAllcategory().size(),Toast.LENGTH_SHORT).show();
                                allCategoriesImagesRecycler.setHasFixedSize(true);
                                allCategoriesImagesRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                                allCategoriesImagesRecycler.setAdapter(new DashboardImagesCategoryAdapter(getContext(), (ArrayList<EOImageCategory>) imageCategoryList.getPayload().getAllcategory()));
                            } else {
                                Toast.makeText(getActivity(), "" + imageCategoryList.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOImageCategoryList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();

                    }
                }
            });
        }
    }

    private void getCategoryCollections() {
        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.apiInterface.getCollectionsApi(CATEGORY_COLLECTIONS.concat(this.languageId)).enqueue(new Callback<EODashboardCategoryList>() {
                @Override
                public void onResponse(Call<EODashboardCategoryList> call, Response<EODashboardCategoryList> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EODashboardCategoryList productsArrayList = response.body();
                        if (!ObjectUtil.isEmpty(productsArrayList)) {
                            if (productsArrayList.getIsSuccess()) {
                                categoryDynamicRecyclerView.setHasFixedSize(true);
                                categoryDynamicRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                                categoryDynamicRecyclerView.setAdapter(new DynamicCategoryAdapter(getContext(), (ArrayList<EODashboardCategoryPayload>) productsArrayList.getPayload()));
                            } else {
                                Toast.makeText(getActivity(), "" + productsArrayList.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EODashboardCategoryList> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
//                        new GlobalAlertDialog ( getActivity ( ), false, true ) {
//                            @Override
//                            public void onDefault() {
//                                super.onDefault ( );
//                            }
//                        }.show ( R.string.server_is_under_maintenance );
                    }
                }
            });
        }
    }

}
