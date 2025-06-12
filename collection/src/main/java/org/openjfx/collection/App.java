package org.openjfx.collection;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import management.DatabaseManager;
import models.GameCollection;
import models.Profile;
import models.Shelf;
import services.APIService;

import java.io.IOException;
import java.util.List;

public class App extends Application {

    private static Scene scene;
    private static final String APP_VERSION = "v0.10.0 -  Game Library App";


    @Override
    public void start(Stage stage) throws IOException {
    	
    	DatabaseManager.connect();
    	
    	AppState.startWishlist();
    	
        List<Shelf> saved = DatabaseManager.loadShelves();
        
        for (Shelf s : saved) {
            List<GameCollection> collections = DatabaseManager.loadCollections(s.getId());
            s.getCollections().setAll(collections);
        }
        
        ObservableList<Shelf> obs = FXCollections.observableArrayList(saved);
        AppState.setShelves(obs);
        
        Profile stored = DatabaseManager.loadProfile();
        AppState.setUserProfile(stored);
        
		Image icon = new Image("icons/game-controller.png");
        scene = new Scene(loadFXML("MainView"), 1280, 720);
		stage.getIcons().add(icon);
        stage.setScene(scene);
        stage.setTitle("Game library");
        stage.show();
    }
    
    
    public static APIService getApiService() {
    	APIService apiService = new APIService("cebtkhkn0ikzfv4jqcjpatzeqh1x8r", "ff7qsk21qb2sbpq44l3fa7vqn2tc92");
    	return apiService;
    }
    
    public static String getVersion() {
        return APP_VERSION;
    }
    
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
    
    @Override
    public void stop() {
    	DatabaseManager.disconnect();
    }

}