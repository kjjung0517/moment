package com.skjin.dev.moment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.skjin.dev.moment.adapters.AssetAdapter;
import com.skjin.dev.moment.adapters.ClusterAdapter;
import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.algorithm.TimeBasedClusterAlgorithm;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.card_detail);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        Intent intent = getIntent();
        String algorithmKey = intent.getStringExtra("algorithmKey");
        int index = intent.getIntExtra("index", 0);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int imageWidth = metrics.widthPixels / 2;
        recyclerView.setAdapter(new AssetAdapter(imageWidth, index, algorithmKey));

    }
}
