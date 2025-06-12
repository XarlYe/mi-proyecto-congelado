package models;

import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;

public class Profile {
    private String id;
	private final StringProperty username = new SimpleStringProperty("USERNAME");
    private final StringProperty avatarPath = new SimpleStringProperty("avatars/user.png");
    private final ObjectProperty<Image> avatar = new SimpleObjectProperty<>();
    
    
    public Profile() {
        loadAvatarFromPath();
    }
    
    private void loadAvatarFromPath() {
        try {
            avatar.set(new Image(
                Files.newInputStream(Paths.get(avatarPath.get())),
                100, 100, true, true
            ));
        } catch (Exception e) {
            avatar.set(null);
        }
    }
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatarPath() {
		return avatarPath.get();
	}
	
    public void setAvatarPath(String p) { 
        avatarPath.set(p);
        loadAvatarFromPath();
    }

	public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }
    
    public StringProperty usernameProperty() {
        return username;
    }

    public Image getAvatar() {
        return avatar.get();
    }
    
    public void setAvatar(Image avatar) {
        this.avatar.set(avatar);
    }

    public ObjectProperty<Image> avatarProperty() {
        return avatar;
    }

}
