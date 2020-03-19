package com.smartit.talabia.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.smartit.talabia.R;
import com.smartit.talabia.activities.AboutUsActivity;
import com.smartit.talabia.activities.AllCategoriesActivity;
import com.smartit.talabia.activities.ProfileActivity;
import com.smartit.talabia.activities.SettingsActivity;
import com.smartit.talabia.components.TalabiaTextView;
import com.smartit.talabia.util.ObjectUtil;

/**
 * Created by android on 9/4/19.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private Activity context;
    private String[] menuStringList;
    private int[] menuImageList;
    private int row_index = -1;
    private DrawerLayout drawer;

    public MenuAdapter(Activity context, String[] menuStringList, int[] menuImageList, DrawerLayout drawer) {
        this.context = context;
        this.menuStringList = menuStringList;
        this.menuImageList = menuImageList;
        this.drawer = drawer;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.menu_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final String menuTitle = this.menuStringList[position];
        int menuImage = this.menuImageList[position];

        holder.tv_menu_title.setText(menuTitle);
        holder.iv_menu_icon.setImageResource(menuImage);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                notifyDataSetChanged();
            }
        });

        if (row_index == position) {
            holder.menu_row_bg.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
            holder.tv_menu_title.setTextColor(context.getResources().getColor(android.R.color.white));
            holder.iv_menu_icon.setColorFilter(context.getResources().getColor(android.R.color.white));

            if (row_index == 1) {
                context.startActivity(new Intent(context, ProfileActivity.class));
                if (this.drawer.isDrawerOpen(GravityCompat.START)) {
                    this.drawer.closeDrawers();
                }
            } else if (row_index == 6) {
                Intent settingsIntent = new Intent(context, SettingsActivity.class);
                context.startActivity(settingsIntent);
                if (this.drawer.isDrawerOpen(GravityCompat.START)) {
                    this.drawer.closeDrawers();
                }
            } else if (row_index == 7) {
                Intent aboutUsIntent = new Intent(context, AboutUsActivity.class);
                context.startActivity(aboutUsIntent);
                if (this.drawer.isDrawerOpen(GravityCompat.START)) {
                    this.drawer.closeDrawers();
                }
            } else if (row_index == 3) {
                context.startActivity(new Intent(context, AllCategoriesActivity.class));
                if (this.drawer.isDrawerOpen(GravityCompat.START)) {
                    this.drawer.closeDrawers();
                }
            }

        } else {
            holder.menu_row_bg.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            holder.tv_menu_title.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.iv_menu_icon.setColorFilter(context.getResources().getColor(android.R.color.black));
        }

    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.menuStringList) ? 0 : this.menuStringList.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TalabiaTextView tv_menu_title;
        private ImageView iv_menu_icon;
        private LinearLayout menu_row_bg;

        private ViewHolder(View view) {
            super(view);
            tv_menu_title = view.findViewById(R.id.tv_menu_title);
            iv_menu_icon = view.findViewById(R.id.iv_menu_icon);
            menu_row_bg = view.findViewById(R.id.menu_row_bg);
        }
    }

}
