package nus.iss.spotifyteam1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MySpotifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ("com.spotify.music.active".equals(intent.getAction())) {
            Toast.makeText(context,"Music started",Toast.LENGTH_SHORT).show();
        }
        throw new UnsupportedOperationException("Not yet implemented");
    }
}