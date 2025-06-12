package org.openjfx.collection;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import management.DatabaseManager;
import models.GameCollection;
import models.Profile;
import models.Shelf;

import java.io.IOException;
import java.util.Optional;

public class MainController {

	@FXML
	private HBox topBar;
	@FXML
	private ScrollPane scrollShelves;
	@FXML
	private HBox footerBar;
	@FXML
	private Label versionLabel;
	@FXML
	private StackPane emptyStatePanel;
	@FXML
	private Button btnCreateFirstShelf;

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

	// ------------------------------

	@FXML
	private MenuButton menuButtonCollections;
	@FXML
	private MenuItem btnCreateCollection;
	@FXML
	private MenuItem btnEditCollection;
	@FXML
	private MenuItem btnRemoveCollection;

	@FXML
	private MenuButton menuButtonShelves;
	@FXML
	private MenuItem btnCreateShelf;
	@FXML
	private MenuItem btnEditShelf;
	@FXML
	private MenuItem btnRemoveShelf;

	@FXML
	private VBox vboxShelvesContainer;

	private ObservableList<Shelf> shelvesList = AppState.getShelves();


	@FXML
	public void initialize() {

		Profile userProfile = AppState.getUserProfile();

		lblUsername.textProperty().bind(userProfile.usernameProperty());
		imgAvatar.imageProperty().bind(userProfile.avatarProperty());
		
		double radius = Math.min(imgAvatar.getFitWidth(), imgAvatar.getFitHeight()) / 2.0;
		Circle clip = new Circle(radius, radius, radius);
		imgAvatar.setClip(clip);
		
		versionLabel.setText(App.getVersion());

		renderShelves(shelvesList);
		updateEmptyStateVisibility();

		shelvesList.addListener((ListChangeListener<Shelf>) c -> {
			renderShelves(shelvesList);
			updateEmptyStateVisibility();
		});

		btnEditProfile.setOnAction(e -> openEditProfileDialog(userProfile));

		btnCreateShelf.setOnAction(e -> openCreateShelfDialog());
		btnEditShelf.setOnAction(e -> openEditShelfDialog());
		btnRemoveShelf.setOnAction(e -> openRemoveShelfDialog());

		btnCreateCollection.setOnAction(e -> openCreateCollectionDialog());
		btnEditCollection.setOnAction(e -> openEditCollectionDialog());
		btnRemoveCollection.setOnAction(e -> openRemoveCollectionDialog());
		
        txtSearch.setOnAction(e -> searchCollections());
		
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
		btnCreateFirstShelf.setOnAction(e -> openCreateShelfDialog());
		shelvesList.addListener((ListChangeListener.Change<? extends Shelf> change) -> {
			updateEmptyStateVisibility();
		});
		updateEmptyStateVisibility();

	}

	private void updateEmptyStateVisibility() {
		boolean isEmpty = shelvesList.isEmpty();
		emptyStatePanel.setVisible(isEmpty);
		emptyStatePanel.setManaged(isEmpty);
		scrollShelves.setVisible(!isEmpty);
		scrollShelves.setManaged(!isEmpty);
	}


	private void renderShelves(ObservableList<Shelf> shelves) {
		vboxShelvesContainer.getChildren().clear();
		for (Shelf shelf : shelves) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("ShelfView.fxml"));
				Parent shelfRoot = loader.load();

				ShelfController shelfCtrl = loader.getController();
				shelfCtrl.setShelf(shelf);

				vboxShelvesContainer.getChildren().add(shelfRoot);
				updateEmptyStateVisibility();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void openCreateShelfDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("ShelfDialog.fxml"));
			Parent dialogRoot = loader.load();
			ShelfDialogController ctrl = loader.getController();

			// Crear un Stage modal
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Create Shelf");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(dialogRoot));
			dialogStage.showAndWait();

			Optional<Shelf> result = ctrl.getResult();
			result.ifPresent(shelf -> {
				AppState.addShelf(shelf);
			});
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void openEditShelfDialog() {
		if (shelvesList.isEmpty()) {
			App.showAlert("Warning", "There are no shelves to edit");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditShelfDialog.fxml"));
			Parent root = loader.load();
			EditShelfController ctrl = loader.getController();
			ctrl.setShelvesList(shelvesList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Edit Shelf");
			stage.setScene(new Scene(root));
			stage.showAndWait();

			if (ctrl.getEditedShelf() != null) {
				Shelf edited = ctrl.getEditedShelf();
				DatabaseManager.updateShelf(edited);
				renderShelves(AppState.getShelves());
			    updateEmptyStateVisibility();
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void openRemoveShelfDialog() {
		if (shelvesList.isEmpty()) {
			App.showAlert("Warning", "There are no shelves to remove");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveShelfDialog.fxml"));
			Parent dialogRoot = loader.load();
			RemoveShelfController ctrl = loader.getController();
			ctrl.setShelvesList(shelvesList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Delete Shelf");
			stage.setScene(new Scene(dialogRoot));
			stage.showAndWait();

			if (ctrl.isConfirmed()) {
				AppState.removeShelf(ctrl.getSelectedShelf());
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	private void openCreateCollectionDialog() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectionDialog.fxml"));
			Parent dialogRoot = loader.load();
			CollectionDialogController ctrl = loader.getController();

			ctrl.setShelvesList(shelvesList);

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Create Collection");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(dialogRoot));
			dialogStage.showAndWait();

			Optional<GameCollection> result = ctrl.getResult();
			result.ifPresent(coll -> {
			    Shelf parentShelf = ctrl.getSelectedShelf();
			    parentShelf.getCollections().add(coll);
			    parentShelf.getCollectionIds().add(coll.getId());
		        DatabaseManager.updateShelf(parentShelf);
			    renderShelves(AppState.getShelves());
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void openEditCollectionDialog() {
		boolean hasAtLeastOneCollection = shelvesList.stream().anyMatch(shelf -> !shelf.getCollections().isEmpty());

		if (!hasAtLeastOneCollection) {
			App.showAlert("Warning", "There are no collections to edit");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("EditCollectionDialog.fxml"));
			Parent root = loader.load();
			EditCollectionController ctrl = loader.getController();
			ctrl.setShelvesList(shelvesList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Edit Collection");
			stage.setScene(new Scene(root));
			stage.showAndWait();

			ctrl.getEditedCollection().ifPresent(c -> {
			    DatabaseManager.updateCollection(c);
			    renderShelves(AppState.getShelves());
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void openRemoveCollectionDialog() {
		boolean hasAtLeastOneCollection = shelvesList.stream().anyMatch(shelf -> !shelf.getCollections().isEmpty());

		if (!hasAtLeastOneCollection) {
			App.showAlert("Warning", "There are no collections to edit");
			return;
		}
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("RemoveCollectionDialog.fxml"));
			Parent dialogRoot = loader.load();
			RemoveCollectionController ctrl = loader.getController();
			ctrl.setShelvesList(shelvesList);

			Stage stage = new Stage();
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.setTitle("Delete Collection");
			stage.setScene(new Scene(dialogRoot));
			stage.showAndWait();

			if (ctrl.isConfirmed()) {
				renderShelves(AppState.getShelves());
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


	public static void openEditProfileDialog(Profile userProfile) {
		try {
			FXMLLoader loader = new FXMLLoader(MainController.class.getResource("ProfileDialog.fxml"));
			Parent dialogRoot = loader.load();
			ProfileDialogController ctrl = loader.getController();

			ctrl.setUsername(userProfile.getUsername());
			ctrl.setAvatar(userProfile.getAvatar());

			Stage dialogStage = new Stage();
			dialogStage.setTitle("Profile editor");
			dialogStage.initModality(Modality.APPLICATION_MODAL);
			dialogStage.setScene(new Scene(dialogRoot));
			dialogStage.showAndWait();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	 
	private void searchCollections() {
        String q = txtSearch.getText().trim().toLowerCase();
        if (q.isEmpty()) {
            renderShelves(shelvesList);
            return;
        }

        ObservableList<Shelf> filtered = FXCollections.observableArrayList();

        for (Shelf shelf : shelvesList) {
            ObservableList<GameCollection> matches = shelf
                .getCollections()
                .filtered(coll -> coll.getName().toLowerCase().contains(q));

            if (!matches.isEmpty()) {
                Shelf copy = new Shelf(shelf.getName());
                copy.getCollections().addAll(matches);
                filtered.add(copy);
            }
        }

        renderShelves(filtered);
    }

}
