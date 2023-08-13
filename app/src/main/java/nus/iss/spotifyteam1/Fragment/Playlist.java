package nus.iss.spotifyteam1.Fragment;

public class Playlist {
    private String name;
    private String description;
    private int imageResId;

    private int playlistID;


    public Playlist(String name, int playlistID, String description, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
        this.playlistID = playlistID;
        this.description = description;

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
}


