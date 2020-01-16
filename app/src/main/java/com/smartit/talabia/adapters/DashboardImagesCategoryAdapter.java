package com.smartit.talabia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartit.talabia.R;
import com.smartit.talabia.activities.ShowAllProductsActivity;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.dashboard.EOImageCategory;
import com.smartit.talabia.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by android on 19/4/19.
 */

public class DashboardImagesCategoryAdapter extends RecyclerView.Adapter <DashboardImagesCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList <EOImageCategory> imageCategoriesList;

    public DashboardImagesCategoryAdapter(Context context, ArrayList <EOImageCategory> imageCategoriesList) {
        this.context = context;
        this.imageCategoriesList = imageCategoriesList;
    }

    @NonNull
    @Override
    public DashboardImagesCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_images_category, parent, false );
        return new DashboardImagesCategoryAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull DashboardImagesCategoryAdapter.ViewHolder holder, final int position) {
        final EOImageCategory imageCategory = imageCategoriesList.get ( position );

        holder.tv_category_name.setText ( imageCategory.getCatName ( ) );
        loadImages ( imageCategory.getThumbnail ( ), holder.productImage );

        holder.itemView.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent productCategoryIntent = new Intent ( view.getContext ( ), ShowAllProductsActivity.class );
                productCategoryIntent.putExtra ( "productCategoryId", imageCategory.getId ( ) );
                productCategoryIntent.putExtra ( "pageTitle", imageCategory.getCatName ( ) );
                productCategoryIntent.putExtra ( "isCategoryTile", true );
                view.getContext ( ).startActivity ( productCategoryIntent );
            }
        } );
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( imagePath )
                .error ( R.drawable.icon_no_image )
                .fit ( )
                .into ( imageView );
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.imageCategoriesList ) ? 0 : this.imageCategoriesList.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView productImage;
        private TalabiaTextView tv_category_name;

        private ViewHolder(@NonNull View itemView) {
            super ( itemView );
            productImage = itemView.findViewById ( R.id.productImage );
            tv_category_name = itemView.findViewById ( R.id.tv_category_name );
        }
    }

}
