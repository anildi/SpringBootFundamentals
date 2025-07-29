package ttl.larku.domain;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

public class Track {

	private int id;
	private String title;
	private List<String> artists;
	private String album;
	private Duration duration;
	private LocalDate date;

	public Track() {
		super();
	}

	public Track(String title, List<String> artists, String album, String durStr, String dateStr) {
		Duration duration = hmsToDuration(durStr);
		LocalDate date = LocalDate.parse(dateStr);

		init(title, artists, album, duration, date);
	}

	public Track(String title, List<String> artists, String album, Duration duration, LocalDate date) {
		init(title, artists, album, duration, date);
	}

	public void init(String title, List<String> artists, String album, Duration duration, LocalDate date) {
		this.title = title;
		this.artists = artists;
		this.album = album;
		this.date = date;
		this.duration = duration;
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

	public List<String> getArtists() {
		return artists;
	}

	public void setArtists(List<String> artists) {
		this.artists = artists;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public Duration getDuration() {
		return duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
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

	@Override
	public String toString() {
		return "Track [id=" + id + ", title=" + title + ", artists=" + artists + ", album=" + album + ", duration="
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
	public static Builder artist(List<String> arg) {
		return new Builder().artists(arg);
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
		private List<String> artists;
		private String album;
		private Duration duration;
		private LocalDate date;
		
		public Builder id(int id) {
			this.id = id;
			return this;
		}

		public Builder title(String title) {
			this.title = title;
			return this;
		}

		public Builder artists(List<String> artists) {
			this.artists = artists;
			return this;
		}

		public Builder album(String album) {
			this.album = album;
			return this;
		}

		public Builder duration(String duration) {
			this.duration = hmsToDuration(duration);
			return this;
		}

		public Builder duration(Duration duration) {
			this.duration = duration;
			return this;
		}

		public Builder date(String dateStr) {
			this.date = LocalDate.parse(dateStr);
			return this;
		}

		public Builder date(LocalDate date) {
			this.date = date;
			return this;
		}

		public Track build() {
			Track t = new Track(title, artists, album, duration, date);
			return t;
		}

	}

}
