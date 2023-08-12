package nus.iss.spotifyteam1;

import com.google.gson.annotations.SerializedName;

public class User {
    private String country;
    @SerializedName("display_name")
    private String displayName;
    private String email;
    @SerializedName("explicit_content")
    private ExplicitContent explicitContent;
    @SerializedName("external_urls")
    private ExternalUrls externalUrls;
    private Followers followers;
    private String href;
    private String id;
    private Image[] images;
    private String product;
    private String type;
    private String uri;

    // Add getters and setters for the fields


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ExplicitContent getExplicitContent() {
        return explicitContent;
    }

    public void setExplicitContent(ExplicitContent explicitContent) {
        this.explicitContent = explicitContent;
    }

    public ExternalUrls getExternalUrls() {
        return externalUrls;
    }

    public void setExternalUrls(ExternalUrls externalUrls) {
        this.externalUrls = externalUrls;
    }

    public Followers getFollowers() {
        return followers;
    }

    public void setFollowers(Followers followers) {
        this.followers = followers;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public static class ExplicitContent {
        @SerializedName("filter_enabled")
        private boolean filterEnabled;
        @SerializedName("filter_locked")
        private boolean filterLocked;

        // Add getters and setters

        public boolean isFilterEnabled() {
            return filterEnabled;
        }

        public void setFilterEnabled(boolean filterEnabled) {
            this.filterEnabled = filterEnabled;
        }

        public boolean isFilterLocked() {
            return filterLocked;
        }

        public void setFilterLocked(boolean filterLocked) {
            this.filterLocked = filterLocked;
        }
    }

    public static class ExternalUrls {
        private String spotify;

        // Add getters and setters

        public String getSpotify() {
            return spotify;
        }

        public void setSpotify(String spotify) {
            this.spotify = spotify;
        }
    }

    public static class Followers {
        private String href;
        private int total;

        // Add getters and setters

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class Image {
        private String url;
        private int height;
        private int width;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
        // Add getters and setters
    }
}