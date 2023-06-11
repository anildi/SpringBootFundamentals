package ttl.larku.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.Duration;

@Entity
public class Track {
	public enum Format
	{
		CD,
		OGG,
		MP3
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String title;
	private String artist;
	private String album;
	private String duration;
	private String date;

	@Enumerated(EnumType.STRING)
	private Format format;

	public Track() {
		super();
	}

	public Track(String title, String artist, String album, String durStr, String dateStr, Format format) {
		init(title, artist, album, durStr, dateStr, format);
	}

	public Track(String title, String artist, String album, String durStr, String dateStr) {
	    init(title, artist, album, durStr, dateStr, null);
	}

	public void init(String title, String artist, String album, String duration, String date, Format format) {
		this.title = title;
		this.artist = artist;
		this.album = album;
		this.date = date;
		this.duration = duration;
		this.format = format;
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

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	@Override
	public String toString() {
		return "Track{" +
				"id=" + id +
				", title='" + title + '\'' +
				", artist='" + artist + '\'' +
				", album='" + album + '\'' +
				", duration='" + duration + '\'' +
				", date='" + date + '\'' +
				", format=" + format +
				'}';
	}

	public static Duration hmsToDuration(String hms) {
		String sp [] = hms.split(":");
		Duration duration = Duration.ofHours(Integer.valueOf(sp[0]))
				.plusMinutes(Integer.valueOf(sp[1]));
		if(sp.length == 3) {
			duration = duration.plusSeconds(Integer.valueOf(sp[2]));
		}
		return duration;
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

	public static Builder format(Format arg) {
		return new Builder().format(arg);
	}

	public static Builder format(String arg) {
		return new Builder().format(arg);
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
		private Format format;
		
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

		public Builder format(String formatStr) {
			return format(Format.valueOf(formatStr));
		}

		public Builder format(Format format) {
			this.format = format;
			return this;
		}

		public Track build() {
			Track t = new Track(title, artist, album, duration, date, format);
			return t;
		}

	}

}
