package models;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class GameCollection {

	private String id;
    private String name;
    private Color color;
    private String shelfId;
    private List<String> gameIds = new ArrayList<>();
    private ObservableList<Game> games = FXCollections.observableArrayList();
    
    public GameCollection() { 
    	
    }
    
    @Override
    public String toString() {
    	return name;
    }

    public GameCollection(String name, Color color, String shelfId) {
        this.name = name;
        this.color = color;
        this.shelfId = shelfId;
    }

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getShelfId() {
		return shelfId;
	}

	public void setShelfId(String shelfId) {
		this.shelfId = shelfId;
	}

	public List<String> getGameIds() {
		return gameIds;
	}

	public void setGameIds(List<String> gameIds) {
		this.gameIds = gameIds;
	}

	public ObservableList<Game> getGames() {
		return games;
	}

	public void setGames(ObservableList<Game> games) {
		this.games = games;
	}
    

 
}
