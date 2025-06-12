package org.openjfx.collection;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import models.Game;
import models.Profile;

public class WishlistController {

	@FXML
	private HBox topBar;
	@FXML
	private ListView<Game> scrollGames;
	@FXML
	private HBox footerBar;
	@FXML
	private VBox scrollGamesContainer;
	@FXML
	private Label versionLabel;

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
	private Button btnRemoveGame;

	@FXML
	public void initialize() {

		Profile userProfile = AppState.getUserProfile();
		lblUsername.textProperty().bind(userProfile.usernameProperty());
		imgAvatar.imageProperty().bind(userProfile.avatarProperty());

		versionLabel.setText(App.getVersion());

		double radius = imgAvatar.getFitWidth() / 2.0;
		Circle clip = new Circle(radius, radius, radius);
		imgAvatar.setClip(clip);

		btnEditProfile.setOnAction(e -> MainController.openEditProfileDialog(userProfile));

		scrollGames.setItems(AppState.getWishlist());

		scrollGames.setCellFactory(new Callback<>() {
			@Override
			public ListCell<Game> call(ListView<Game> list) {
				return new ListCell<>() {
					private final ImageView ivCover = new ImageView();
					private final Label lblName = new Label();
					private final Label lblRelease = new Label();
					private final Label lblRating = new Label();
					private final Label lblGenres = new Label();
					private final Hyperlink linkUrl = new Hyperlink();
					private final HBox root = new HBox(10);
					private final ContextMenu contextMenu = new ContextMenu();
					{
						ivCover.setFitWidth(120);
						ivCover.setFitHeight(170);
						ivCover.setPreserveRatio(true);

						root.setPadding(new Insets(10));
						VBox info = new VBox(5, lblName, lblRelease, lblRating, lblGenres, linkUrl);
						root.getChildren().addAll(ivCover, info);

						MenuItem miRemove = new MenuItem("Remove from wishlist");
						miRemove.setOnAction(e -> {
							Game g = getItem();
							Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
									"Are you sure you want to remove “" + g.getName() + "” from the wishlist?", ButtonType.YES,
									ButtonType.NO);
							confirm.setHeaderText(null);
							confirm.setTitle("Remove game");
							confirm.showAndWait().ifPresent(btn -> {
								if (btn == ButtonType.YES)
									if (g != null) {
										AppState.removeFromWishlist(g);
									}
							});

						});
						contextMenu.getItems().add(miRemove);

					}

					@Override
					protected void updateItem(Game g, boolean empty) {
						super.updateItem(g, empty);
						if (empty || g == null) {
							setGraphic(null);
							setContextMenu(null);
						} else {
							ivCover.setImage(new Image(
									"https://images.igdb.com/igdb/image/upload/t_cover_big/" + g.getCoverId() + ".jpg",
									true));
							lblName.setText(g.getName());
							lblRelease.setText("Release: " + (g.getReleaseDate() != null
									? g.getReleaseDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
									: "Unknown"));
							lblRating.setText(String.format("Rating: %.1f", g.getRating()));
							lblGenres.setText("Genres: " + String.join(", ", g.getGenres()));
							linkUrl.setText(g.getUrl());
							linkUrl.setOnAction(ev -> {
								try {
									Desktop.getDesktop().browse(URI.create(g.getUrl()));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							});

							root.setOnMouseClicked(evt -> {
								if (evt.getButton() == MouseButton.PRIMARY && evt.getClickCount() == 1) {
									AppState.setSelectedGameId(g.getId());
									AppState.setPreviousView("Wishlist");
									try {
										App.setRoot("Game");
									} catch (Exception ex) {
										ex.printStackTrace();
									}
								}
							});

							setGraphic(root);
							setContextMenu(contextMenu);
						}
					}
				};
			}
		});

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

}
