package nus.iss.spotifyteam1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard extends AppCompatActivity implements View.OnClickListener{
    String[] loction = {Manifest.permission.ACCESS_FINE_LOCATION};
    User user = new User();
    int MY_REQ_LOCATION = 11;
    LocationManager locationManager;
    Context mContext;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        SharedPreferences pref = getSharedPreferences("user_obj", MODE_PRIVATE);
        user.setId(pref.getString("user_id",""));
        user.setEmail(pref.getString("user_email",""));
        user.setDisplayName(pref.getString("user_name",""));
        mContext = this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.spotify.music.active");
        registerReceiver(bcr, filter);
        btn = findViewById(R.id.tomap);
        btn.setOnClickListener(this);
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            String dataString = formatter.format(date);
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude + "Data: "+ dataString;
            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

            SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putFloat("Latitude",(float)latitude);
            editor.putFloat("Longitude",(float)longitude);
            editor.putString("Time",dataString);
            editor.commit();
        }
    };

    protected BroadcastReceiver bcr = new MySpotifyReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.spotify.music.active")) {
                getLocation();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bcr);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == MY_REQ_LOCATION){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                getLocation();
            }

        }

    }

    void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,loction,MY_REQ_LOCATION);
            return;
        }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                2000,
                10, locationListenerGPS);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.tomap){
            Intent intent = new Intent(Dashboard.this,GeoJsonDemoActivity.class);
            startActivity(intent);
        }
    }
}