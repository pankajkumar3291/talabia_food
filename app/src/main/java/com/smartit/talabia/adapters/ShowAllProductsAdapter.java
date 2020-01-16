package com.smartit.talabia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.smartit.talabia.R;
import com.smartit.talabia.activities.ProductDetailsActivity;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.allproducts.EOAllProductsPayloadData;
import com.smartit.talabia.entity.dashboard.EOPrice;
import com.smartit.talabia.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.smartit.talabia.util.Constants.ARABIC_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.CURRENCY_CODE;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 12/4/19.
 */

public class ShowAllProductsAdapter extends RecyclerView.Adapter<ShowAllProductsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EOAllProductsPayloadData> productsDataList;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private static final String NORMAL_TYPE = "1";
    private static final String BUNDLE_TYPE = "2";
    private static final String DISH_TYPE = "3";

    public ShowAllProductsAdapter(Context context, ArrayList<EOAllProductsPayloadData> productsDataList) {
        this.context = context;
        this.productsDataList = productsDataList;

        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ShowAllProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_page, parent, false);
        return new ShowAllProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull ShowAllProductsAdapter.ViewHolder holder, final int position) {
        final EOAllProductsPayloadData payloadData = this.productsDataList.get(position);

        if (!ObjectUtil.isEmpty(payloadData.getProductName()))
            holder.tv_product_name.setText(payloadData.getProductName());
        if (!ObjectUtil.isEmpty(payloadData.getFeaturedImage()))
            loadImages(payloadData.getFeaturedImage(), holder.productImage);

        if (!ObjectUtil.isEmpty(payloadData.getProductType())) {
            if (payloadData.getProductType().equalsIgnoreCase(NORMAL_TYPE)) {  //TODO checking here object is Normal type
                holder.layout_items_included.setVisibility(View.GONE);
                holder.tv_full_dish_price.setVisibility(View.GONE);
                ArrayList<String> unitList = new ArrayList<>();
                if (!ObjectUtil.isEmpty(payloadData.getPrices())) {
                    for (EOPrice eoPrice : payloadData.getPrices()) {
                        unitList.add(eoPrice.getUnit());
                    }
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, unitList);
                arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
                holder.spinner_quantity.setAdapter(arrayAdapter);

            } else if (payloadData.getProductType().equalsIgnoreCase(BUNDLE_TYPE)) { //TODO checking here object is Bundle type
                holder.layout_spinner.setVisibility(View.GONE);
                holder.layout_items_included.setVisibility(View.GONE);
                holder.tv_full_dish_price.setVisibility(View.GONE);

                if (!ObjectUtil.isEmpty(payloadData.getBunndlPrice())) {
                    if (ObjectUtil.isEmpty(payloadData.getBunndlPrice().getRegularPrice())) {
                        holder.tv_product_price.setText(CURRENCY_CODE.concat(" ").concat("00."));
                        holder.tv_price_after_decimal.setText("0");
                    } else {
                        String[] priceArray = String.valueOf(payloadData.getBunndlPrice().getRegularPrice()).split("\\.");
                        if (!ObjectUtil.isEmpty(priceArray[0]) && !ObjectUtil.isEmpty(languageId)) {
                            if (languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID)) {
                                holder.tv_product_price.setText(".".concat(priceArray[0]).concat(" ").concat(CURRENCY_CODE));
                            } else {
                                holder.tv_product_price.setText(CURRENCY_CODE.concat(" ").concat(priceArray[0]).concat("."));
                            }
                        }
                        if (!ObjectUtil.isEmpty(priceArray[1])) {
                            holder.tv_price_after_decimal.setText(priceArray[1]);
                        }
                    }

                }

            } else if (payloadData.getProductType().equalsIgnoreCase(DISH_TYPE)) { //TODO checking here object is Dish type
                holder.layout_spinner.setVisibility(View.GONE);
                holder.tv_add_to_cart.setVisibility(View.GONE);
                holder.tv_add_all_items_to_cart.setVisibility(View.VISIBLE);

                if (!ObjectUtil.isEmpty(payloadData.getIncludedproducts()))
                    holder.tv_items_included.setText(String.valueOf(payloadData.getIncludedproducts().size()));

                if (!ObjectUtil.isEmpty(payloadData.getDishPrice())) {
                    if (ObjectUtil.isEmpty(payloadData.getDishPrice().getRegularPrice())) {
                        holder.tv_product_price.setText(CURRENCY_CODE.concat(" ").concat("00."));
                        holder.tv_price_after_decimal.setText("0");
                    } else {
                        String[] priceArray = String.valueOf(payloadData.getDishPrice().getRegularPrice()).split("\\.");
                        if (!ObjectUtil.isEmpty(priceArray[0]) && !ObjectUtil.isEmpty(languageId)) {
                            if (languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID)) {
                                holder.tv_product_price.setText(".".concat(priceArray[0]).concat(" ").concat(CURRENCY_CODE));
                            } else {
                                holder.tv_product_price.setText(CURRENCY_CODE.concat(" ").concat(priceArray[0]).concat("."));
                            }
                        }
                        if (!ObjectUtil.isEmpty(priceArray[1])) {
                            holder.tv_price_after_decimal.setText(priceArray[1]);
                        }
                    }
                }
            }
        } else {
            holder.layout_items_included.setVisibility(View.GONE);
            holder.tv_full_dish_price.setVisibility(View.GONE);
            ArrayList<String> unitList = new ArrayList<>();
            if (!ObjectUtil.isEmpty(payloadData.getPrices())) {
                for (EOPrice eoPrice : payloadData.getPrices()) {
                    unitList.add(eoPrice.getUnit());
                }
            }
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, unitList);
            arrayAdapter.setDropDownViewResource(R.layout.spinner_item);
            holder.spinner_quantity.setAdapter(arrayAdapter);
        }

        holder.spinner_quantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ObjectUtil.isEmpty(payloadData.getPrices()) && !ObjectUtil.isEmpty(languageId)) {
                    holder.tv_product_price.setText(CURRENCY_CODE.concat(" ").concat("00."));
                    holder.tv_price_after_decimal.setText("0");
                } else {
                    for (EOPrice eoPrice : payloadData.getPrices()) {
                        if (eoPrice.getUnit().equalsIgnoreCase((String) parent.getItemAtPosition(position))) {
                            String[] priceArray = String.valueOf(eoPrice.getRegularPrice()).split("\\.");
                            if (!ObjectUtil.isEmpty(priceArray[0]) && !ObjectUtil.isEmpty(languageId)) {
                                if (languageId.equalsIgnoreCase(ARABIC_LANGUAGE_ID)) {
                                    holder.tv_product_price.setText(".".concat(priceArray[0]).concat(" ").concat(CURRENCY_CODE));
                                } else {
                                    holder.tv_product_price.setText(CURRENCY_CODE.concat(" ").concat(priceArray[0]).concat("."));
                                }
                            }
                            if (!ObjectUtil.isEmpty(priceArray[1])) {
                                holder.tv_price_after_decimal.setText(priceArray[1]);
                            }
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.productImage.setOnClickListener(new View.OnClickListener() { //TODO from here go to details page
            @Override
            public void onClick(View view) {
                Intent productDetailIntent = new Intent(view.getContext(), ProductDetailsActivity.class);
                productDetailIntent.putExtra("productId", payloadData.getId());
                //productDetailIntent.putExtra("pageTitle", eoProduct.getCatName());
                view.getContext().startActivity(productDetailIntent);
            }
        });

    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .error(R.drawable.icon_no_image)
                .fit()
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(productsDataList) ? 0 : productsDataList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TalabiaTextView tv_product_name, tv_add_to_cart, tv_full_dish_price, tv_add_all_items_to_cart;
        private LinearLayout layout_spinner, layout_items_included;
        private Spinner spinner_quantity;
        private TextView tv_product_price, tv_price_after_decimal, tv_items_included;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_price = itemView.findViewById(R.id.tv_product_price);
            tv_price_after_decimal = itemView.findViewById(R.id.tv_price_after_decimal);
            tv_add_to_cart = itemView.findViewById(R.id.tv_add_to_cart);
            spinner_quantity = itemView.findViewById(R.id.spinner_quantity);
            layout_spinner = itemView.findViewById(R.id.layout_spinner);
            layout_items_included = itemView.findViewById(R.id.layout_items_included);
            tv_items_included = itemView.findViewById(R.id.tv_items_included);
            tv_full_dish_price = itemView.findViewById(R.id.tv_full_dish_price);
            tv_add_all_items_to_cart = itemView.findViewById(R.id.tv_add_all_items_to_cart);
        }
    }

}
