package nus.iss.spotifyteam1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class MySpotifyReceiver extends BroadcastReceiver {
    public abstract void onReceive(Context context, Intent intent);

}