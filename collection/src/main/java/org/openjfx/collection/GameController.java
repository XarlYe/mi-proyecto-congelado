package org.openjfx.collection;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Game;
import models.Profile;
import models.Shelf;
import services.APIService;

public class GameController {

	@FXML
	private HBox topBar;
	@FXML
	private ScrollPane scrollGames;
	@FXML
	private HBox footerBar;
	@FXML
	private VBox scrollGamesContainer;
	@FXML
	private Label versionLabel;

	@FXML
	private TextField txtSearch;

	@FXML
	private MenuButton userMenu;
	@FXML
	private MenuItem btnEditProfile;

	@FXML
	private ImageView imgAvatar;
	@FXML
	private Label lblUsername;

	@FXML
	private Button btnAddToCollection;
	@FXML
	private Button btnAddToWishlist;
	@FXML
	private Button btnReturn;
	@FXML
	private Label lblGameName;
	@FXML
	private Label lblPlatforms;
	@FXML
	private Label lblReleaseDate;
	@FXML
	private Label lblRating;
	@FXML
	private ImageView ivCover;
	@FXML
	private TextArea lblSummary;
	@FXML
	private Label lblGenres;
	@FXML
	private ImageView ivScreenhots;
	@FXML
	private Label dlcs;
	@FXML
	private Hyperlink lblUrl;
	@FXML
	private FlowPane flowScreenshots;
	@FXML
	private FlowPane flowDlcs;

	private final APIService apiService = App.getApiService();

	private ObservableList<Shelf> shelvesList = AppState.getShelves();

	@FXML
	public void initialize() {

		int id = AppState.getSelectedGameId();

		new Thread(() -> {
			Game g = apiService.getGameDetails(id);
			if (g != null) {
				Platform.runLater(() -> populateGame(g));
			}
		}).start();

		Profile userProfile = AppState.getUserProfile();
		lblUsername.textProperty().bind(userProfile.usernameProperty());
		imgAvatar.imageProperty().bind(userProfile.avatarProperty());

		versionLabel.setText(App.getVersion());

		double radius = imgAvatar.getFitWidth() / 2.0;
		Circle clip = new Circle(radius, radius, radius);
		imgAvatar.setClip(clip);

		btnReturn.setOnAction(event -> {
			String prev = AppState.getPreviousView();
			if (prev == null) {
				prev = "GameSearch";
			}
			try {
				App.setRoot(prev);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		btnAddToCollection.setOnAction(e -> openAddToCollectionDialog());
		btnAddToWishlist.setOnAction(e -> {
		    Game g = AppState.getSelectedGame();
		    if (g == null) {
		        App.showAlert("Error", "No game loaded");
		        return;
		    }
		    AppState.addToWishlist(g);
			Alert success = new Alert(Alert.AlertType.INFORMATION, "Game added to wishlist '" + g.getName() + "'");
			success.showAndWait();
		});
		
		lblUrl.setOnAction(e -> {
			try {
				URI uri = new URI(lblUrl.getText());
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(uri);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	private void openAddToCollectionDialog() {
		boolean hasAtLeastOneCollection = shelvesList.stream().anyMatch(shelf -> !shelf.getCollections().isEmpty());

		if (!hasAtLeastOneCollection) {
			App.showAlert("Warning", "There are no collections created");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("AddGameToCollectionDialog.fxml"));
			Parent dialogRoot = loader.load();
			AddGameToCollectionController ctrl = loader.getController();
			ctrl.setShelvesList(shelvesList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Add game to Collection");
			stage.setScene(new Scene(dialogRoot));
			stage.showAndWait();
			
			Alert success = new Alert(Alert.AlertType.INFORMATION, "Game added to collection '" + AppState.getSelectedCollection().getName() + "'");
			success.showAndWait();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void populateGame(Game g) {
		lblGameName.setText(g.getName());
		lblReleaseDate.setText(
				g.getReleaseDate() != null ? g.getReleaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
						: "Unknown");
		lblRating.setText(String.format("%.1f", g.getRating()));
		lblPlatforms.setText(String.join(", ", g.getPlatforms()));
		lblGenres.setText(String.join(", ", g.getGenres()));
		lblSummary.setText(g.getSummary());

		if (g.getCoverId() != null) {
			Image cover = new Image("https://images.igdb.com/igdb/image/upload/t_cover_big/" + g.getCoverId() + ".jpg",
					true);
			ivCover.setImage(cover);
		}

		flowScreenshots.getChildren().clear();
		for (String shotId : g.getScreenshotIds()) {
			String urlThumb = "https://images.igdb.com/igdb/image/upload/t_screenshot_med/" + shotId + ".jpg";
			Image thumbImage = new Image(urlThumb, 200, 0, true, true);
			ImageView thumb = new ImageView(thumbImage);
			thumb.setPreserveRatio(true);
			thumb.setFitWidth(200);

			thumb.setOnMouseClicked(evt -> {
				String urlHighRes = "https://images.igdb.com/igdb/image/upload/t_screenshot_huge/" + shotId + ".jpg";
				Image highRes = new Image(urlHighRes, true);
				ImageView big = new ImageView(highRes);
				big.setPreserveRatio(true);
				big.setFitWidth(800);
				big.setFitHeight(400);

				Stage popup = new Stage();
				popup.initModality(Modality.APPLICATION_MODAL);
				popup.setTitle("Screenshot Preview");
				StackPane pane = new StackPane(big);
				pane.setPadding(new Insets(10));
				popup.setScene(new Scene(pane));
				popup.showAndWait();
			});

			flowScreenshots.getChildren().add(thumb);
		}

		flowDlcs.getChildren().clear();
		new Thread(() -> {
		    List<Game> dlcGames = apiService.getGamesByIds(g.getDlcIds());
		    Platform.runLater(() -> {
		        for (Game dlc : dlcGames) {
		            Label dlcLabel = new Label(dlc.getName());
		            flowDlcs.getChildren().add(dlcLabel);
		        }
		        if (dlcGames.isEmpty()) {
		            flowDlcs.getChildren().add(new Label("No DLCs"));
		        }
		    });
		}).start();

		lblUrl.setText(g.getUrl());
		lblUrl.setOnAction(e -> {
			try {
				URI uri = new URI(lblUrl.getText());
				if (Desktop.isDesktopSupported()) {
					Desktop.getDesktop().browse(uri);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	    AppState.setSelectedGame(g);
	}

}
