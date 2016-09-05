package com.skjin.dev.recommend.algorithm;

import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by kjjung on 16. 8. 25..
 */
public class TimeBasedClusterAlgorithm implements ClusteringAlgorithm {

    public static final String kContextKeyTime = "___time";
    public static final String kContextKeyInterval = "___time_interval";
    public static final String kAlgorithmKey = "TimeBasedClusterAlgorithm";

    private long timeInterval;
    public TimeBasedClusterAlgorithm() {
    }

    /*
     * ClusteringAlgorithm interface functions
     */
    public boolean isAcceptableAsset(Asset asset) {
        return (null != asset.date());
    }

    public boolean isAcceptableAsset(Asset asset, AssetCluster cluster) {
        if ( !isAcceptableAsset(asset) ) {
            return false;
        }

        if ( !cluster.algorithmKeys.contains(this.algorithmKey()) ) {
            return false;
        }

        long timeStamp = Long.parseLong(cluster.context.get(TimeBasedClusterAlgorithm.kContextKeyTime));
        Date date = new Date(timeStamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String dateString = format.format(date);

        return dateString.equals(format.format(asset.date()));

    }

    public boolean isAcceptableCluster(AssetCluster cluster, AssetCluster destCluster) {
        return false;
    }

    public void constructContextOfCluster(AssetCluster cluster) {
        if ( null != cluster.context.get(TimeBasedClusterAlgorithm.kContextKeyTime) ) {
            return;
        }

        Iterator<Asset> iterator = cluster.iterator();
        Asset asset = iterator.next();
        cluster.context.put(TimeBasedClusterAlgorithm.kContextKeyTime, "" + asset.date().getTime());

    }

    public String algorithmKey() {
        return TimeBasedClusterAlgorithm.kAlgorithmKey;
    }
}
