package org.openjfx.collection;


import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import management.DatabaseManager;
import models.GameCollection;
import models.Shelf;

import java.util.Optional;

public class CollectionDialogController {

    @FXML private TextField txtCollectionName;
    @FXML private ColorPicker colorPicker;
    @FXML private ComboBox<Shelf> cbShelves;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;

    private GameCollection resultCollection = null;
    private Shelf selectedShelf = null;

    @FXML
    public void initialize() {
        btnOK.setOnAction(e -> {
            String name = txtCollectionName.getText().trim();
            Color c = colorPicker.getValue();
            Shelf s = cbShelves.getSelectionModel().getSelectedItem();
            
            if (s == null) {
            	App.showAlert("Required selection", "You must select a shelf");
            	return;
            }
            
            if (name.isBlank()) {
                App.showAlert("Name required", "Collections must have a name");
                return;
            }
            
            if (!name.isBlank() && s != null) {
                GameCollection newColl = new GameCollection(name, c, s.getId());
                DatabaseManager.createCollection(newColl, s.getId());
                resultCollection = newColl;
                selectedShelf = s;
                
            }
            
            closeWindow();
        });
        btnCancel.setOnAction(e -> {
            resultCollection = null;
            closeWindow();
        });
    }

    public void setShelvesList(ObservableList<Shelf> shelves) {
        cbShelves.setItems(shelves);
    }

    public void setDefaultName(String name) {
        txtCollectionName.setText(name);
    }

    public void setDefaultColor(Color c) {
        colorPicker.setValue(c);
    }

    public void setDefaultShelf(Shelf s) {
        cbShelves.getSelectionModel().select(s);
    }

    public Optional<GameCollection> getResult() {
        return Optional.ofNullable(resultCollection);
    }

    public Shelf getSelectedShelf() {
        return selectedShelf;
    }

    private void closeWindow() {
        ((Stage) txtCollectionName.getScene().getWindow()).close();
    }
}