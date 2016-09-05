package com.skjin.dev.recommend.cluster;

import com.skjin.dev.recommend.asset.Asset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by kjjung on 16. 8. 24..
 */
public class AssetCluster {

    private ArrayList<Asset> assetsSet = new ArrayList<>();
    private ArrayList<Asset> assets = null;
    public HashSet<String> algorithmKeys = new HashSet<>();
    public HashMap<String, String> context = new HashMap<>();


    public void addAsset(Asset asset) {
        this.assetsSet.add(asset);
    }

    public void addAssetsFromCluster(AssetCluster cluster) {
        Iterator<Asset> iterator = cluster.assetsSet.iterator();
        while ( iterator.hasNext() ) {
            Asset asset = iterator.next();
            this.assetsSet.add(asset);
        }

        cluster.assetsSet.clear();
    }

    public ArrayList<Asset> getAssets() {
        if ( null == this.assets ) {
            this.assets = this.assetsSet;
            Collections.sort(this.assets, new Comparator<Asset>() {
                @Override
                public int compare(Asset asset1, Asset asset2) {
                    return asset1.date().getTime() > asset2.date().getTime() ? -1 : 1;
                }
            });
        }

        return this.assets;
    }

    public Iterator<Asset> iterator() {
        return this.assetsSet.iterator();
    }
}
