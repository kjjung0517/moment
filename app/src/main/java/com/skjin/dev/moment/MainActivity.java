package com.skjin.dev.moment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.skjin.dev.moment.fragments.CardFragment;
import com.skjin.dev.moment.fragments.MapFragment;
import com.skjin.dev.moment.tasks.ClusteringTask;
import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.RecommendationEngineListener;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements RecommendationEngineListener {

    private ProgressDialog dialog;
    private CardFragment cardFragment;
    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set action bar buttons
        View buttonLayout = getLayoutInflater().inflate(R.layout.layout_button, null);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(buttonLayout);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Progress clustering..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        RecommendationEngine.getInstance().listener = this;
        ClusteringTask task = new ClusteringTask(this);
        task.execute();
    }

    /*
     * Related ActionBar button
     */
    public void onButtonClicked(View view) {

        Fragment fragment;
        if ( findViewById(R.id.button_card) == view ) {
            fragment = this.getCardFragment();
        } else {
            fragment = this.getMapFragment();
        }

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment, fragment);
        fragmentTransaction.commit();
    }

    /*
     * Related Recommendation Engine
     */
    public void progressChanged(final float progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setProgress((int)(progress * 100));
            }
        });
    }

    public void progressFinished() {

        final CardFragment fragment = this.getCardFragment();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment, fragment);
                fragmentTransaction.commit();
            }
        });

    }

    /*
     * Private methods
     */
    private CardFragment getCardFragment() {
        if ( null == this.cardFragment ) {
            this.cardFragment = new CardFragment();
        }

        return this.cardFragment;
    }

    private MapFragment getMapFragment() {
        if ( null == this.mapFragment ) {
            this.mapFragment = new MapFragment();
        }

        return this.mapFragment;
    }
}
