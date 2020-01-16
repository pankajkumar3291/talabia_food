package com.smartit.talabia.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.smartit.talabia.R;
import com.smartit.talabia.adapters.DetailsPageAdapter;
import com.smartit.talabia.adapters.IncludedProductsAdapter;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.GlobalAlertDialog;
import com.smartit.talabia.components.GlobalProgressDialog;
import com.smartit.talabia.components.QuantityField;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaEditText;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.dashboard.EOPrice;
import com.smartit.talabia.entity.productDetails.EOProductDetails;
import com.smartit.talabia.entity.productDetails.EOProductDetailsPayload;
import com.smartit.talabia.entity.productDetails.EORelatedSimilarProduct;
import com.smartit.talabia.networking.RestClient;
import com.smartit.talabia.util.LocalizationHelper;
import com.smartit.talabia.util.ObjectUtil;
import com.smartit.talabia.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.smartit.talabia.util.Apies.PRODUCT_DETAILS;
import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.CURRENCY_CODE;
import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class ProductDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private RestClient.APIInterface apiInterface;
    private GlobalProgressDialog progress;
    private Toolbar toolbarLayout;
    private ImageView iv_back_arrow;
    private TalabiaTextView tv_page_title, tv_product_name, tv_product_description, tv_add_to_cart, tv_add_all_items_to_cart, tv_nutritional_facts, tv_video_description,
            tv_view_all_similar_products, tv_view_all_related_products, tv_view_all_other_bundles, tv_view_all_dish_using, tv_what_included,
            tv_recipe_ideas;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private int productId;
    private String pageTitle;
    private ImageView productImage, iv_nutritional_facts, iv_how_to_prepare, iv_what_included, iv_recipe_ideas, iv_cutting_service;
    private TextView tv_product_price, tv_price_after_decimal;
    private QuantityField productQuantity;
    private Spinner spinner_quantity;
    private ArrayList<String> unitList = new ArrayList<>();
    private RecyclerView similarProductRecyclerView, relatedProductRecyclerView, otherBundlesRecyclerView, dishUsingRecyclerView, whatIncludedRecyclerView;
    private LinearLayout layout_nutritional_facts, layout_nutritional, layout_how_to_prepare, layout_prepare, layoutSimilar_product,
            relatedProductsLayout, otherBundlesLayout, dishUsingLayout, layout_what_is_included, layout_recipe_ideas, layout_cutting_service,
            layout_cutting_note_refundable, layout_enable_cutting_service, layout_note_online_payment, layout_spinner;
    private WebView productVideoView;
    private EOProductDetailsPayload productDetailsPayload;
    private boolean isShowNutritionalFacts, isShowHowToPrepare, isShowWhatIsIncluded, isShowRecipeIdeas, isShowCuttingService;
    private CheckBox checkbox_enable_cutting_service;
    private ConstraintLayout constraint_special_request;
    private TalabiaEditText et_special_request;
    private Spinner spinner_cutting_selection;
    private static final String NORMAL_TYPE = "1";
    private static final String BUNDLE_TYPE = "2";
    private static final String DISH_TYPE = "3";

    private ArrayList<EORelatedSimilarProduct> similarProductsArrayList = new ArrayList<>();
    private ArrayList<EORelatedSimilarProduct> relatedProductsArrayList = new ArrayList<>();
    private ArrayList<EORelatedSimilarProduct> dishUsingProductsArrayList = new ArrayList<>();
    private ArrayList<EORelatedSimilarProduct> includedProductArrayList = new ArrayList<>();

    private DetailsPageAdapter similarPageAdapter = new DetailsPageAdapter(this, similarProductsArrayList);
    private DetailsPageAdapter relatedPageAdapter = new DetailsPageAdapter(this, relatedProductsArrayList);
    private DetailsPageAdapter dishUsingPageAdapter = new DetailsPageAdapter(this, dishUsingProductsArrayList);
    private IncludedProductsAdapter includedProductsAdapter = new IncludedProductsAdapter(this, this.includedProductArrayList);


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

        setContentView(R.layout.activity_product_details);

        if (!ObjectUtil.isEmpty(this.getIntent().getIntExtra("productId", 0)) || !ObjectUtil.isEmpty(this.getIntent().getStringExtra("pageTitle"))) {
            this.productId = this.getIntent().getIntExtra("productId", 0);
            this.pageTitle = this.getIntent().getStringExtra("pageTitle");
        }

        this.initView();
        this.setOnClickListener();
        this.getProductDetails();
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

        if (!ObjectUtil.isEmpty(this.languageId)) {
            this.tv_page_title.setText(this.languageId.equals(ENGLISH_LANGUAGE_ID) ? "Product Details" : "تفاصيل المنتج");
        }

        this.productImage = this.findViewById(R.id.productImage);
        this.tv_product_name = this.findViewById(R.id.tv_product_name);
        this.tv_product_price = this.findViewById(R.id.tv_product_price);
        this.tv_price_after_decimal = this.findViewById(R.id.tv_price_after_decimal);
        this.tv_product_description = this.findViewById(R.id.tv_product_description);
        this.productQuantity = this.findViewById(R.id.productQuantity);
        this.spinner_quantity = this.findViewById(R.id.spinner_quantity);
        this.tv_add_to_cart = this.findViewById(R.id.tv_add_to_cart);
        this.tv_add_all_items_to_cart = this.findViewById(R.id.tv_add_all_items_to_cart);
        this.similarProductRecyclerView = this.findViewById(R.id.similarProductRecyclerView);
        this.relatedProductRecyclerView = this.findViewById(R.id.relatedProductRecyclerView);
        this.layout_nutritional_facts = this.findViewById(R.id.layout_nutritional_facts);
        this.layout_nutritional = this.findViewById(R.id.layout_nutritional);
        this.iv_nutritional_facts = this.findViewById(R.id.iv_nutritional_facts);
        this.tv_nutritional_facts = this.findViewById(R.id.tv_nutritional_facts);
        this.layout_how_to_prepare = this.findViewById(R.id.layout_how_to_prepare);
        this.layout_prepare = this.findViewById(R.id.layout_prepare);
        this.iv_how_to_prepare = this.findViewById(R.id.iv_how_to_prepare);
        this.productVideoView = this.findViewById(R.id.productVideoView);
        this.tv_video_description = this.findViewById(R.id.tv_video_description);
        this.layoutSimilar_product = this.findViewById(R.id.layoutSimilar_product);
        this.tv_view_all_similar_products = this.findViewById(R.id.tv_view_all_similar_products);
        this.relatedProductsLayout = this.findViewById(R.id.relatedProductsLayout);
        this.tv_view_all_related_products = this.findViewById(R.id.tv_view_all_related_products);
        this.otherBundlesLayout = this.findViewById(R.id.otherBundlesLayout);
        this.tv_view_all_other_bundles = this.findViewById(R.id.tv_view_all_other_bundles);
        this.otherBundlesRecyclerView = this.findViewById(R.id.otherBundlesRecyclerView);
        this.dishUsingLayout = this.findViewById(R.id.dishUsingLayout);
        this.tv_view_all_dish_using = this.findViewById(R.id.tv_view_all_dish_using);
        this.dishUsingRecyclerView = this.findViewById(R.id.dishUsingRecyclerView);
        this.layout_what_is_included = this.findViewById(R.id.layout_what_is_included);
        this.whatIncludedRecyclerView = this.findViewById(R.id.whatIncludedRecyclerView);
        this.tv_what_included = this.findViewById(R.id.tv_what_included);
        this.layout_recipe_ideas = this.findViewById(R.id.layout_recipe_ideas);
        this.iv_what_included = this.findViewById(R.id.iv_what_included);
        this.iv_recipe_ideas = this.findViewById(R.id.iv_recipe_ideas);
        this.tv_recipe_ideas = this.findViewById(R.id.tv_recipe_ideas);
        this.layout_cutting_service = this.findViewById(R.id.layout_cutting_service);
        this.iv_cutting_service = this.findViewById(R.id.iv_cutting_service);
        this.checkbox_enable_cutting_service = this.findViewById(R.id.checkbox_enable_cutting_service);
        this.constraint_special_request = this.findViewById(R.id.constraint_special_request);
        this.et_special_request = this.findViewById(R.id.et_special_request);
        this.spinner_cutting_selection = this.findViewById(R.id.spinner_cutting_selection);
        this.layout_cutting_note_refundable = this.findViewById(R.id.layout_cutting_note_refundable);
        this.layout_enable_cutting_service = this.findViewById(R.id.layout_enable_cutting_service);
        this.layout_note_online_payment = this.findViewById(R.id.layout_note_online_payment);
        this.layout_spinner = this.findViewById(R.id.layout_spinner);

    }

    private void setOnClickListener() {
        this.iv_back_arrow.setOnClickListener(this);
        this.layout_nutritional.setOnClickListener(this);
        this.layout_how_to_prepare.setOnClickListener(this);
        this.layout_what_is_included.setOnClickListener(this);
        this.layout_recipe_ideas.setOnClickListener(this);
        this.layout_cutting_service.setOnClickListener(this);
    }

    private void getProductDetails() {
        if (!ObjectUtil.isEmpty(this.languageId) && this.productId != 0) {
            progress.showProgressBar();
            this.apiInterface.getProductDetails(PRODUCT_DETAILS.concat(this.languageId), String.valueOf(this.productId)).enqueue(new Callback<EOProductDetails>() {
                @Override
                public void onResponse(Call<EOProductDetails> call, Response<EOProductDetails> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOProductDetails eoProductDetails = response.body();
                        if (!ObjectUtil.isEmpty(eoProductDetails)) {
                            if (eoProductDetails.getIsSuccess()) {
                                if (!ObjectUtil.isEmpty(eoProductDetails.getPayload())) {
                                    productDetailsPayload = eoProductDetails.getPayload();
                                    dataToView();
                                }
                            } else {
                                Toast.makeText(ProductDetailsActivity.this, "" + eoProductDetails.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOProductDetails> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        new GlobalAlertDialog(ProductDetailsActivity.this, false, true) {
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

    //TODO show here data on view on the basis of condition
    private void dataToView() {
        //TODO Show data for common section

        if (!ObjectUtil.isEmpty(productDetailsPayload.getProductName()))
            this.tv_product_name.setText(productDetailsPayload.getProductName());
        if (!ObjectUtil.isEmpty(productDetailsPayload.getFeaturedImage()))
            loadImages(productDetailsPayload.getFeaturedImage(), this.productImage);
        if (!ObjectUtil.isEmpty(productDetailsPayload.getDescription()))
            this.tv_product_description.setText(UIUtil.fromHtml(productDetailsPayload.getDescription()));

        //TODO check condition for Nutritional facts, how to prepare, what is included, recipe ideas, cutting service
        if (!ObjectUtil.isEmpty(productDetailsPayload.getNutritionalFact()))
            this.layout_nutritional_facts.setVisibility(View.VISIBLE);
        if (!ObjectUtil.isEmpty(productDetailsPayload.getHowToPrepare()) || !ObjectUtil.isEmpty(productDetailsPayload.getHowToPrepareVideo()))
            this.layout_how_to_prepare.setVisibility(View.VISIBLE);
        if (!ObjectUtil.isEmpty(productDetailsPayload.getIncludedproducts())) {
            this.layout_what_is_included.setVisibility(View.VISIBLE);

            this.includedProductArrayList.addAll(productDetailsPayload.getIncludedproducts());

            this.whatIncludedRecyclerView.setHasFixedSize(true);
            this.whatIncludedRecyclerView.setAdapter(this.includedProductsAdapter);
        }

        if (!ObjectUtil.isEmpty(productDetailsPayload.getRecipeIdeas()))
            this.layout_recipe_ideas.setVisibility(View.VISIBLE);
        if (!ObjectUtil.isEmpty(productDetailsPayload.getCuttingcharges()))
            this.layout_cutting_service.setVisibility(View.VISIBLE);


        if (!ObjectUtil.isEmpty(productDetailsPayload.getProductType())) {

            if (productDetailsPayload.getProductType().equals(NORMAL_TYPE)) { //TODO checking here object is Normal type

                if (!ObjectUtil.isEmpty(productDetailsPayload.getPrices())) {  //TODO this is used to fill data into spinner and select data from this
                    for (EOPrice eoPrice : productDetailsPayload.getPrices()) {
                        unitList.add(eoPrice.getUnit());
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, unitList);
                    arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                    this.spinner_quantity.setAdapter(arrayAdapter);

                    this.spinner_quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (ObjectUtil.isEmpty(productDetailsPayload.getPrices()) && !ObjectUtil.isEmpty(languageId)) {
                                tv_product_price.setText(CURRENCY_CODE.concat(" ").concat("00."));
                                tv_price_after_decimal.setText("0");
                            } else {
                                for (EOPrice eoPrice : productDetailsPayload.getPrices()) {
                                    if (eoPrice.getUnit().equalsIgnoreCase((String) parent.getItemAtPosition(position))) {
                                        String[] priceArray = String.valueOf(eoPrice.getRegularPrice()).split("\\.");
                                        if (!ObjectUtil.isEmpty(priceArray[0]) && !ObjectUtil.isEmpty(languageId)) {
                                            if (languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID)) {
                                                tv_product_price.setText(".".concat(priceArray[0]).concat(" ").concat(CURRENCY_CODE));
                                            } else {
                                                tv_product_price.setText(CURRENCY_CODE.concat(" ").concat(priceArray[0]).concat("."));
                                            }
                                        }
                                        if (!ObjectUtil.isEmpty(priceArray[1])) {
                                            tv_price_after_decimal.setText(priceArray[1]);
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            } else if (productDetailsPayload.getProductType().equals(BUNDLE_TYPE)) { //TODO checking here object is Bundle type
                this.layout_spinner.setVisibility(View.GONE);

                if (!ObjectUtil.isEmpty(productDetailsPayload.getBunndlPrice())) {
                    if (ObjectUtil.isEmpty(productDetailsPayload.getBunndlPrice().getRegularPrice())) {
                        tv_product_price.setText(CURRENCY_CODE.concat(" ").concat("00."));
                        tv_price_after_decimal.setText("0");
                    } else {
                        String[] priceArray = String.valueOf(productDetailsPayload.getBunndlPrice().getRegularPrice()).split("\\.");
                        if (!ObjectUtil.isEmpty(priceArray[0]) && !ObjectUtil.isEmpty(languageId)) {
                            if (languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID)) {
                                tv_product_price.setText(".".concat(priceArray[0]).concat(" ").concat(CURRENCY_CODE));
                            } else {
                                tv_product_price.setText(CURRENCY_CODE.concat(" ").concat(priceArray[0]).concat("."));
                            }
                        }
                        if (!ObjectUtil.isEmpty(priceArray[1])) {
                            tv_price_after_decimal.setText(priceArray[1]);
                        }
                    }

                }

            } else if (productDetailsPayload.getProductType().equals(DISH_TYPE)) { //TODO checking here object is Dish type
                this.layout_spinner.setVisibility(View.GONE);
                this.tv_add_to_cart.setVisibility(View.GONE);
                this.tv_add_all_items_to_cart.setVisibility(View.VISIBLE);

                if (!ObjectUtil.isEmpty(productDetailsPayload.getDishPrice())) {
                    if (ObjectUtil.isEmpty(productDetailsPayload.getDishPrice().getRegularPrice())) {
                        tv_product_price.setText(CURRENCY_CODE.concat(" ").concat("00."));
                        tv_price_after_decimal.setText("0");
                    } else {
                        String[] priceArray = String.valueOf(productDetailsPayload.getDishPrice().getRegularPrice()).split("\\.");
                        if (!ObjectUtil.isEmpty(priceArray[0]) && !ObjectUtil.isEmpty(languageId)) {
                            if (languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID)) {
                                tv_product_price.setText(".".concat(priceArray[0]).concat(" ").concat(CURRENCY_CODE));
                            } else {
                                tv_product_price.setText(CURRENCY_CODE.concat(" ").concat(priceArray[0]).concat("."));
                            }
                        }
                        if (!ObjectUtil.isEmpty(priceArray[1])) {
                            tv_price_after_decimal.setText(priceArray[1]);
                        }
                    }
                }

            }
        }

        //TODO checking for similarProducts, relatedProducts, otherBundlesProducts, dishUsingThisProducts
        if (!ObjectUtil.isEmpty(productDetailsPayload.getSimilarproducts())) {
            this.layoutSimilar_product.setVisibility(View.VISIBLE);
            this.similarProductsArrayList.addAll(productDetailsPayload.getSimilarproducts());
            this.similarProductRecyclerView.setHasFixedSize(true);
            this.similarProductRecyclerView.setAdapter(similarPageAdapter);
            this.similarPageAdapter.notifyDataSetChanged();
        }

        if (!ObjectUtil.isEmpty(productDetailsPayload.getRelatedproducts())) {
            this.relatedProductsLayout.setVisibility(View.VISIBLE);
            this.relatedProductsArrayList.addAll(productDetailsPayload.getRelatedproducts());
            this.relatedProductRecyclerView.setHasFixedSize(true);
            this.relatedProductRecyclerView.setAdapter(relatedPageAdapter);
            this.relatedPageAdapter.notifyDataSetChanged();
        }

        if (!ObjectUtil.isEmpty(productDetailsPayload.getProductUsedInDishes())) {
            this.dishUsingLayout.setVisibility(View.VISIBLE);
            this.dishUsingProductsArrayList.addAll(productDetailsPayload.getProductUsedInDishes());
            this.dishUsingRecyclerView.setHasFixedSize(true);
            this.dishUsingRecyclerView.setAdapter(dishUsingPageAdapter);
            this.dishUsingPageAdapter.notifyDataSetChanged();
        }
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.icon_no_image)
                .fit()
                .into(imageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back_arrow:
                this.finish();
                break;
            case R.id.layout_nutritional:
                this.showNutritionalFacts();
                break;
            case R.id.layout_how_to_prepare:
                this.showHowToPrepare();
                break;
            case R.id.layout_what_is_included:
                this.showWhatIsIncluded();
                break;
            case R.id.layout_recipe_ideas:
                this.showRecipeIdeas();
                break;
            case R.id.layout_cutting_service:
                this.showCuttingService();
                break;
        }
    }

    private void showNutritionalFacts() {
        if (!isShowNutritionalFacts) {
            iv_nutritional_facts.setImageResource(R.drawable.ic_up_arrow);
            if (!ObjectUtil.isEmpty(productDetailsPayload.getNutritionalFact())) {
                this.tv_nutritional_facts.setVisibility(View.VISIBLE);
                this.tv_nutritional_facts.setText(UIUtil.fromHtml(productDetailsPayload.getNutritionalFact()));
            } else {
                this.tv_nutritional_facts.setVisibility(View.GONE);
            }
            isShowNutritionalFacts = true;
        } else {
            isShowNutritionalFacts = false;
            this.tv_nutritional_facts.setVisibility(View.GONE);
            iv_nutritional_facts.setImageResource(R.drawable.ic_down_arrows);
        }
    }

    private void showHowToPrepare() {
        if (!isShowHowToPrepare) {
            iv_how_to_prepare.setImageResource(R.drawable.ic_up_arrow);
            if (!ObjectUtil.isEmpty(productDetailsPayload.getHowToPrepareVideo())) {
                productVideoView.setVisibility(View.VISIBLE);
                productVideoView.setWebChromeClient(new WebChromeClient());
//                productVideoView.setWebViewClient(new Browser());
//                productVideoView.setWebChromeClient(new MyWebClient());
                WebSettings webSettings = productVideoView.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setSupportZoom(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(true);
                //productVideoView.setWebViewClient(new WebViewClient());
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(false);
                productVideoView.loadUrl(productDetailsPayload.getHowToPrepareVideo());
                productVideoView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            } else {
                productVideoView.setVisibility(View.GONE);
            }
            if (!ObjectUtil.isEmpty(productDetailsPayload.getHowToPrepare())) {
                tv_video_description.setVisibility(View.VISIBLE);
                this.tv_video_description.setText(UIUtil.fromHtml(productDetailsPayload.getHowToPrepare()));
            } else {
                tv_video_description.setVisibility(View.GONE);
            }
            isShowHowToPrepare = true;
        } else {
            isShowHowToPrepare = false;
            iv_how_to_prepare.setImageResource(R.drawable.ic_down_arrows);
            productVideoView.setVisibility(View.GONE);
            tv_video_description.setVisibility(View.GONE);
        }
    }

    private void showWhatIsIncluded() {
        if (!isShowWhatIsIncluded) {
            iv_what_included.setImageResource(R.drawable.ic_up_arrow);
            if (!ObjectUtil.isEmpty(productDetailsPayload.getIncludedproducts())) {
                this.whatIncludedRecyclerView.setVisibility(View.VISIBLE);
                this.includedProductsAdapter.notifyDataSetChanged();
            } else {
                this.whatIncludedRecyclerView.setVisibility(View.GONE);
            }
            isShowWhatIsIncluded = true;
        } else {
            isShowWhatIsIncluded = false;
            this.whatIncludedRecyclerView.setVisibility(View.GONE);
            iv_what_included.setImageResource(R.drawable.ic_down_arrows);
        }
    }

    private void showRecipeIdeas() {
        if (!isShowRecipeIdeas) {
            iv_recipe_ideas.setImageResource(R.drawable.ic_up_arrow);
            if (!ObjectUtil.isEmpty(productDetailsPayload.getRecipeIdeas())) {
                this.tv_recipe_ideas.setVisibility(View.VISIBLE);
                this.tv_recipe_ideas.setText(UIUtil.fromHtml(productDetailsPayload.getRecipeIdeas()));
            } else {
                this.tv_recipe_ideas.setVisibility(View.GONE);
            }
            isShowRecipeIdeas = true;
        } else {
            isShowRecipeIdeas = false;
            this.tv_recipe_ideas.setVisibility(View.GONE);
            iv_recipe_ideas.setImageResource(R.drawable.ic_down_arrows);
        }
    }

    private void showCuttingService() {
        if (!isShowCuttingService) {
            iv_cutting_service.setImageResource(R.drawable.ic_up_arrow);
            if (!ObjectUtil.isEmpty(productDetailsPayload.getCuttingcharges())) {
                this.layout_cutting_note_refundable.setVisibility(View.VISIBLE);
                this.layout_enable_cutting_service.setVisibility(View.VISIBLE);

                //TODO first time checking checkbox is selected or not
                if (this.checkbox_enable_cutting_service.isChecked()) {
                    this.layout_note_online_payment.setVisibility(View.VISIBLE);
                    this.constraint_special_request.setVisibility(View.VISIBLE);
                } else {
                    this.layout_note_online_payment.setVisibility(View.GONE);
                    this.constraint_special_request.setVisibility(View.GONE);
                }

                this.checkbox_enable_cutting_service.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            layout_note_online_payment.setVisibility(View.VISIBLE);
                            constraint_special_request.setVisibility(View.VISIBLE);
                        } else {
                            layout_note_online_payment.setVisibility(View.GONE);
                            constraint_special_request.setVisibility(View.GONE);
                        }
                    }
                });

            } else {
                this.layout_cutting_note_refundable.setVisibility(View.GONE);
                this.layout_enable_cutting_service.setVisibility(View.GONE);

                this.layout_note_online_payment.setVisibility(View.GONE);
                this.constraint_special_request.setVisibility(View.GONE);
            }
            isShowCuttingService = true;
        } else {
            isShowCuttingService = false;
            this.layout_cutting_note_refundable.setVisibility(View.GONE);
            this.layout_enable_cutting_service.setVisibility(View.GONE);

            this.layout_note_online_payment.setVisibility(View.GONE);
            this.constraint_special_request.setVisibility(View.GONE);
            iv_cutting_service.setImageResource(R.drawable.ic_down_arrows);
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
