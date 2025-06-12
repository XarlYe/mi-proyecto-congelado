package org.openjfx.collection;

import java.io.IOException;

import javafx.fxml.FXML;
import models.GameCollection;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class CollectionPanelController {

	@FXML
	private Label lblCollectionName;
	@FXML
	private Pane paneColor;
	@FXML
	private Label lblGameCount;
	@FXML
	private VBox rootPane;

	private GameCollection gameCollection;
	
    public void initialize() {
        rootPane.setOnMouseClicked(evt -> openCollectionDetail());
    }

	public void setCollection(GameCollection gameCollection) {
		this.gameCollection = gameCollection;
		lblCollectionName.setText(gameCollection.getName());
		paneColor.setStyle(
				"-fx-background-color: " + toHexString(gameCollection.getColor()) + ";" + "-fx-background-radius: 8;");
        lblGameCount.setText(gameCollection.getGameIds().size() + " Games");
	}
	
    private void openCollectionDetail() {
        try {
        	
        	AppState.setSelectedCollection(gameCollection);
        	AppState.setPreviousView("CollectionView");
        	App.setRoot("CollectionView");

            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private String toHexString(Color c) {
		return String.format("#%02X%02X%02X", (int) (c.getRed() * 255), (int) (c.getGreen() * 255),
				(int) (c.getBlue() * 255));
	}
}