package com.skjin.dev.moment.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.algorithm.LocationBasedClusterAlgorithm;
import com.skjin.dev.recommend.algorithm.TimeBasedClusterAlgorithm;
import com.skjin.dev.recommend.asset.Asset;

import java.util.LinkedList;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class ClusteringTask extends AsyncTask {

    private Activity activity;
    public ClusteringTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        RecommendationEngine.getInstance().addClusteringAlgorithm(new TimeBasedClusterAlgorithm());
        RecommendationEngine.getInstance().addClusteringAlgorithm(new LocationBasedClusterAlgorithm(5000.0)); // 5000 meter
        LinkedList<Asset> assets = RecommendationEngine.getInstance().getAllDeviceImages(this.activity);
        RecommendationEngine.getInstance().clusteringWithAssets(assets);

        return null;
    }
}
