package org.openjfx.collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import management.DatabaseManager;
import models.Game;
import models.GameCollection;
import models.Profile;
import models.Shelf;
import models.Wishlist;

public class AppState {

	private static ObservableList<Shelf> shelves = FXCollections.observableArrayList();
	private static Profile userProfile = new Profile();
	private static Wishlist wishlist;
    private static ObservableList<Game> wishlistView = FXCollections.observableArrayList();
	private static GameCollection selectedCollection;
	private static Game selectedGame;
	private static int selectedGameId;

	private static String previousView;
	

	public static String getPreviousView() {
		return previousView;
	}

	public static void setPreviousView(String viewName) {
		previousView = viewName;
	}

	public static GameCollection getSelectedCollection() {
		return selectedCollection;
	}

	public static void setSelectedCollection(GameCollection coll) {
		selectedCollection = coll;
	}

	public static Game getSelectedGame() {
		return selectedGame;
	}

	public static void setSelectedGame(Game game) {
		selectedGame = game;
	}

	public static void setSelectedGameId(int id) {
		selectedGameId = id;
	}

	public static int getSelectedGameId() {
		return selectedGameId;
	}

	public static ObservableList<Shelf> getShelves() {
		return shelves;
	}

	public static void setShelves(ObservableList<Shelf> newShelves) {
		shelves = newShelves;
	}

	public static void addShelf(Shelf shelf) {
	    Shelf persisted = DatabaseManager.createShelf(shelf);
	    shelves.add(persisted);
	}

	public static void removeShelf(Shelf shelf) {
	    DatabaseManager.deleteShelf(shelf);
	    shelves.remove(shelf);
	}

	public static Profile getUserProfile() {
		return userProfile;
	}

	public static void setUserProfile(Profile profile) {
		userProfile = profile;
	}
	
	public static void setup() {
	    wishlist = DatabaseManager.loadWishlist();
	}
	
    public static ObservableList<Game> getWishlist() {
        return wishlistView;
    }
    
    public static void startWishlist() {
        wishlist = DatabaseManager.loadWishlist();
        wishlistView.clear();
        for (String gid : wishlist.getGameIds()) {
            Game g = App.getApiService().getGameDetails(Integer.parseInt(gid));
            if (g != null) {
                wishlistView.add(g);
            }
        }
    }

    public static void addToWishlist(Game g) {
        if (!wishlistView.contains(g)) {
            wishlistView.add(g);
            wishlist.getGameIds().add(String.valueOf(g.getId()));
            DatabaseManager.updateWishlist(wishlist);
        }
    }

    public static void removeFromWishlist(Game g) {
        if (wishlistView.remove(g)) {
            wishlist.getGameIds().remove(String.valueOf(g.getId()));
            DatabaseManager.updateWishlist(wishlist);
        }
    }

}
