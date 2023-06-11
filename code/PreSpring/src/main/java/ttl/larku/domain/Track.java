package ttl.larku.domain;

public class Track {

    private int id;
    private String title;
    private String artist;
    private String album;
    private String duration;
    private String date;

    public Track() {
        super();
    }

    public Track(String title, String artist, String album, String duration, String date) {
        super();
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public String toString() {
        return "Track [id=" + id + ", title=" + title + ", artist=" + artist + ", album=" + album + ", duration="
                + duration + ", date=" + date + "]";
    }


    public static Builder id(int arg) {
        return new Builder().id(arg);
    }

    public static Builder title(String arg) {
        return new Builder().title(arg);
    }

    public static Builder album(String arg) {
        return new Builder().album(arg);
    }

    public static Builder artist(String arg) {
        return new Builder().artist(arg);
    }

    public static Builder duration(String arg) {
        return new Builder().duration(arg);
    }

    public static Builder date(String arg) {
        return new Builder().date(arg);
    }

    /**
     * Make us a Builder
     */
    public static class Builder {
        private int id;
        private String title;
        private String artist;
        private String album;
        private String duration;
        private String date;

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder artist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder album(String album) {
            this.album = album;
            return this;
        }

        public Builder duration(String duration) {
            this.duration = duration;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Track build() {
            Track t = new Track(title, artist, album, duration, date);
            return t;
        }

    }

}
