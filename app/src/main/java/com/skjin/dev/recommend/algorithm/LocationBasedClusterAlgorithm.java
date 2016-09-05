package com.skjin.dev.recommend.algorithm;

import android.location.Location;
import android.util.Log;

import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.util.Iterator;

/**
 * Created by kjjung on 16. 8. 25..
 */
public class LocationBasedClusterAlgorithm implements ClusteringAlgorithm {

    public static final String kContextKeyLocation = "___loc";
    public static final String kContextKeyDistance = "___loc_distance";
    public static final String kAlgorithmKey = "LocationBasedClusterAlgorithm";

    private double distance;
    public LocationBasedClusterAlgorithm(double distance) {
        this.distance = distance;
    }

    /*
     * ClusteringAlgorithm interface functions
     */
    public boolean isAcceptableAsset(Asset asset) {
        return (null != asset.location());

    }

    public boolean isAcceptableAsset(Asset asset, AssetCluster cluster) {
        if ( !isAcceptableAsset(asset) ) {
            return false;
        }

        if ( !cluster.algorithmKeys.contains(this.algorithmKey()) ) {
            return false;
        }

        String locationString = cluster.context.get(LocationBasedClusterAlgorithm.kContextKeyLocation);
        Location location = new Location("AssetLocationAlgorithm");
        location.setLatitude(Double.parseDouble(locationString.split(",")[0]));
        location.setLongitude(Double.parseDouble(locationString.split(",")[1]));
        double distance = Double.parseDouble(cluster.context.get(LocationBasedClusterAlgorithm.kContextKeyDistance));

        return Math.abs(asset.location().distanceTo(location)) < distance;

    }

    public boolean isAcceptableCluster(AssetCluster cluster, AssetCluster destCluster) {

        if ( !cluster.algorithmKeys.contains(this.algorithmKey()) ) {
            return false;
        }

        if ( !destCluster.algorithmKeys.contains(this.algorithmKey()) ) {
            return false;
        }

        String locationString = cluster.context.get(LocationBasedClusterAlgorithm.kContextKeyLocation);
        Location location = new Location("AssetLocationAlgorithm");
        location.setLatitude(Double.parseDouble(locationString.split(",")[0]));
        location.setLongitude(Double.parseDouble(locationString.split(",")[1]));

        String destLocationString = destCluster.context.get(LocationBasedClusterAlgorithm.kContextKeyLocation);
        Location destLocation = new Location("AssetLocationAlgorithm");
        destLocation.setLatitude(Double.parseDouble(destLocationString.split(",")[0]));
        destLocation.setLongitude(Double.parseDouble(destLocationString.split(",")[1]));

        double distance = Double.parseDouble(cluster.context.get(LocationBasedClusterAlgorithm.kContextKeyDistance));

        return Math.abs(destLocation.distanceTo(location)) < distance;

    }

    public void constructContextOfCluster(AssetCluster cluster) {
        double latitude = 0.0;
        double longitude = 0.0;
        Iterator<Asset> iterator = cluster.iterator();
        int num_of_assets = 0;
        while ( iterator.hasNext() ) {
            Asset asset = iterator.next();
            if ( !isAcceptableAsset(asset) ) {
                continue;
            }

            latitude += asset.location().getLatitude();
            longitude += asset.location().getLongitude();
            num_of_assets++;
        }

        Location location = new Location("AssetLocationAlgorithm");
        location.setLatitude(latitude / num_of_assets);
        location.setLongitude(longitude / num_of_assets);

        double maxDistance = this.distance;
        while ( iterator.hasNext() ) {
            Asset asset = iterator.next();
            if ( !isAcceptableAsset(asset) ) {
                continue;
            }

            double distance = Math.abs(location.distanceTo(asset.location()));
            if ( distance > maxDistance ) {
                maxDistance = distance;
            }
        }

        cluster.context.put(LocationBasedClusterAlgorithm.kContextKeyDistance, "" + maxDistance);
        cluster.context.put(LocationBasedClusterAlgorithm.kContextKeyLocation, location.getLatitude() + "," + location.getLongitude());
        cluster.algorithmKeys.add(this.algorithmKey());

    }

    public String algorithmKey() {
        return LocationBasedClusterAlgorithm.kAlgorithmKey;
    }

}
