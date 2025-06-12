package org.openjfx.collection;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Shelf;


public class EditShelfController {

	@FXML
	private ComboBox<Shelf> cbShelves;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;
    @FXML private TextField txtShelfName;
    
    private Shelf editedShelf;
    
    public void setShelvesList(ObservableList<Shelf> shelves) {
        cbShelves.setItems(shelves);
        cbShelves.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                txtShelfName.setText(n.getName());
            }
        });
    }

    @FXML
    public void initialize() {
        btnOK.setOnAction(e -> {
            Shelf s = cbShelves.getSelectionModel().getSelectedItem();
            String nuevo = txtShelfName.getText().trim();
            if (s == null || nuevo.isEmpty()) {
                App.showAlert("Warning", "A shelf must be selected and it cannot have a null name");
                return;
            }
            s.setName(nuevo);
            editedShelf = s;
            close();
        });
        btnCancel.setOnAction(e -> close());
    }

    public Shelf getEditedShelf() {
        return editedShelf;
    }

    private void close() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}