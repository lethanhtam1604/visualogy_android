package com.visualogyx.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.visualogyx.R;
import com.visualogyx.Utils.Utils;

public class GoogleMapManager implements OnMapReadyCallback, LocationListener {

    private GoogleMap googleMap;
    private Context context;
    private LatLng latLng = new LatLng(10.7703238, 106.671691);
    public boolean checkInHistoryDetail, selectLocation;
    private MapFragment mapFragment;
    private Location location;
    private LocationManager locationManager;
    private String mprovider;
    public LatLng myLatLng;

    public GoogleMapManager(Context context, MapFragment mapFragment, LatLng latLng) {
        this.context = context;
        this.latLng = latLng;
        myLatLng = latLng;
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        Criteria criteria = new Criteria();

        mprovider = locationManager.getBestProvider(criteria, false);
        //googleMap.setMyLocationEnabled(true);
        location = getLastBestLocation();
        if (location != null)
            myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        locationManager.requestLocationUpdates(mprovider, 15000, 1, this);
        if (checkInHistoryDetail && latLng != null) {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Place"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            myLatLng = latLng;
        }

        if (!checkInHistoryDetail && myLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(myLatLng).title("My Location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));
        }

        this.googleMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if (checkInHistoryDetail == true && !selectLocation)
                    return;
                if (currentMaker != null)
                    currentMaker.remove();

                myLatLng = latLng;

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.blue);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(Utils.scaleDown(icon, 40, true)));

                currentMaker = GoogleMapManager.this.googleMap.addMarker(markerOptions);
                GoogleMapManager.this.googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });
    }

    private Marker currentMaker = null;

    @Override
    public void onLocationChanged(Location location) {
        if (checkInHistoryDetail && latLng != null) {
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Place"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            myLatLng = latLng;
        }

        if (!checkInHistoryDetail && myLatLng != null) {
            googleMap.addMarker(new MarkerOptions().position(myLatLng).title("My Location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18));
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    private Location getLastBestLocation() {
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }
}
