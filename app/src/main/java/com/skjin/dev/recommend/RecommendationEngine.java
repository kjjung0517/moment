package com.skjin.dev.recommend;

import android.app.Activity;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.skjin.dev.recommend.algorithm.ClusteringAlgorithm;
import com.skjin.dev.recommend.algorithm.TimeBasedClusterAlgorithm;
import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by kjjung on 16. 8. 25..
 */
public class RecommendationEngine {

    private static final String tag = "RecommendationEngine";
    // single ton
    private static RecommendationEngine ourInstance = new RecommendationEngine();
    public static RecommendationEngine getInstance() {
        return ourInstance;
    }

    private HashMap<String, ClusteringAlgorithm> algorithms = new HashMap<>();
    private HashMap<String, LinkedList<AssetCluster>> clusters = new HashMap<>();
    private RecommendationEngine() {
    }

    public RecommendationEngineListener listener = null;
    public void addClusteringAlgorithm(ClusteringAlgorithm algorithm) {
        this.algorithms.put(algorithm.algorithmKey(), algorithm);
        this.clusters.put(algorithm.algorithmKey(), new LinkedList<AssetCluster>());
    }

    public void clusteringWithAssets(LinkedList<Asset> assets) {
        Log.v(RecommendationEngine.tag, "====== Clustering started.");

        float progress = 0.0f;
        this.delegateProgress(progress);
        float progressForOne = 0.95f / (assets.size() * this.algorithms.keySet().size());

        Iterator<String> keyIterator = this.algorithms.keySet().iterator();
        while ( keyIterator.hasNext() ) {
            // get algorithm
            String algorithmKey = keyIterator.next();
            ClusteringAlgorithm algorithm = this.algorithms.get(algorithmKey);

            // clear cluster
            LinkedList<AssetCluster> clustersForAlgorithm = this.clusters.get(algorithmKey);
            clustersForAlgorithm.clear();

            Iterator<Asset> iterator = assets.iterator();
            while ( iterator.hasNext() ) {

                progress += progressForOne;
                this.delegateProgress(progress);

                Asset asset = iterator.next();
                if ( !algorithm.isAcceptableAsset(asset) ) {
                    continue;
                }

                boolean clustered = false;
                for ( int i = 0 ; i < clustersForAlgorithm.size() ; i++ ) {
                    AssetCluster cluster = clustersForAlgorithm.get(i);
                    if ( algorithm.isAcceptableAsset(asset, cluster) ) {
                        cluster.addAsset(asset);
                        clustered = true;
                        this.constructContextOfCluster(cluster);
                    }
                }

                if ( !clustered ) {
                    AssetCluster cluster = new AssetCluster();
                    cluster.addAsset(asset);
                    cluster.algorithmKeys.add(algorithmKey);
                    this.constructContextOfCluster(cluster);
                    clustersForAlgorithm.add(cluster);
                }
            }

            // sort by time
            if ( algorithmKey.equals(TimeBasedClusterAlgorithm.kAlgorithmKey) ) {
                Collections.sort(clustersForAlgorithm, new Comparator<AssetCluster>() {
                    @Override
                    public int compare(AssetCluster cluster1, AssetCluster cluster2) {
                        long timeStamp1 = Long.parseLong(cluster1.context.get(TimeBasedClusterAlgorithm.kContextKeyTime));
                        long timeStamp2 = Long.parseLong(cluster2.context.get(TimeBasedClusterAlgorithm.kContextKeyTime));
                        return timeStamp1 > timeStamp2 ? -1 : 1;
                    }
                });
            }
        }

        Log.v(RecommendationEngine.tag, "Cluster size after merge : " + clusters.size());
        Log.v(RecommendationEngine.tag, "====== Clustering finished.");
        if ( null != this.listener ) {
            this.listener.progressChanged(1.0f);
            this.listener.progressFinished();
        }

    }

    private void delegateProgress(float progress) {
        if ( null != this.listener ) {
            this.listener.progressChanged(progress);
        }
    }

    private void constructContextOfCluster(AssetCluster cluster) {
        Iterator<String> keyIterator = cluster.algorithmKeys.iterator();
        while ( keyIterator .hasNext() ) {
            String key = keyIterator.next();
            ClusteringAlgorithm algorithm = this.algorithms.get(key);
            algorithm.constructContextOfCluster(cluster);
        }
    }

    public LinkedList<Asset> getAllDeviceImages(Activity activity) {
        Uri uri;
        LinkedList<Asset> assets = new LinkedList<>();
        Cursor cursor;
        int column_index_data;
        String imagePath;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        while (cursor.moveToNext()) {
            imagePath = cursor.getString(column_index_data);
            try {
                ExifInterface exif = new ExifInterface(imagePath);
                Asset asset = new Asset(imagePath, exif);
                assets.add(asset);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return assets;
    }

    public LinkedList<AssetCluster> getClusters(String algorithmKey) {

        return this.clusters.get(algorithmKey);
    }
}
