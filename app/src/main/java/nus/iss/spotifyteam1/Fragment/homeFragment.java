package nus.iss.spotifyteam1.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import nus.iss.spotifyteam1.R;
import nus.iss.spotifyteam1.User;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
    private ListView listView;

    private BottomNavigationView navigationView;

    private ViewPager viewPager;
    private List<Playlist> playlists = new ArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String userid;

    private static final String BASE_URL = "https://api.spotify.com/";

    Button generateButton;

    Thread bkgdThread;

    String TOKEN;

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
        playlists.add(new Playlist("Playlist1", 114510, "8.5.2023", R.drawable.playlist_image1));
        playlists.add(new Playlist("Playlist2", 321654, "8.2.2023", R.drawable.playlist_image2));
        playlists.add(new Playlist("Playlist3", 1234556, "28.7.2023", R.drawable.playlist_image3));
        //PUSH TEST test
        //在这里用API抓取所有的Playlist，重点是名字、ID和时间，时间在这里用了String的格式，到时候可以更换......
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);

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

                startActivity(intent);
            }
        });

        generateButton = view.findViewById(R.id.generateButton);
        generateButton.setOnClickListener(this);

        return view;
        //listView = listView.findViewById(R.id.playlistListView);
        //PlaylistAdapter adapter = new PlaylistAdapter(getContext(), playlists);
        //listView.setAdapter(adapter);
        //return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.generateButton) {
            SharedPreferences pref = requireActivity().getSharedPreferences("user_obj", Context.MODE_PRIVATE);
            TOKEN = pref.getString("user_TOKEN", "");
            userid = pref.getString("user_id","");
            bkgdThread = generatePlaylist();
            bkgdThread.start();
        }
    }


    public Thread generatePlaylist() {

        return new Thread(new Runnable() {
            @Override
            public void run() {
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
                        jsonInput.put("name", "mynewList");
                        jsonInput.put("description", "dynamic generated from Team1 ");
                        jsonInput.put("public", true);
                        try (OutputStream os = connection.getOutputStream()) {
                            byte[] input = jsonInput.toString().getBytes("utf-8");
                            os.write(input, 0, input.length);
                        }

                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_CREATED) {
                            Toast.makeText(getContext(),  "Generated", Toast.LENGTH_LONG).show();//for test
                        }
                    } catch (IOException e) {
                        e.printStackTrace();

                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    };
                    bkgdThread = null;
                }catch (Exception e){
                    bkgdThread= null;
                    e.printStackTrace();
                }
            }
        });
    }


    private void handleResponseData(String responseData) {
        /*
        Gson gson = new Gson();
        User user = gson.fromJson(responseData, User.class);
        SharedPreferences pref = getSharedPreferences("user_obj", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id",user.getId());
        editor.putString("user_name",user.getDisplayName());
        editor.putString("user_email",user.getCountry());
        editor.commit();
        user.getId();
        user.getEmail();
        user.getId();
         */
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