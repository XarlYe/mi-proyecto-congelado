package org.openjfx.collection;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.GameCollection;
import models.Shelf;

public class EditCollectionController {

	@FXML
	private ComboBox<Shelf> cbShelves;
	@FXML
	private ComboBox<Shelf> cbShelvesChange;
	@FXML
	private ComboBox<GameCollection> cbCollections;
	@FXML
	private Button btnOK;
	@FXML
	private Button btnCancel;
	@FXML
	private TextField txtCollectionName;
	@FXML
	private ColorPicker colorPicker;

	private GameCollection editedCollection;
	private Shelf originalShelf;

	public void setShelvesList(ObservableList<Shelf> shelves) {
		cbShelves.setItems(shelves);
		cbShelves.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
			if (n != null) {
				cbCollections.setItems(n.getCollections());
				cbCollections.setDisable(false);
			}
		});
	}

	@FXML
	public void initialize() {
		cbCollections.setDisable(true);

		cbCollections.getSelectionModel().selectedItemProperty().addListener((obs, o, selected) -> {
			if (selected != null) {
				txtCollectionName.setText(selected.getName());
				colorPicker.setValue(selected.getColor());
				originalShelf = cbShelves.getSelectionModel().getSelectedItem();
			}
		});

		btnOK.setOnAction(e -> {
			GameCollection coll = cbCollections.getSelectionModel().getSelectedItem();
			if (coll == null) {
				App.showAlert("Warning", "You must select a shelf and a collection");
				return;
			}
			coll.setName(txtCollectionName.getText().trim());
			coll.setColor(colorPicker.getValue());
			editedCollection = coll;
			close();
		});
		btnCancel.setOnAction(e -> close());
	}

	public Optional<GameCollection>  getEditedCollection() {
	    return Optional.ofNullable(editedCollection);
	}
	
	public Shelf getOriginalShelf() {
	    return originalShelf;
	}

	private void close() {
		Stage stage = (Stage) btnCancel.getScene().getWindow();
		stage.close();
	}
}