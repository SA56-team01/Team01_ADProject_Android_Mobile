package nus.iss.spotifyteam1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import nus.iss.spotifyteam1.Adapter.ViewPagerAdapter;
import nus.iss.spotifyteam1.Fragment.homeFragment;
import nus.iss.spotifyteam1.Fragment.moreFragment;
import nus.iss.spotifyteam1.Fragment.userFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener{
    private BottomNavigationView navigationView;
    private ViewPager viewPager;

    Thread bkgdThread;


    //从这里开始是原来Dashboard的部分
    String[] loction = {Manifest.permission.ACCESS_FINE_LOCATION};
    User user = new User();
    int MY_REQ_LOCATION = 11;
    LocationManager locationManager;
    Context mContext;
    Button btn;
    double latitude;
    double longitude;
    String dataString;
    String trackId;
    String previousTrackId;


    boolean isManualLocation = false;

    //从这里为止是原来Dashboard的部分

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        SharedPreferences location = getSharedPreferences("manual_location", MODE_PRIVATE);
        isManualLocation = location.getBoolean("manual",false);


        //从这里开始是原来Dashboard的部分
        SharedPreferences pref = getSharedPreferences("user_obj", MODE_PRIVATE);
        user.setId(pref.getString("user_id",""));
        user.setEmail(pref.getString("user_email",""));
        user.setDisplayName(pref.getString("user_name",""));
        mContext = this;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        regis();
        btn = findViewById(R.id.toMap);
        btn.setOnClickListener(this);
        //从这里为止是原来Dashboard的部分


        navigationView = findViewById(R.id.nav_bottom);
        viewPager = findViewById(R.id.vp);

        homeFragment homeFragment= new homeFragment();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new homeFragment()); //新建一个homeFragment对象将这个对象加入到数组fragments中
        fragments.add(new moreFragment());
        fragments.add(new userFragment());

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),fragments);
        //创建对象并通过构造函数初始化，该适配器可以知道要显示哪些片段。
        viewPager.setAdapter(viewPagerAdapter);
        //将前面创建的 viewPagerAdapter 适配器设置给 viewPager 视图组件，以便在 ViewPager 中显示相应的页面。
        //底部导航栏监听事件
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId() == R.id.item_home){
                    viewPager.setCurrentItem(0,false);
                    // 将 ViewPager 的当前页面显示成索引为 0 的页面
                    return true;
                }
                else if(item.getItemId() == R.id.item_more){
                    viewPager.setCurrentItem(1);
                    return true;
                }
                else if(item.getItemId() == R.id.item_user){
                    viewPager.setCurrentItem(2);
                    return true;
                }
                return true;
            }

        });

        // 添加页面切换的监听器，根据页面切换实现菜单切换
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){  // 根据页面位置更新导航栏的选中状态
                    case 0:
                        navigationView.setSelectedItemId(R.id.item_home);
                        //将导航栏中的选中项设置为 R.id.item_home
                        break;
                    case 1:
                        navigationView.setSelectedItemId(R.id.item_more);
                        break;
                    case 2:
                        navigationView.setSelectedItemId(R.id.item_user);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });//通过使用页面切换监听器，
        // 我们可以根据页面切换的情况来更改导航栏的选中状态，
        // 从而实现页面切换时导航栏菜单的同步切换效果。

    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            SharedPreferences Mylocation = getSharedPreferences("manual_location", MODE_PRIVATE);
                isManualLocation = Mylocation.getBoolean("manual",false);
            if(!isManualLocation){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }else {
                latitude =Mylocation.getFloat("Latitude",0);
                longitude= Mylocation.getFloat("Longitude",0);
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            dataString = formatter.format(date);

            SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putFloat("Latitude",(float)latitude);
            editor.putFloat("Longitude",(float)longitude);
            editor.putString("Time",dataString);
            editor.commit();

            bkgdThread = saveLocation();
            if(trackId!=null){
                String[] track = trackId.split(":",3);
                if(track.length>2){
                    trackId = track[2];
                }
                if(trackId!=previousTrackId){
                    bkgdThread.start();

                }
            }

        }
    };


    public Thread saveLocation(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(getString(R.string.BACKEND_URL) + "userHistory?spotify_userId="+user.getId());
//                    ?userId=123344&latitude=2232.33
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                    JSONObject jsonInput = new JSONObject();
                    String[] track = trackId.split(":",3);
                    if(track.length>2){
                        trackId = track[2];

                    }
                    previousTrackId = trackId;
                    jsonInput.put("userid", user.getId());
                    jsonInput.put("spotifyTrackId", trackId);
                    jsonInput.put("latitude", latitude);
                    jsonInput.put("longitude", longitude);
                    if(dataString==null){
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = new Date();
                        dataString = formatter.format(date);
                    }
                    jsonInput.put("timestamp",dataString);
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInput.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                bkgdThread = null;
            }

        });



    }

    protected BroadcastReceiver bcr = new MySpotifyReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
         if (action.equals("com.spotify.music.metadatachanged")) {
                trackId = intent.getStringExtra("id");
                getLocation();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bcr);
    }


    void regis(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.spotify.music.metadatachanged");
        registerReceiver(bcr, filter);
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
        if(id == R.id.toMap){
            Intent intent = new Intent(HomepageActivity.this,GeoJsonDemoActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
