package nus.iss.spotifyteam1.Fragment;

import java.util.Date;

public class Playlist {
    private String name;
    private String description;
    private Date timestamp;
    private int imageResId;

    private int playlistID;


    public Playlist(int playlistID) {

        this.playlistID = playlistID;

    }

    public String getName() {
        return name;
    }

    public int getPlaylistID() {
        return playlistID;
    }

    public void setPlaylistID(int playlistID) {
        this.playlistID = playlistID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}


