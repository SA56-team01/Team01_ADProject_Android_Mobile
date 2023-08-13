package nus.iss.spotifyteam1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    private ListView listView;
    private BottomNavigationView navigationView;
    private ViewPager viewPager;
    private List<Musiclist> musiclists = new ArrayList<>();
    private String playlistName;
    private String playlistDescription;
    private int playlistId;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);


        // Add some example playlists
        musiclists.add(new Musiclist("Music1", "8.5.2023", R.drawable.playlist_image1));
        musiclists.add(new Musiclist("Music2", "8.2.2023", R.drawable.playlist_image2));
        musiclists.add(new Musiclist("Music3", "28.7.2023", R.drawable.playlist_image3));


        //设置playlist介绍
        if (getIntent() != null) {
            playlistName = getIntent().getStringExtra("name");
            playlistDescription = getIntent().getStringExtra("description");
            playlistId = getIntent().getIntExtra("playlistId", -1);
        }

        TextView nameTextView = findViewById(R.id.playlistNameTextView);  // Assuming you have a TextView with this ID for the name
        nameTextView.setText(playlistName);

        TextView descriptionTextView = findViewById(R.id.playlistDescriptionTextView);  // Similarly, a TextView for the description
        descriptionTextView.setText(playlistDescription);

        ImageView imageView = findViewById(R.id.albumCoverImageView);
        imageView.setImageResource(R.drawable.playlist_image3);



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

            //ImageView imageView = convertView.findViewById(R.id.playlistImageView);
            //imageView.setImageResource(playlist.getImageResId());

            TextView nameTextView = convertView.findViewById(R.id.musicNameTextView);
            nameTextView.setText(playlist.getName());

            TextView descriptionTextView = convertView.findViewById(R.id.musicDescriptionTextView);
            descriptionTextView.setText(playlist.getDescription());

            return convertView;
        }
    }
}