package org.openjfx.collection;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import management.DatabaseManager;
import models.Game;
import models.GameCollection;
import models.Shelf;


public class AddGameToCollectionController {
	
	@FXML
	private ComboBox<Shelf> cbShelves;
	@FXML
	private ComboBox<GameCollection> cbCollections;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;
    
    
    private Shelf selectedShelf;
    private GameCollection selectedCollection;

    
    public void setShelvesList(ObservableList<Shelf> shelves) {
        cbShelves.setItems(shelves);

        cbShelves.getSelectionModel().selectedItemProperty().addListener((obs, old, neu) -> {
            selectedShelf = neu;
            if (neu != null) {
                cbCollections.setItems(neu.getCollections());
                cbCollections.setDisable(false);
            } else {
                cbCollections.getItems().clear();
                cbCollections.setDisable(true);
            }
        });
    }

    @FXML
    public void initialize() {
    	cbCollections.setDisable(true);
    	
        btnOK.setOnAction(e -> {
            selectedCollection = cbCollections.getSelectionModel().getSelectedItem();
            if (selectedShelf == null || selectedCollection == null) {
                App.showAlert("Warning", "You must select a shelf and a collection");
                return;
            }
            Game game = AppState.getSelectedGame();
            if (game == null) {
                App.showAlert("Error", "Game did not load correctly");
                return;
            }
            selectedCollection.getGames().add(game);
            selectedCollection.getGameIds().add(String.valueOf(game.getId()));
            DatabaseManager.updateCollection(selectedCollection);
            AppState.setSelectedCollection(selectedCollection);
            Stage stage = (Stage) btnOK.getScene().getWindow();
            stage.close();

        });
    	    	
    	btnCancel.setOnAction(e-> close());
    	
    }
    
    
    
	private void close() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}


}