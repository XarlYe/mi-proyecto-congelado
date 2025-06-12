package models;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Wishlist {

	private String id = "global_whishlist";
	private List<String> gameIds = new ArrayList<>();
	private final ObservableList<Game> games = FXCollections.observableArrayList();

	public Wishlist() {

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getGameIds() {
		return gameIds;
	}

	public void setGameIds(List<String> ids) {
		this.gameIds = ids != null ? ids : new ArrayList<>();
	}

	public ObservableList<Game> getGames() {
		return games;
	}

}
