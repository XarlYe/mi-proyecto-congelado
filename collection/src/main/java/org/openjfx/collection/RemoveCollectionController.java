package org.openjfx.collection;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import management.DatabaseManager;
import models.GameCollection;
import models.Shelf;

public class RemoveCollectionController {

	@FXML
	private ComboBox<Shelf> cbShelves;
	@FXML
	private ComboBox<GameCollection> cbCollections;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;
    
    private Shelf selectedShelf;
    private GameCollection selectedCollection;
    private boolean confirmed = false;


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
    			App.showAlert("Warning", "You must select both a shelf and a collection");
    			return;
    		}
    		
    		Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete the collection '" + selectedCollection.getName() + "'?");
    		Optional<ButtonType> result = confirm.showAndWait();
    		if (result.isPresent() && result.get() == ButtonType.OK) {
    			DatabaseManager.deleteCollection(selectedCollection);
    			selectedShelf.getCollectionIds().remove(selectedCollection.getId());
    			DatabaseManager.updateShelf(selectedShelf);
    			selectedShelf.getCollections().remove(selectedCollection);
    			confirmed = true;
    			close();
    		}
    	});
    	
    	btnCancel.setOnAction(e -> {
    		confirmed = false;
    		close();
    	});
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void close() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
     
}