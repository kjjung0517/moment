package com.skjin.dev.moment.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skjin.dev.moment.R;
import com.skjin.dev.moment.slider.FullScreenViewActivity;
import com.skjin.dev.moment.tasks.ImageLoadingTask;
import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {

    private int imageWidth;
    private int index;
    private String algorithmKey;
    private Bitmap blankBitmap;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);

            imageView = (ImageView)view.findViewById(R.id.image);
            textView = (TextView)view.findViewById(R.id.textview);
            cardView = (CardView)view.findViewById(R.id.cardview);
        }
    }

    public AssetAdapter(int imageWidth, int index, String algorithmKey) {
        this.imageWidth = imageWidth;
        this.index = index;
        this.algorithmKey = algorithmKey;

        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        this.blankBitmap = Bitmap.createBitmap(100, 100, conf);
    }

    @Override
    public AssetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AssetCluster cluster = RecommendationEngine.getInstance().getClusters(this.algorithmKey).get(this.index);
        Iterator<Asset> iterator = cluster.getAssets().iterator();
        for ( int i = 0 ; i < position ; i++ ) {
            iterator.next();
        }
        Asset asset = iterator.next();

        Date date = asset.date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String dateString = format.format(date);
        holder.textView.setText(dateString);

        final ImageLoadingTask task = new ImageLoadingTask(holder.imageView, true);
        final ImageLoadingTask.AsyncDrawable asyncDrawable
                = new ImageLoadingTask.AsyncDrawable(holder.imageView.getContext().getResources(), blankBitmap, task);
        holder.imageView.setImageDrawable(asyncDrawable);
        task.execute(asset.filePath(), imageWidth, imageWidth);

        final int clusterIndex = this.index;
        final String algorithmKey = this.algorithmKey;
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), FullScreenViewActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("cluster_index", clusterIndex);
                intent.putExtra("algorithmKey", algorithmKey);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        AssetCluster cluster = RecommendationEngine.getInstance().getClusters(this.algorithmKey).get(this.index);
        return cluster.getAssets().size();

    }

}
