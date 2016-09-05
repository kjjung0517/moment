package com.skjin.dev.moment.slider;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.skjin.dev.moment.R;
import com.skjin.dev.moment.tasks.ImageLoadingTask;
import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.util.Iterator;

/**
 * Created by kjjung on 16. 8. 27..
 */
public class FullScreenImageAdapter extends PagerAdapter {
    private Activity _activity;
    private AssetCluster _cluster;
    private LayoutInflater inflater;
    private Bitmap bmp;
    private int screenWidth;
    private int screenHeight;

    // constructor
    public FullScreenImageAdapter(Activity activity, AssetCluster cluster, int width, int height) {
        this._activity = activity;
        this._cluster = cluster;
        this.screenWidth = width;
        this.screenHeight = height;

        Bitmap.Config conf = Bitmap.Config.ARGB_4444; // see other conf types
        bmp = Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap
    }

    @Override
    public int getCount() {
        return this._cluster.getAssets().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        TouchImageView imgDisplay; // Replace here with TouchImageView

        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);

        imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay); // this one too

        Iterator<Asset> iterator = this._cluster.getAssets().iterator();
        for ( int i = 0 ; i < position ; i++ ) {
            iterator.next();
        }
        Asset asset = iterator.next();

        final ImageLoadingTask task = new ImageLoadingTask(imgDisplay, false);
        final ImageLoadingTask.AsyncDrawable asyncDrawable
                = new ImageLoadingTask.AsyncDrawable(this._activity.getResources(), bmp, task);
        imgDisplay.setImageDrawable(asyncDrawable);
        task.execute(asset.filePath(), this.screenWidth, this.screenHeight);


        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    public Bitmap getResizedBitmap(Bitmap bm, float newHeight, float newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;

        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

}
