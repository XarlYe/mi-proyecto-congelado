package org.openjfx.collection;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Circle;
import models.Game;
import models.GameCollection;
import models.Profile;
import services.APIService;

public class CollectionController {

	@FXML
	private HBox topBar;
	@FXML
	private ScrollPane scrollGames;
	@FXML
	private TilePane scrollGamesContainer;
	@FXML
	private HBox footerBar;
	@FXML
	private Label versionLabel;
	@FXML
	private Label lblCollectionName;

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
	private Button btnReturn;

	private final APIService apiService = App.getApiService();

	private GameCollection coll;

	// ------------------------------

	public void setGameCollection(GameCollection coll) {
		this.coll = coll;

		coll.getGames().clear();

		for (String gid : coll.getGameIds()) {
			try {
				int id = Integer.parseInt(gid);
				Game g = apiService.getGameDetails(id);
				if (g != null) {
					coll.getGames().add(g);
				}
			} catch (NumberFormatException ex) {
				System.err.println("Invalid gameId in collection: " + gid);
			}
		}

		lblCollectionName.setText(coll.getName());
		loadCovers();
	}

	@FXML
	public void initialize() {


		this.coll = AppState.getSelectedCollection();
		if (coll != null) {
			setGameCollection(coll);
		}
		
		lblCollectionName.setText(coll.getName());

		Profile userProfile = AppState.getUserProfile();
		lblUsername.textProperty().bind(userProfile.usernameProperty());
		imgAvatar.imageProperty().bind(userProfile.avatarProperty());

		versionLabel.setText(App.getVersion());

		double radius = imgAvatar.getFitWidth() / 2.0;
		Circle clip = new Circle(radius, radius, radius);
		imgAvatar.setClip(clip);

		btnEditProfile.setOnAction(e -> MainController.openEditProfileDialog(userProfile));

		btnReturn.setOnAction(event -> {
			try {
				App.setRoot("MainView");
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}

	private void loadCovers() {
		scrollGamesContainer.getChildren().clear();
		
	    if (this.coll == null) return;

		for (Game game : coll.getGames()) {
			ImageView cover = new ImageView(
					new Image("https://images.igdb.com/igdb/image/upload/t_cover_big/" + game.getCoverId() + ".jpg",
							150, 200, true, true));
			cover.setCursor(Cursor.HAND);

			cover.setOnMouseClicked(e -> {
				if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 1) {
					AppState.setSelectedGameId(game.getId());
					AppState.setPreviousView("CollectionView");
					try {
						App.setRoot("Game");
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
			});

			MenuItem miRemove = new MenuItem("Remove from collection");
			miRemove.setOnAction(e -> {
				Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
						"Are you sure you want to remove “" + game.getName() + "” from the collection?", ButtonType.YES,
						ButtonType.NO);
				confirm.setHeaderText(null);
				confirm.setTitle("Remove Game");
				confirm.showAndWait().ifPresent(btn -> {
					if (btn == ButtonType.YES) {
						coll.getGames().remove(game);
						loadCovers();
					}
				});
			});
			ContextMenu ctx = new ContextMenu(miRemove);
			cover.setOnContextMenuRequested(evt -> ctx.show(cover, evt.getScreenX(), evt.getScreenY()));

			scrollGamesContainer.getChildren().add(cover);
		}
	}

	public void onShow() {
		loadCovers();
	}

}
