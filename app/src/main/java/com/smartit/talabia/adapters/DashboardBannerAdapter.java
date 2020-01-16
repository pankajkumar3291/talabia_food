package com.smartit.talabia.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smartit.talabia.R;
import com.smartit.talabia.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DashboardBannerAdapter extends PagerAdapter {

    private ArrayList <String> imageList;
    private Context context;

    public DashboardBannerAdapter(Context context, ArrayList <String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView ( (View) object );
    }

    @Override
    public int getCount() {
        return ObjectUtil.isEmpty ( this.imageList.size ( ) ) ? 0 : this.imageList.size ( );
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup view, final int position) {
        View imageLayout = LayoutInflater.from ( context ).inflate ( R.layout.dashboard_viewpager_banner, view, false );
        assert imageLayout != null;
        ImageView imageView = imageLayout.findViewById ( R.id.image );
        loadImages ( this.imageList.get ( position ), imageView );
        view.addView ( imageLayout );
        return imageLayout;
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get ( )
                .load ( imagePath )
                .error ( R.drawable.icon_no_image )
                .fit ( )
                .into ( imageView );
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals ( object );
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
