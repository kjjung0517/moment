package com.skjin.dev.moment.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.skjin.dev.moment.DetailActivity;
import com.skjin.dev.moment.R;
import com.skjin.dev.moment.tasks.ImageLoadingTask;
import com.skjin.dev.moment.tasks.ThumbnailTask;
import com.skjin.dev.recommend.RecommendationEngine;
import com.skjin.dev.recommend.algorithm.LocationBasedClusterAlgorithm;
import com.skjin.dev.recommend.asset.Asset;
import com.skjin.dev.recommend.cluster.AssetCluster;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

/**
 * Created by kjjung on 16. 8. 29..
 */
public class MapFragment extends Fragment implements LocationListener {

    public MapView mapView;
    private GoogleMap googleMap = null;
    private Location backupedLocation = null;
    boolean flag = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView)rootView.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        mapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int index = (int)marker.getTag();

                        Intent intent = new Intent(mapView.getContext(), DetailActivity.class);
                        intent.putExtra("index", index);
                        intent.putExtra("algorithmKey", LocationBasedClusterAlgorithm.kAlgorithmKey);
                        mapView.getContext().startActivity(intent);

                        return false;
                    }
                });

                LinkedList<AssetCluster> clusters = RecommendationEngine.getInstance().getClusters(LocationBasedClusterAlgorithm.kAlgorithmKey);
                for ( int i = 0 ; i < clusters.size() ; i++ ) {
                    AssetCluster cluster = clusters.get(i);
                    String locationString = cluster.context.get(LocationBasedClusterAlgorithm.kContextKeyLocation);

                    if ( null == locationString ) {
                        continue;
                    }

                    final LatLng location = new LatLng(Double.parseDouble(locationString.split(",")[0]), Double.parseDouble(locationString.split(",")[1]));

                    Asset asset = null;

                    Iterator<Asset> iterator = cluster.iterator();
                    while ( iterator.hasNext() ) {
                        asset = iterator.next();
                    }

                    final ThumbnailTask task = new ThumbnailTask();
                    try {
                        Bitmap bitmap = (Bitmap)task.execute(asset.filePath(), 150).get();
                        Marker marker = mMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromBitmap(bitmap)));
                        marker.setTag(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }

                // For showing a move to my location button
                if (ContextCompat.checkSelfPermission(mapView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    // Show rationale and request permission.
                }

                if ( null != backupedLocation ) {
                    // Creating a LatLng object for the current location
                    LatLng latLng = new LatLng(backupedLocation.getLatitude(), backupedLocation.getLongitude());

                    // Showing the current location in Google Map
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    // Zoom in the Google Map
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                }
            }
        });

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager)this.getActivity().getSystemService(Context.LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);


        if(location != null){
            onLocationChanged(location);
        }
        locationManager.requestLocationUpdates(provider, 0, 0, this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /*
     * Location Listener
     */
    @Override
    public void onLocationChanged(Location location) {
        this.backupedLocation = location;
        if ( null == this.googleMap ) {
            return;
        }

        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        if( false == flag ) {
            // Showing the current location in Google Map
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

            flag=true;
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
}
