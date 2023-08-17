package nus.iss.spotifyteam1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nus.iss.spotifyteam1.Fragment.Playlist;

public class MusicActivity extends AppCompatActivity {
    private ListView listView;
    private BottomNavigationView navigationView;
    private ViewPager viewPager;
    private List<Musiclist> musiclists = new ArrayList<>();

    private static final String BASE_URL = "https://api.spotify.com/";
    private String playlistName;

    Thread getTracksInfo;
    String timeString;

    Button openSpotifyButton;

    String TOKEN;
    private String playlistDescription;
    private int playlistId;
    String spotifyPlaylistId;




    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        // Add some example playlists
//        musiclists.add(new Musiclist("Music1", "8.5.2023", R.drawable.playlist_image1));
//        musiclists.add(new Musiclist("Music2", "8.2.2023", R.drawable.playlist_image2));
//        musiclists.add(new Musiclist("Music3", "28.7.2023", R.drawable.playlist_image3));

        SharedPreferences user = getSharedPreferences("user_obj", Context.MODE_PRIVATE);
        TOKEN = user.getString("user_TOKEN", "");
        //设置playlist介绍
        if (getIntent() != null) {
            playlistName = getIntent().getStringExtra("name");
            playlistDescription = getIntent().getStringExtra("description");
            playlistId = getIntent().getIntExtra("playlistId", -1);
            spotifyPlaylistId = getIntent().getStringExtra("spotifyPlaylistId");
            timeString = getIntent().getStringExtra("timestamp");
            getTracksInfo = getSongsAttr();
            getTracksInfo.start();
        }

        TextView nameTextView = findViewById(R.id.playlistNameTextView);  // Assuming you have a TextView with this ID for the name
        nameTextView.setText(playlistName);

        TextView descriptionTextView = findViewById(R.id.playlistDescriptionTextView);  // Similarly, a TextView for the description
        descriptionTextView.setText(playlistDescription);

        ImageView imageView = findViewById(R.id.albumCoverImageView);
        imageView.setImageResource(R.drawable.default_playlist_image);



        //设置下面的playlist
        listView = findViewById(R.id.musiclistListView);
        MusiclistAdapter adapter = new MusiclistAdapter(musiclists);
        listView.setAdapter(adapter);

        //返回键
        ImageView backImageView = findViewById(R.id.backImageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // This will close the current activity and return to the previous one
            }
        });


        openSpotifyButton = findViewById(R.id.openSpotifyButton);
        openSpotifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent spotifyIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("spotify:playlist:"+spotifyPlaylistId));
                startActivity(spotifyIntent);
            }
        });

    }

    private class MusiclistAdapter extends ArrayAdapter<Musiclist> {
        public MusiclistAdapter(List<Musiclist> musiclists) {
            super(MusicActivity.this, 0, musiclists);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.musiclist_item, parent, false);
            }

            Musiclist playlist = getItem(position);

//            ImageView imageView = convertView.findViewById(R.id.playlistImageView);
//            imageView.setImageResource(playlist.getImageResId());

            TextView nameTextView = convertView.findViewById(R.id.musicNameTextView);
            nameTextView.setText(playlist.getName());

            TextView countTextView = convertView.findViewById(R.id.numberTextView);
            countTextView.setText(position+1+" ");

            TextView descriptionTextView = convertView.findViewById(R.id.musicDescriptionTextView);
            descriptionTextView.setText(playlist.getArtist());

            return convertView;
        }
    }

    public Thread getSongsAttr(){
        return new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = null;
                    BufferedReader reader = null;
                    try {
                        URL url = new URL(BASE_URL + "v1/playlists/"+spotifyPlaylistId);
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
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

                                JSONObject jsonObject = new JSONObject(response.toString());
//                                jsonObject.getString()
                                 musiclists = parseMusiclistArray(jsonObject);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            listView = findViewById(R.id.musiclistListView);
                                            MusiclistAdapter adapter = new MusiclistAdapter(musiclists);
                                            listView.setAdapter(adapter);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                for (Musiclist musiclist : musiclists) {
                                    System.out.println("Name: " + musiclist.getName());
                                    System.out.println("Description: " + musiclist.getDescription());
                                    System.out.println("Artist: " + musiclist.getArtist());
                                    System.out.println("Image URL: " + musiclist.getImageResId());
                                    System.out.println("Track ID: " + musiclist.getTrackID());
                                    System.out.println();
                                }


                            };
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        getTracksInfo=null;

                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                        getTracksInfo=null;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    getTracksInfo=null;

                }
            }
        });

    }
    public static List<Musiclist> parseMusiclistArray(JSONObject json) throws JSONException {
        List<Musiclist> musiclistList = new ArrayList<>();

        JSONArray items = json.getJSONObject("tracks").getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject itemObject = items.getJSONObject(i);
            JSONObject trackObject = itemObject.getJSONObject("track");

            Musiclist musiclist = new Musiclist();
            musiclist.setName(trackObject.getString("name"));

            musiclist.setDescription(json.getString("description"));

            JSONArray artists = trackObject.getJSONArray("artists");
            if (artists.length() > 0) {
                JSONObject artistObject = artists.getJSONObject(0);
                musiclist.setArtist(artistObject.getString("name"));
            }

            musiclist.setTrackID(trackObject.getString("id"));

            musiclistList.add(musiclist);
        }

        return musiclistList;
    }
}