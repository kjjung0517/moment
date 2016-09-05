package com.skjin.dev.recommend.algorithm;

import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

/**
 * Created by kjjung on 16. 8. 25..
 */
public interface ClusteringAlgorithm {

    boolean isAcceptableAsset(Asset asset);
    boolean isAcceptableAsset(Asset asset, AssetCluster cluster);
    boolean isAcceptableCluster(AssetCluster cluster, AssetCluster destCluster);

    void constructContextOfCluster(AssetCluster cluster);
    String algorithmKey();
}
