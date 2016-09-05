package com.skjin.dev.moment.slider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;

import com.skjin.dev.moment.R;
import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.cluster.AssetCluster;

/**
 * Created by kjjung on 16. 8. 27..
 */
public class FullScreenViewActivity extends Activity {
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_view);

        viewPager = (ViewPager) findViewById(R.id.pager);

        Intent i = getIntent();
        int clusterIndex = i.getIntExtra("cluster_index", 0);
        String algorithmKey = i.getStringExtra("algorithmKey");
        AssetCluster cluster = RecommendationEngine.getInstance().getClusters(algorithmKey).get(clusterIndex);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, cluster, metrics.widthPixels, metrics.heightPixels);

        viewPager.setAdapter(adapter);

        // displaying selected image first
        int index = i.getIntExtra("index", 0);
        viewPager.setCurrentItem(index);
    }
}
