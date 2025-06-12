package org.openjfx.collection;


import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Label;
import models.GameCollection;
import models.Shelf;

import java.io.IOException;


public class ShelfController {

    @FXML private Label lblShelfName;
    @FXML private TilePane flowCollections;

    private Shelf shelf;
    
    public void setShelf(Shelf shelf) {
        this.shelf = shelf;

        lblShelfName.setText(shelf.getName());
        renderCollections();                

        shelf.getCollections().addListener((ListChangeListener<GameCollection>) change -> {
            renderCollections();
        });
    }

    private void renderCollections() {
        flowCollections.getChildren().clear();
        for (GameCollection coll : shelf.getCollections()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CollectionPanel.fxml"));
                Parent collRoot = loader.load();

                CollectionPanelController collCtrl = loader.getController();
                collCtrl.setCollection(coll);

                flowCollections.getChildren().add(collRoot);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void refreshName() {
        lblShelfName.setText(shelf.getName());
    }
}
