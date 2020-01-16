package com.smartit.talabia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartit.talabia.R;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.entity.allproducts.EOIncludedProductDetails;
import com.smartit.talabia.entity.productDetails.EORelatedSimilarProduct;
import com.smartit.talabia.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class IncludedProductsAdapter extends RecyclerView.Adapter<IncludedProductsAdapter.ViewHolder> {

    private Context context;
    private ArrayList<EORelatedSimilarProduct> productDetailsList;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;

    public IncludedProductsAdapter(Context context, ArrayList<EORelatedSimilarProduct> productDetailsList) {
        this.context = context;
        this.productDetailsList = productDetailsList;

        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public IncludedProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_included_products, parent, false);
        return new IncludedProductsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final @NonNull IncludedProductsAdapter.ViewHolder holder, final int position) {
        final EOIncludedProductDetails productdetails = productDetailsList.get(position).getProductdetails();

        if (!ObjectUtil.isEmpty(productdetails.getFeaturedImage()))
            loadImages(productdetails.getFeaturedImage(), holder.productImage);

        if (position == 2) {
            holder.tv_more_products.setVisibility(View.VISIBLE);
            //holder.tv_more_products.setAlpha(0.5f);
            holder.productImage.setBackground(context.getDrawable(R.drawable.opacity_background));
            if (languageId.equals(ENGLISH_LANGUAGE_ID))
                holder.tv_more_products.setText(String.valueOf(this.productDetailsList.size() - 3).concat("+"));
            else
                holder.tv_more_products.setText("+".concat(String.valueOf(this.productDetailsList.size() - 3)));
        }

//        holder.productImage.setOnClickListener(new View.OnClickListener() { //TODO from here go to details page
//            @Override
//            public void onClick(View view) {
//                Intent productDetailIntent = new Intent(view.getContext(), ProductDetailsActivity.class);
//                productDetailIntent.putExtra("productId", productdetails.getId());
//                //productDetailIntent.putExtra("pageTitle", eoProduct.getCatName());
//                ((ProductDetailsActivity) context).finish();
//                view.getContext().startActivity(productDetailIntent);
//            }
//        });

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
        //return ObjectUtil.isEmpty(this.productDetailsList) ? 0 : this.productDetailsList.size();
        if (this.productDetailsList.size() >= 3) {
            return 3;
        } else {
            this.productDetailsList.size();
        }
        return 0;
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
        private TextView tv_more_products;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            tv_more_products = itemView.findViewById(R.id.tv_more_products);
        }
    }
}
