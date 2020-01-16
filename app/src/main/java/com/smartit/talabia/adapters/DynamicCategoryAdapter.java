package com.smartit.talabia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartit.talabia.R;
import com.smartit.talabia.activities.ShowAllProductsActivity;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.entity.dashboard.EODashboardCategoryPayload;
import com.smartit.talabia.entity.dashboard.EOProduct;
import com.smartit.talabia.util.ObjectUtil;

import java.util.ArrayList;

/**
 * Created by android on 22/4/19.
 */

public class DynamicCategoryAdapter extends RecyclerView.Adapter <DynamicCategoryAdapter.ViewHolder> {

    private Context context;
    private ArrayList <EODashboardCategoryPayload> dashboardCategoryPayloads;

    public DynamicCategoryAdapter(Context context, ArrayList <EODashboardCategoryPayload> dashboardCategoryPayloads) {
        this.context = context;
        this.dashboardCategoryPayloads = dashboardCategoryPayloads;
    }

    @NonNull
    @Override
    public DynamicCategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from ( context ).inflate ( R.layout.row_dynamic_category, parent, false );
        return new DynamicCategoryAdapter.ViewHolder ( view );
    }

    @Override
    public void onBindViewHolder(@NonNull DynamicCategoryAdapter.ViewHolder holder, final int position) {
        final EODashboardCategoryPayload dashboardCategoryPayload = this.dashboardCategoryPayloads.get ( position );

        holder.tv_category_name.setText ( dashboardCategoryPayload.getCollectionName ( ) );

        holder.dynamicCategoryRecyclerView.setHasFixedSize ( true );
        holder.dynamicCategoryRecyclerView.setLayoutManager ( new LinearLayoutManager ( context, LinearLayoutManager.HORIZONTAL, false ) );
        holder.dynamicCategoryRecyclerView.setAdapter ( new DashboardCategoryAdapter ( context, (ArrayList <EOProduct>) dashboardCategoryPayload.getProducts ( ) ) );

        holder.tv_view_all.setOnClickListener ( new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Intent productCategoryIntent = new Intent ( view.getContext ( ), ShowAllProductsActivity.class );
                productCategoryIntent.putExtra ( "productCategoryId", dashboardCategoryPayload.getId ( ) );
                productCategoryIntent.putExtra ( "pageTitle", dashboardCategoryPayload.getCollectionName ( ) );
                view.getContext ( ).startActivity ( productCategoryIntent );
            }
        } );

    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty ( this.dashboardCategoryPayloads ) ? 0 : this.dashboardCategoryPayloads.size ( );
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TalabiaTextView tv_category_name, tv_view_all;
        private RecyclerView dynamicCategoryRecyclerView;

        private ViewHolder(@NonNull View itemView) {
            super ( itemView );
            tv_category_name = itemView.findViewById ( R.id.tv_category_name );
            tv_view_all = itemView.findViewById ( R.id.tv_view_all );
            dynamicCategoryRecyclerView = itemView.findViewById ( R.id.dynamicCategoryRecyclerView );
        }
    }

}
