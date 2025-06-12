package org.openjfx.collection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Shelf;

import java.util.Optional;

public class ShelfDialogController {

    @FXML private TextField txtShelfName;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;

    private Shelf resultShelf = null;

    @FXML
    public void initialize() {
        btnOK.setOnAction(e -> {
            String name = txtShelfName.getText().trim();
            if (name.isBlank()) {
            	App.showAlert("Name required", "Shelves need a name");
            	return;
            }
            if (!name.isBlank()) {
                resultShelf = new Shelf(name);
            }
            closeWindow();
        });
        btnCancel.setOnAction(e -> {
            resultShelf = null;
            closeWindow();
        });
    }

    public void setDefaultName(String name) {
        txtShelfName.setText(name);
    }

    public Optional<Shelf> getResult() {
        return Optional.ofNullable(resultShelf);
    }

    private void closeWindow() {
        ((Stage) txtShelfName.getScene().getWindow()).close();
    }
}