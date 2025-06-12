package org.openjfx.collection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import management.DatabaseManager;
import models.Profile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import static management.DatabaseManager.AVATAR_DIR;


public class ProfileDialogController {

    @FXML private TextField txtUsername;
    @FXML private ImageView imgPreview;
    @FXML private Button btnChooseAvatar;
    @FXML private Button btnOK;
    @FXML private Button btnCancel;

    private Profile resultProfile = null;

    
    private File selectedFile; 

    @FXML
    public void initialize() {
    	
        Profile appProfile = AppState.getUserProfile();
    	
        setUsername(appProfile.getUsername());
        setAvatar(appProfile.getAvatar());
        
        btnChooseAvatar.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choose Avatar");
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
            File file = chooser.showOpenDialog(imgPreview.getScene().getWindow());
            if (file != null) {
                Image preview = new Image(
                        file.toURI().toString(),
                        imgPreview.getFitWidth(),
                        imgPreview.getFitHeight(),
                        true,
                        true
                        
                );
                selectedFile = file;
                imgPreview.setImage(preview);
            }
        });
        
        

        btnOK.setOnAction(e -> {
            String newName = txtUsername.getText().trim();
            if (newName.isBlank()) {
                App.showAlert("Name required", "You must set a username");
                return;
            }

            Profile existing = AppState.getUserProfile();
            existing.setUsername(newName);

            if (selectedFile != null) {
                try {

                    String filename = System.currentTimeMillis() + "-" + selectedFile.getName();
                    Path destFile = AVATAR_DIR.resolve(filename);

                    Files.copy(
                        selectedFile.toPath(),
                        destFile,
                        StandardCopyOption.REPLACE_EXISTING
                    );

                    existing.setAvatarPath(destFile.toAbsolutePath().toString());

                } catch (IOException ex) {
                    App.showAlert("Error saving avatar", ex.getMessage());
                    return;
                }
            }
            DatabaseManager.updateProfile(existing);

            closeWindow();
        });

        btnCancel.setOnAction(e -> closeWindow());
    }

    public void setUsername(String username) {
        this.txtUsername.setText(username);
    }

    public void setAvatar(Image avatar) {
        this.imgPreview.setImage(avatar);
    }

    public Optional<Profile> getResult() {
        return Optional.ofNullable(resultProfile);
    }

    private void closeWindow() {
        ((Stage) txtUsername.getScene().getWindow()).close();
    }
}
