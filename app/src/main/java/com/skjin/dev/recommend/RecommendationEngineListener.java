package com.skjin.dev.recommend;

import com.skjin.dev.recommend.cluster.AssetCluster;

import java.util.LinkedList;

/**
 * Created by kjjung on 16. 8. 24..
 */
public interface RecommendationEngineListener {

    void progressChanged(float progress);
    void progressFinished();
}
