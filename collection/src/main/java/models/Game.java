package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Game {
	private int id;
	private String name;
	private List<Integer> platformIds = new ArrayList<>();
	private List<String> platforms = new ArrayList<>();
	private LocalDate releaseDate;
	private double rating;
	private String coverId;
	private String summary;
	private List<Integer> genreIds = new ArrayList<>();
	private List<String> genres    = new ArrayList<>();
	private List<String> screenshotIds = new ArrayList<>();
	private List<Integer> dlcIds = new ArrayList<>();
	private String url;

	public Game() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String title) {
		this.name = title;
	}

	public List<Integer> getPlatformIds() {
		return platformIds;
	}

	public void setPlatformIds(List<Integer> platformIds) {
	    this.platformIds = (platformIds != null) ? platformIds : new ArrayList<>();
	}

	public LocalDate getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getCoverId() {
		return coverId;
	}

	public void setCoverId(String cover) {
		this.coverId = cover;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<String> getScreenshotIds() {
		return screenshotIds;
	}

	public void setScreenshotIds(List<String> screenshots) {
		this.screenshotIds = (screenshotIds != null) ? screenshotIds : new ArrayList<>();
	}

	public List<Integer> getDlcIds() {
		return dlcIds;
	}

	public void setDlcIds(List<Integer> dlcs) {
		this.dlcIds = dlcs;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Integer> getGenreIds() {
		return genreIds;
	}

	public void setGenreIds(List<Integer> genres) {
		this.genreIds = (genreIds != null) ? genreIds : new ArrayList<>();
	}

	public List<String> getPlatforms() {
		return platforms;
	}

	public void setPlatforms(List<String> platforms) {
		this.platforms = platforms;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}
}