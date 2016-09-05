package com.skjin.dev.moment.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skjin.dev.moment.DetailActivity;
import com.skjin.dev.moment.R;
import com.skjin.dev.moment.tasks.ImageLoadingTask;
import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.algorithm.TimeBasedClusterAlgorithm;
import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class ClusterAdapter extends RecyclerView.Adapter<ClusterAdapter.ViewHolder> {

    private int imageWidth;
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

    public ClusterAdapter(int imageWidth) {
        this.imageWidth = imageWidth;

        Bitmap.Config conf = Bitmap.Config.ARGB_4444;
        this.blankBitmap = Bitmap.createBitmap(100, 100, conf);
    }

    @Override
    public ClusterAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AssetCluster cluster = RecommendationEngine.getInstance().getClusters(TimeBasedClusterAlgorithm.kAlgorithmKey).get(position);
        long time = Long.parseLong(cluster.context.get(TimeBasedClusterAlgorithm.kContextKeyTime));
        Date date = new Date(time);

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        String dateString = format.format(date);

        holder.textView.setText(dateString);

        Asset asset = null;

        Iterator<Asset> iterator = cluster.iterator();
        while ( iterator.hasNext() ) {
            asset = iterator.next();
        }

        final ImageLoadingTask task = new ImageLoadingTask(holder.imageView, true);
        final ImageLoadingTask.AsyncDrawable asyncDrawable
                = new ImageLoadingTask.AsyncDrawable(holder.imageView.getContext().getResources(), blankBitmap, task);
        holder.imageView.setImageDrawable(asyncDrawable);
        task.execute(asset.filePath(), imageWidth, imageWidth);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("algorithmKey", TimeBasedClusterAlgorithm.kAlgorithmKey);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return RecommendationEngine.getInstance().getClusters(TimeBasedClusterAlgorithm.kAlgorithmKey).size();
    }
}
