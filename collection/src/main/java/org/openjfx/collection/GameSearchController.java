package org.openjfx.collection;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import models.Game;
import models.Profile;
import services.APIService;

public class GameSearchController {
	@FXML
	private HBox topBar;
	@FXML
	private HBox footerBar;
	@FXML
	private VBox scrollGamesContainer;
	@FXML
	private Label versionLabel;

	@FXML
	private TableView<Game> tvTableView;
	@FXML
	private TableColumn<Game, String> nameColumn;
	@FXML
	private TableColumn<Game, String> genresColumn;
	@FXML
	private TableColumn<Game, String> platformsColumn;
	@FXML
	private TableColumn<Game, String> releaseColumn;
	@FXML
	private TableColumn<Game, Double> ratingColumn;

	@FXML
	private TextField txtSearch;

	@FXML
	private MenuButton viewMenu;
	@FXML
	private MenuItem btnHome;
	@FXML
	private MenuItem btnWishlist;
	@FXML
	private MenuItem btnSearchGames;

	@FXML
	private MenuButton userMenu;
	@FXML
	private MenuItem btnEditProfile;

	@FXML
	private ImageView imgAvatar;
	@FXML
	private Label lblUsername;

	@FXML
	private Button btnResetFilters;

	@FXML
	private RadioMenuItem radioItemAscending;
	@FXML
	private RadioMenuItem radioItemDescending;
	@FXML
	private RadioMenuItem radioItemAlphabetically;
	@FXML
	private RadioMenuItem radioItemRating;
	@FXML
	private RadioMenuItem radioItemReleaseDate;

	private final APIService apiService = App.getApiService();

	// ------------------------------

	@FXML
	public void initialize() {

		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		genresColumn.setCellValueFactory(
				cell -> Bindings.createStringBinding(() -> String.join(", ", cell.getValue().getGenres())));

		platformsColumn.setCellValueFactory(
				cell -> Bindings.createStringBinding(() -> String.join(", ", cell.getValue().getPlatforms())));

		releaseColumn.setCellValueFactory(cell -> Bindings.createStringBinding(() -> {
			if (cell.getValue().getReleaseDate() == null)
				return "";
			return cell.getValue().getReleaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}));

		ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
		ratingColumn.setCellFactory(column -> new TableCell<Game, Double>() {
		    @Override
		    protected void updateItem(Double value, boolean empty) {
		        super.updateItem(value, empty);
		        if (empty || value == null) {
		            setText(null);
		        } else {
		            setText(String.format("%.1f", value));
		        }
		    }
		});
		
		tvTableView.setRowFactory(tv -> {
		    TableRow<Game> row = new TableRow<>();
		    row.setOnMouseClicked(evt -> {
		        if (! row.isEmpty() && evt.getClickCount() == 2) {
		            Game selected = row.getItem();
		            openGameDetail(selected.getId());
		        }
		    });
		    return row;
		});
		
		txtSearch.setOnAction(e -> {
			String q = txtSearch.getText().trim();
			if (!q.isEmpty()) {
				performSearch(q);
			}
		});
		
		new Thread(() -> {
			String body = "fields id,name,platforms.abbreviation,first_release_date,rating,genres.name;" + " sort rating desc;"
					+ " limit 50;";
			List<Game> games;
			try {
				games = apiService.parseGames(apiService.executeQuery("games", body));
			} catch (Exception e) {
				e.printStackTrace();
				games = List.of();
			}
			ObservableList<Game> obs = FXCollections.observableArrayList(games);
			Platform.runLater(() -> tvTableView.setItems(obs));
		}).start();

		Profile userProfile = AppState.getUserProfile();
		lblUsername.textProperty().bind(userProfile.usernameProperty());
		imgAvatar.imageProperty().bind(userProfile.avatarProperty());

		versionLabel.setText(App.getVersion());

		double radius = imgAvatar.getFitWidth() / 2.0;
		Circle clip = new Circle(radius, radius, radius);
		imgAvatar.setClip(clip);

		btnEditProfile.setOnAction(e -> MainController.openEditProfileDialog(userProfile));

		btnHome.setOnAction(event -> {
			try {
				App.setRoot("MainView");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		btnWishlist.setOnAction(event -> {
			try {
				App.setRoot("Wishlist");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		btnSearchGames.setOnAction(event -> {
			try {
				App.setRoot("GameSearch");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}
	
	private void performSearch(String query) {
	    new Thread(() -> {
	        List<Game> results;
	        try {
	            results = apiService.searchGames(query);
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            results = List.of();
	        }
	        ObservableList<Game> obs = FXCollections.observableArrayList(results);
	        Platform.runLater(() -> tvTableView.setItems(obs));
	    }).start();
	}
	
	private void openGameDetail(int gameId) {
	    AppState.setSelectedGameId(gameId);
	    AppState.setPreviousView("GameSearch");
	    try {
	        App.setRoot("Game");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
}
