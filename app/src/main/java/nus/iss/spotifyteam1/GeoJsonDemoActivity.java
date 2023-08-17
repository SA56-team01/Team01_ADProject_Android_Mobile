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


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import nus.iss.spotifyteam1.Fragment.Playlist;
import nus.iss.spotifyteam1.Fragment.homeFragment;

public class GeoJsonDemoActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
//    GoogleMap.OnMyLocationButtonClickListener,
//    GoogleMap.OnMyLocationClickListener, ActivityCompat.OnRequestPermissionsResultCallback
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng originalMarkerPosition;
    float latitude;
    float longitude;
    String time;
    String userid;

    Thread loadPlayList;

    Button btn;
    public GoogleMap map;
    //mock data to add marker
//    void addMarker(){
//        float[][] locations = {
//                {1.3727795f, 103.88294f},
//                {1.408266673925444f, 103.91020935355873f},
//                {1.3502465844033085f, 103.67926661136065f},
//                {1.3503323912326217f, 103.74664370201187f}
//        };
//        for(int i = 0;i<locations.length;i++){
//            map.addMarker(new MarkerOptions()
//                    .position(new LatLng(locations[i][0], locations[i][1]))
//                    .title(time)
//                    .draggable(true));
//        }
//
//    }


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


        SharedPreferences user = getSharedPreferences("user_obj", Context.MODE_PRIVATE);
        userid = user.getString("user_id","");



        btn = findViewById(R.id.back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GeoJsonDemoActivity.this,HomepageActivity.class);
                startActivity(intent);

//                finish();

            }
        });

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
//        LatLng local = new LatLng(latitude, longitude);
        LatLng singapore = new LatLng(1.35, 103.87); // Coordinates for Singapore

//        addMarker();
        loadPlayList = getAllList();
        loadPlayList.start();
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
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences location = getSharedPreferences("manual_location", MODE_PRIVATE);
        SharedPreferences.Editor editor = location.edit();
        editor.putFloat("Latitude",0);
        editor.putFloat("Longitude",0);
        editor.putBoolean("manual",false);
        editor.commit();

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

    public Thread getAllList(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(getString(R.string.BACKEND_URL) + "playlists/user/"+userid);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            if(response.toString()!=null){

                                JSONArray jsonArray = new JSONArray(response.toString());
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    int playListID = jsonObject.getInt("id");
                                    String name = jsonObject.getString("playlistName");
                                    String spotifyPlaylistId = jsonObject.getString("spotifyPlaylistId");
                                    Float longi = Float.parseFloat(jsonObject.getString("longitude"));
                                    Float latitude = Float.parseFloat(jsonObject.getString("latitude"));
                                    String timestampString = jsonObject.getString("timestamp");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date time = dateFormat.parse(timestampString);
                                    runOnUiThread(new Runnable() {
                                        //                {1.408266673925444f, 103.91020935355873f},

                                        @Override
                                        public void run() {
                                            map.addMarker(new MarkerOptions()
                                                    .position(new LatLng(longi,latitude))
                                                    .title(name + time +spotifyPlaylistId)
                                                    .draggable(true));
                                        }
                                    });


                                }

                            };
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        loadPlayList =null;

                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                        loadPlayList =null;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    loadPlayList =null;
                }
            }
        });

    }

}