package nus.iss.spotifyteam1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.Date;

public class GeoJsonDemoActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
//    GoogleMap.OnMyLocationButtonClickListener,
//    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng originalMarkerPosition;
    float latitude;
    float longitude;
    String time;

    Button btn;
    public GoogleMap map;
    //mock data to add marker
    void addMarker(){
        float[][] locations = {
                {1.3727795f, 103.88294f},
                {1.408266673925444f, 103.91020935355873f},
                {1.3502465844033085f, 103.67926661136065f},
                {1.3503323912326217f, 103.74664370201187f}
        };
        for(int i = 0;i<locations.length;i++){
            map.addMarker(new MarkerOptions()
                    .position(new LatLng(locations[i][0], locations[i][1]))
                    .title(time)
                    .draggable(true));
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geo_json_demo);

        // Get a handle to the fragment and register the callback.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);
        latitude = pref.getFloat("Latitude", 0);
        longitude = pref.getFloat("Longitude", 0);
        time = pref.getString("Time", "");

        btn = findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
//        LatLng local = new LatLng(latitude, longitude);
        LatLng singapore = new LatLng(1.35, 103.87); // Coordinates for Singapore

        addMarker();
        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                LatLng newPosition = marker.getPosition();
                if (isPositionInsideSingapore(newPosition)) {
                    // Reset the marker's position to a valid point in Singapore
//                    marker.setPosition(getNearestPointInSingapore(newPosition));
                    latitude = (float) newPosition.latitude;
                    longitude = (float) newPosition.longitude;

                }
                else {
                    Toast.makeText(GeoJsonDemoActivity.this,"Singapore Only",Toast.LENGTH_LONG);
                    marker.setPosition(originalMarkerPosition);

                }

            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                originalMarkerPosition = marker.getPosition();
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getApplicationContext(), "New location: "  +latitude+" , "+longitude, Toast.LENGTH_SHORT).show();
                SharedPreferences location = getSharedPreferences("manual_location", MODE_PRIVATE);
                SharedPreferences.Editor editor = location.edit();
                editor.putFloat("Latitude",(float)marker.getPosition().latitude);
                editor.putFloat("Longitude",(float)marker.getPosition().longitude);
                editor.putBoolean("manual",true);
                editor.commit();
                return false;
            }
        });
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(singapore)
                .zoom(17)    // Sets the center of the map to Mountain View
                .build();                   // Creates a CameraPosition from the builder
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        enableLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                enableLocation();
            }

        }

    }
    private boolean isPositionInsideSingapore(LatLng position) {
        double minLat = 1.15;
        double maxLat = 1.47;
        double minLng = 103.59;
        double maxLng = 104.05;

        double lat = position.latitude;
        double lng = position.longitude;

        return lat >= minLat && lat <= maxLat && lng >= minLng && lng <= maxLng;
    }

    private LatLng getNearestPointInSingapore(LatLng position) {
        double lat = Math.max(1.15, Math.min(1.47, position.latitude));
        double lng = Math.max(103.59, Math.min(104.05, position.longitude));
        return new LatLng(lat, lng);
    }

    void enableLocation(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
                .show();
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
                .show();
    }

}