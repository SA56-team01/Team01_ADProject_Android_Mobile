package nus.iss.spotifyteam1.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import nus.iss.spotifyteam1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link homeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class homeFragment extends Fragment {

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

    public homeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment homeFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        playlists.add(new Playlist("Playlist3", 123456, "28.7.2023", R.drawable.playlist_image3));
        //在这里用API抓取所有的Playlist，重点是名字、ID和时间，时间在这里用了String的格式，到时候可以更换
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

        return view;
        //listView = listView.findViewById(R.id.playlistListView);
        //PlaylistAdapter adapter = new PlaylistAdapter(getContext(), playlists);
        //listView.setAdapter(adapter);
        //return inflater.inflate(R.layout.fragment_home, container, false);
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