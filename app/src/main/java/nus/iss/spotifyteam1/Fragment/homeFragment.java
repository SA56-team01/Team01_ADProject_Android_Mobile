package nus.iss.spotifyteam1.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import nus.iss.spotifyteam1.HomepageActivity;
import nus.iss.spotifyteam1.LoginActivity;
import nus.iss.spotifyteam1.MusicActivity;
import nus.iss.spotifyteam1.Musiclist;
import nus.iss.spotifyteam1.R;
import nus.iss.spotifyteam1.User;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    String userId;
    private ListView listView;

    Thread loadPlayList;

    Boolean isManualLocation = false;


    private BottomNavigationView navigationView;

    private ViewPager viewPager;
    private List<Playlist> playlists = new ArrayList<>();

    String userid;
    String playListId;

    String dataString;

    String playlist_description;
    String res;
    String playlistnameT;

    float lat;
    float longi;

    String username;
    View view;
    String[] uris;
    String playListStoreToDB;

    private static final String BASE_URL = "https://api.spotify.com/";
    Button generateButton;

    Thread bkgdThread;

    String TOKEN;

    JSONObject MLResponse;

    private Handler handler = new Handler();

    public homeFragment() {
        // Required empty public constructor
    }

    public static homeFragment newInstance(String param1, String param2) {
        homeFragment fragment = new homeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        dataString = formatter.format(date);

        playlist_description = "dynamic generated from Team1 - "+dataString;
        loadPlayList = getAllList();
        loadPlayList.start();
        SharedPreferences user = requireActivity().getSharedPreferences("user_obj", Context.MODE_PRIVATE);
        TOKEN = user.getString("user_TOKEN", "");
        userid = user.getString("user_id","");
        username = user.getString("user_name","");
//        playlists.add(new Playlist("Playlist1", 114510, "8.5.2023", R.drawable.playlist_image1));
//        playlists.add(new Playlist("Playlist2", 321654, "8.2.2023", R.drawable.playlist_image2));
//        playlists.add(new Playlist("Playlist3", 1234556, "28.7.2023", R.drawable.playlist_image3));
        //PUSH TEST test
        //在这里用API抓取所有的Playlist，重点是名字、ID和时间，时间在这里用了String的格式，到时候可以更换......

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = view.findViewById(R.id.playlistListView);
        PlaylistAdapter adapter = new PlaylistAdapter(getContext(),playlists);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Playlist clickedPlaylist = playlists.get(position);

                Intent intent = new Intent(getContext(), MusicActivity.class);
                intent.putExtra("name", clickedPlaylist.getName());
                intent.putExtra("description", clickedPlaylist.getDescription());
                intent.putExtra("playlistId", clickedPlaylist.getPlaylistID());
                intent.putExtra("timestamp", clickedPlaylist.getTimestamp().toString());
                intent.putExtra("spotifyPlaylistId",clickedPlaylist.getSpotifyPlaylistId());

                startActivity(intent);
            }
        });

        generateButton = view.findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);
//



        return view;
        //listView = listView.findViewById(R.id.playlistListView);
        //PlaylistAdapter adapter = new PlaylistAdapter(getContext(), playlists);
        //listView.setAdapter(adapter);
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.generateButton) {
            SharedPreferences location =requireActivity().getSharedPreferences("manual_location",Context.MODE_PRIVATE);
            isManualLocation = location.getBoolean("manual",false);
            if(isManualLocation){
                lat =location.getFloat("Latitude",0);
                longi= location.getFloat("Longitude",0);

            }
            else {
                SharedPreferences pref = requireActivity().getSharedPreferences("location", Context.MODE_PRIVATE);
                lat = pref.getFloat("Latitude",0);
                longi = pref.getFloat("Longitude",0);
            }
            bkgdThread = generatePlaylist();
            bkgdThread.start();
        }
    }

    void topMusic(){

        try {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(BASE_URL + "v1/me/top/tracks?limit=5");
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
                        res = response.toString();
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }catch (Exception e){
            bkgdThread= null;
            e.printStackTrace();
        }
    }
    void sendResponseToML(){
        //Call ML API
        try {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(getString(R.string.ML_URL)+"predictTrackAttributes?userId=1&latitude="+lat+"&longitude="+longi+"&time="+dataString);
//                URL url = new URL("http://10.249.248.198:5001/predictTrackAttributes?userId=1&latitude=1.03&longitude=103.1&time=2023-08-15%2000:00:00");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("top_tracks", res);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
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
                        JSONArray playlistSongsArray = jsonObject.getJSONArray("playlist_songs");
                        MLResponse = jsonObject;
                        uris = new String[playlistSongsArray.length()];

                        for (int i = 0; i < playlistSongsArray.length(); i++) {
                            uris[i] = playlistSongsArray.getString(i);
                        }
                        playListStoreToDB = response.toString();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }catch (Exception e){
            bkgdThread= null;
            e.printStackTrace();
        }
    }

    void createEmptyPlayList(){
        try {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(BASE_URL + "v1/users/"+userid+"/playlists");

                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
                connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                JSONObject jsonInput = new JSONObject();
                playlistnameT = username+" - "+dataString;
                jsonInput.put("name", playlistnameT);

                jsonInput.put("description", playlist_description);
                jsonInput.put("public", true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if(response.toString()!=null){
                        JSONObject jsonObject = new JSONObject(response.toString());
                        playListId = jsonObject.getString("id");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }catch (Exception e){
            bkgdThread= null;
            e.printStackTrace();
        }

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
                                    String timestampString = jsonObject.getString("timestamp");
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    Date time = dateFormat.parse(timestampString);
                                    Playlist playlist = new Playlist(playListID);
                                    playlist.setName(name);
                                    playlist.setTimestamp(time);
                                    playlist.setImageResId(R.drawable.default_playlist_image);
                                    playlist.setSpotifyPlaylistId(spotifyPlaylistId);
                                    playlist.setDescription(playlist_description);
                                    playlists.add(playlist);
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // Update your UI components here
                                                // For example, update a TextView, RecyclerView, etc.
                                                listView = view.findViewById(R.id.playlistListView);
                                                PlaylistAdapter adapter = new PlaylistAdapter(getContext(),playlists);
                                                listView.setAdapter(adapter);

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
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

    boolean addItemToPlayList(){
        try {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(BASE_URL + "v1/playlists/"+playListId+"/tracks");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", "Bearer " + TOKEN);
                connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                JSONObject jsonInput = new JSONObject();
                JSONArray urisArray = new JSONArray(uris);
                jsonInput.put("uris",urisArray);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
               String msg =  connection.getResponseMessage();
                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }catch (Exception e){
            bkgdThread= null;
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void savePlaylistToDB(){
        try {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(getString(R.string.BACKEND_URL) + "playlists/store?spotify_userId="+userid);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json"); // Set content type if needed
                JSONObject jsonInput = new JSONObject();
                jsonInput.put("playlistName", playlistnameT);
                jsonInput.put("spotifyPlaylistId", playListId);
                jsonInput.put("timestamp", dataString);
                jsonInput.put("longitude", lat);
                jsonInput.put("latitude", longi);
                jsonInput.put("targetAcousticness",MLResponse.getString("target_acousticness"));
                jsonInput.put("targetDanceability",MLResponse.getString("target_danceability"));
                jsonInput.put("targetEnergy",MLResponse.getString("target_energy"));
                jsonInput.put("targetLiveness",MLResponse.getString("target_liveness"));
                jsonInput.put("targetLoudenes",MLResponse.getString("target_loudness"));
                jsonInput.put("targetSpeechiness",MLResponse.getString("target_speechiness"));
                jsonInput.put("targetTempo",MLResponse.getString("target_tempo"));
                jsonInput.put("targetValence",MLResponse.getString("target_valence"));
                JSONArray urisArray = new JSONArray();
                for (String track:uris) {
                    JSONObject listSong = new JSONObject();
                    listSong.put("trackId",track);
                    urisArray.put(listSong);
                }
                jsonInput.put("playlistSongs", urisArray);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInput.toString().getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Intent intent = new Intent(requireActivity(),HomepageActivity.class);
                    startActivity(intent);

                }

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }catch (Exception e){
            bkgdThread= null;
            e.printStackTrace();
        }
    }

    public Thread generatePlaylist() {

        //call get_top_api from spotify
        //send response to ML
        //get response from ML ( 20 music track id)
        //create empty playlist in Spotify
        //add item(20 track id) to playlist
        //Save playlist etc to DB

        return new Thread(new Runnable() {
            @Override
            public void run() {
                topMusic();
                sendResponseToML();
                createEmptyPlayList();
                if(addItemToPlayList()){
                    savePlaylistToDB();
                }


            }
        });
    }

    private class PlaylistAdapter extends ArrayAdapter<Playlist> {
        public PlaylistAdapter(Context context, List<Playlist> playlists) {
            super(context, 0, playlists);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.playlist_item, parent, false);
            }

            Playlist playlist = getItem(position);

            ImageView imageView = convertView.findViewById(R.id.playlistImageView);
            imageView.setImageResource(playlist.getImageResId());

            TextView nameTextView = convertView.findViewById(R.id.playlistNameTextView);
            nameTextView.setText(playlist.getName());

            TextView descriptionTextView = convertView.findViewById(R.id.playlistDescriptionTextView);
            descriptionTextView.setText(playlist.getDescription());

            return convertView;
        }
    }

}