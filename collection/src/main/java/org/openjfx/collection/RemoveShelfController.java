package org.openjfx.collection;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.Shelf;


public class RemoveShelfController {

	@FXML
	private ComboBox<Shelf> cbShelves;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;
    
    private Shelf selectedShelf;
    private boolean confirmed = false;
    
    public void setShelvesList(ObservableList<Shelf> shelves) {
        cbShelves.setItems(shelves);
    }

    @FXML
    public void initialize() {
        btnOK.setOnAction(e -> {
            selectedShelf = cbShelves.getSelectionModel().getSelectedItem();
            if (selectedShelf == null) {
    			App.showAlert("Warning", "You must select a shelf");
                return;
            }
            if (!selectedShelf.getCollections().isEmpty()) {
                new Alert(Alert.AlertType.ERROR,
                    "Cannot delete shelf '" + selectedShelf.getName() + "' because it contains collections.")
                .showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete the shelf ‘" + selectedShelf.getName() + "’?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                confirmed = true;
                close();
            }
        });
        btnCancel.setOnAction(e -> {
            confirmed = false;
            close();
        });
    }

    public Shelf getSelectedShelf() {
        return selectedShelf;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    private void close() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
  }
