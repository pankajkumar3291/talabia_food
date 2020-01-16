package com.smartit.talabia.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smartit.talabia.R;
import com.smartit.talabia.activities.ShowAllProductsActivity;
import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.components.SessionSecuredPreferences;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.expabdable.EOAllCategoryPayload;
import com.smartit.talabia.expabdable.EOChild;
import com.smartit.talabia.expabdable.EOGrandChild;
import com.smartit.talabia.util.DividerItemDecoration;
import com.smartit.talabia.util.GlobalUtil;
import com.smartit.talabia.util.ObjectUtil;

import java.util.ArrayList;

import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

public class AllCategoryMenuAdapter extends RecyclerView.Adapter<AllCategoryMenuAdapter.ViewHolder> {

    private Context context;
    private SessionSecuredPreferences languagePreferences;
    private String languageId;
    private ArrayList<EOAllCategoryPayload> menuArrayList;
    private ArrayList<EOChild> childMenuArrayList = new ArrayList<>();
    private ArrayList<EOGrandChild> grandChildArrayList = new ArrayList<>();
    private String child;
    private String grandChild;
    private boolean isCollapsed;

    public AllCategoryMenuAdapter(Context context, ArrayList<EOAllCategoryPayload> menuArrayList) {
        this.context = context;
        this.menuArrayList = menuArrayList;

        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        setHasStableIds(true);
    }

    private AllCategoryMenuAdapter(Context context, ArrayList<EOChild> childMenuArrayList, String str) {
        this.context = context;
        this.childMenuArrayList = childMenuArrayList;
        this.child = str;

        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        setHasStableIds(true);
    }

    private AllCategoryMenuAdapter(Context context, ArrayList<EOGrandChild> grandChildArrayList, String str, String grandChild) {
        this.context = context;
        this.grandChildArrayList = grandChildArrayList;
        this.child = str;
        this.grandChild = grandChild;

        this.languagePreferences = ApplicationHelper.application().languagePreferences(LANGUAGE_PREFERENCE);
        this.languageId = languagePreferences.getString(SELECTED_LANG_ID, "");
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public AllCategoryMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_expandable_category, parent, false);
        return new AllCategoryMenuAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AllCategoryMenuAdapter.ViewHolder holder, final int position) {
        final EOAllCategoryPayload eoAllCategoryPayload;

        if (!ObjectUtil.isEmpty(child) && child.equals("child")) { //TODO check here child array
            final EOChild eoChild = childMenuArrayList.get(position);
            holder.tv_expandable_view.setText(eoChild.getCatName());
            if (ObjectUtil.isEmpty(eoChild.getGrandchild())) {
                holder.iv_icon.setVisibility(View.GONE);
            }

            holder.tv_expandable_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productCategoryIntent = new Intent(view.getContext(), ShowAllProductsActivity.class);
                    productCategoryIntent.putExtra("productCategoryId", eoChild.getId());
                    productCategoryIntent.putExtra("pageTitle", eoChild.getCatName());
                    productCategoryIntent.putExtra("isCategoryTile", true);
                    view.getContext().startActivity(productCategoryIntent);
                }
            });

            //TODO from here set background for subChild onClick and padding from left and right onChange language
            holder.row_background.setBackgroundColor(context.getResources().getColor(R.color.light_gray_color));
            int pixel = GlobalUtil.getDipFromPixel(context, 25);
            if (!ObjectUtil.isEmpty(this.languageId)) {
                if (this.languageId.equals(ENGLISH_LANGUAGE_ID))
                    holder.row_background.setPadding(pixel, 0, 0, 0);
                else
                    holder.row_background.setPadding(0, 0, pixel, 0);
            }

        } else if (!ObjectUtil.isEmpty(menuArrayList)) {
            eoAllCategoryPayload = this.menuArrayList.get(position);
            holder.tv_expandable_view.setText(eoAllCategoryPayload.getCatName());
            if (ObjectUtil.isEmpty(eoAllCategoryPayload.getChild())) {
                holder.iv_icon.setVisibility(View.GONE);
            }

            holder.tv_expandable_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productCategoryIntent = new Intent(view.getContext(), ShowAllProductsActivity.class);
                    productCategoryIntent.putExtra("productCategoryId", eoAllCategoryPayload.getId());
                    productCategoryIntent.putExtra("pageTitle", eoAllCategoryPayload.getCatName());
                    productCategoryIntent.putExtra("isCategoryTile", true);
                    view.getContext().startActivity(productCategoryIntent);
                }
            });

        } else if (!ObjectUtil.isEmpty(grandChild) && grandChild.equals("subChild")) { //TODO check here subChild array
            final EOGrandChild eoGrandChild = grandChildArrayList.get(position);
            holder.tv_expandable_view.setText(eoGrandChild.getCatName());
            holder.iv_icon.setVisibility(View.GONE);

            holder.tv_expandable_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent productCategoryIntent = new Intent(view.getContext(), ShowAllProductsActivity.class);
                    productCategoryIntent.putExtra("productCategoryId", eoGrandChild.getId());
                    productCategoryIntent.putExtra("pageTitle", eoGrandChild.getCatName());
                    productCategoryIntent.putExtra("isCategoryTile", true);
                    view.getContext().startActivity(productCategoryIntent);
                }
            });

            //TODO from here set background for subChild onClick and padding from left and right onChange language
            holder.row_background.setBackgroundColor(context.getResources().getColor(R.color.gray_default));
            int pixel = GlobalUtil.getDipFromPixel(context, 50);
            if (!ObjectUtil.isEmpty(this.languageId)) {
                if (this.languageId.equals(ENGLISH_LANGUAGE_ID))
                    holder.row_background.setPadding(pixel, 0, 0, 0);
                else
                    holder.row_background.setPadding(0, 0, pixel, 0);
            }

        }

        holder.layout_plus_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCollapsed) {
                    isCollapsed = true;

                    holder.iv_icon.setImageResource(R.drawable.ic_minus);
                    holder.dynamicMenuRecyclerView.setVisibility(View.VISIBLE);
                    holder.dynamicMenuRecyclerView.setHasFixedSize(true);
                    holder.dynamicMenuRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                    holder.dynamicMenuRecyclerView.addItemDecoration(new DividerItemDecoration(context));

                    if (!ObjectUtil.isEmpty(menuArrayList)) {
                        holder.dynamicMenuRecyclerView.setAdapter(new AllCategoryMenuAdapter(context, (ArrayList<EOChild>) menuArrayList.get(position).getChild(), "child"));
                    } else if (!ObjectUtil.isEmpty(child) && child.equals("child")) {
                        holder.dynamicMenuRecyclerView.setAdapter(new AllCategoryMenuAdapter(context, (ArrayList<EOGrandChild>) childMenuArrayList.get(position).getGrandchild(), "", "subChild"));
                    }

                } else {
                    isCollapsed = false;
                    holder.iv_icon.setImageResource(R.drawable.ic_plus);
                    holder.dynamicMenuRecyclerView.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {

        if (!ObjectUtil.isEmpty(child) && child.equals("child")) {
            if (!ObjectUtil.isEmpty(childMenuArrayList))
                return childMenuArrayList.size();
        } else if (ObjectUtil.isEmpty(child)) {
            if (!ObjectUtil.isEmpty(menuArrayList))
                return menuArrayList.size();
        }

        if (!ObjectUtil.isEmpty(grandChild) && grandChild.equals("subChild")) {
            if (!ObjectUtil.isEmpty(grandChildArrayList))
                return grandChildArrayList.size();
        } else if (ObjectUtil.isEmpty(grandChild)) {
            if (!ObjectUtil.isEmpty(menuArrayList))
                return menuArrayList.size();
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

        private TalabiaTextView tv_expandable_view;
        private ImageView iv_icon;
        private RecyclerView dynamicMenuRecyclerView;
        private LinearLayout row_background, layout_plus_icon;

        private ViewHolder(View view) {
            super(view);
            tv_expandable_view = view.findViewById(R.id.tv_expandable_view);
            iv_icon = view.findViewById(R.id.iv_icon);
            dynamicMenuRecyclerView = view.findViewById(R.id.dynamicMenuRecyclerView);
            row_background = view.findViewById(R.id.row_background);
            layout_plus_icon = view.findViewById(R.id.layout_plus_icon);
        }
    }

}
