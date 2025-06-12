package management;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;

import javafx.scene.paint.Color;
import models.GameCollection;
import models.Profile;
import models.Shelf;
import models.Wishlist;



public class DatabaseManager {
	private static MongoClient mongoClient;
	private static MongoDatabase database;
    private static MongoCollection<Document> shelvesColl;
    private static MongoCollection<Document> collectionsColl;
    private static MongoCollection<Document> profilesColl;
    private static MongoCollection<Document> wishlistColl;
    public static final Path AVATAR_DIR = Paths.get(
    	    System.getProperty("user.home"),
    	    ".game-library",
    	    "avatars"
    	);

	public static void connect() {
		try {
	        try {
	            Files.createDirectories(AVATAR_DIR);
	        } catch (IOException e) {
	            throw new RuntimeException("Unable to create vatar directory", e);
	        }
			mongoClient = MongoClients.create("mongodb://localhost:27017");
			database = mongoClient.getDatabase("GameLibrary");
	        shelvesColl = database.getCollection("shelves");
	        collectionsColl = database.getCollection("collections");
	        profilesColl = database.getCollection("UserProfile");
	        wishlistColl = database.getCollection("wishlist");

			System.out.println("Connection to MongoDB succesfull.");
		} catch (Exception e) {
			System.err.println("MongoDB connection error: " + e.getMessage());
		}
	}

	public static void disconnect() {
		if (mongoClient != null) {
			try {
				mongoClient.close();
				System.out.println("Connection closed.");
			} catch (Exception e) {
				System.err.println("Error when trying to close connection: " + e.getMessage());
			} finally {
				mongoClient = null;
				database = null;
			}
		}
	}

	public static void cleanDatabase() {
		database.getCollection("shelves").deleteMany(new Document());
		database.getCollection("collections").deleteMany(new Document());
		database.getCollection("games").deleteMany(new Document());
		database.getCollection("wishlists").deleteMany(new Document());
	}

	// Shelves-------------------------------------------------------------------------------------------------

	
    // Convierte un Shelf a Document
	private static Document toDocument(Shelf s, boolean forUpdate) {
	    Document doc = new Document("name", s.getName())
	                   .append("collectionIds", s.getCollectionIds());
	    if (forUpdate && s.getId() != null) {
	        doc.append("_id", new ObjectId(s.getId()));
	    }
	    return doc;
	}

    // Convierte un Document a Shelf
    private static Shelf fromDocument(Document doc) {
        Shelf s = new Shelf(doc.getString("name"));
        String id = doc.getObjectId("_id").toHexString();
        s.setId(id);
        @SuppressWarnings("unchecked")
        List<String> collIds = (List<String>)doc.get("collectionIds");
        s.setCollectionIds(collIds);
        return s;
    }
    
    public static List<Shelf> loadShelves() {
        List<Shelf> result = new ArrayList<>();
        for (Document doc : shelvesColl.find()) {
            result.add(fromDocument(doc));
        }
        return result;
    }

    public static Shelf createShelf(Shelf s) {
        Document doc = toDocument(s, false);
        shelvesColl.insertOne(doc);
        String id = doc.getObjectId("_id").toHexString();
        s.setId(id);
        return s;
    }

    public static void updateShelf(Shelf s) {
        Document doc = toDocument(s, false);  
        shelvesColl.replaceOne(
            Filters.eq("_id", new ObjectId(s.getId())),
            doc
        );
    }

    public static void deleteShelf(Shelf s) {
        shelvesColl.deleteOne(Filters.eq("_id", new org.bson.types.ObjectId(s.getId())));
    }
	

	//Collections-------------------------------------------------------------------------------------------------

    private static Document collToDocument(GameCollection c) {
        return new Document("_id",       c.getId() == null ? new ObjectId() 
                                                             : new ObjectId(c.getId()))
            .append("name",     c.getName())
            .append("color",    toHexString(c.getColor()))
            .append("gameIds",  c.getGameIds())
            .append("shelfId",  c.getShelfId());
    }
    
    private static GameCollection documentToColl(Document doc) {
        GameCollection c = new GameCollection(doc.getString("name"),
                                              fromHex(doc.getString("color")),
                                              doc.getString("shelfId"));
        c.setId(doc.getObjectId("_id").toHexString());
        @SuppressWarnings("unchecked")
        List<String> games = (List<String>) doc.get("gameIds");
        c.setGameIds(games);
        return c;
    }
    
    public static List<GameCollection> loadCollections(String shelfId) {
        List<GameCollection> list = new ArrayList<>();
        for (Document doc : collectionsColl.find(Filters.eq("shelfId", shelfId))) {
            GameCollection c = documentToColl(doc);
            c.setShelfId(shelfId); 
            list.add(c);
        }
        return list;
    }
    
    public static GameCollection createCollection(GameCollection c, String shelfId) {
        c.setShelfId(shelfId);
        Document doc = collToDocument(c);
        collectionsColl.insertOne(doc);
        c.setId(doc.getObjectId("_id").toHexString());
        return c;
    }
    
    public static void updateCollection(GameCollection c) {
        Document doc = collToDocument(c);
        collectionsColl.replaceOne(Filters.eq("_id", new org.bson.types.ObjectId(c.getId())), doc);
    }

    public static void deleteCollection(GameCollection c) {
        collectionsColl.deleteOne(Filters.eq("_id", new org.bson.types.ObjectId(c.getId())));
    }
    
	private static String toHexString(Color c) {
		return String.format("#%02X%02X%02X", (int) (c.getRed() * 255), (int) (c.getGreen() * 255),
				(int) (c.getBlue() * 255));
	}
	
    private static Color fromHex(String hex) {
        return Color.web(hex);
    }
    
	//Profile-------------------------------------------------------------------------------------------------

    public static Profile loadProfile() {
        Document doc = profilesColl.find().first();
        Profile p = new Profile();
        if (doc != null) {
            if (doc.containsKey("_id"))
                p.setId(doc.getObjectId("_id").toHexString());
            p.setUsername(doc.getString("username"));
            p.setAvatarPath(doc.getString("avatarPath"));
        }
        return p;
    }

    public static void updateProfile(Profile p) {
        Document doc = new Document("username",   p.getUsername())
                       .append("avatarPath", p.getAvatarPath());
        if (p.getId() != null) {
            profilesColl.replaceOne(
                Filters.eq("_id", new org.bson.types.ObjectId(p.getId())),
                doc,
                new ReplaceOptions().upsert(true)
            );
        } else {
            profilesColl.insertOne(doc);
            p.setId(doc.getObjectId("_id").toHexString());
        }
    }
    
	//Wishlist-------------------------------------------------------------------------------------------------

    public static Wishlist loadWishlist() {
        Wishlist w = new Wishlist();
        Document doc = wishlistColl.find().first();
        if (doc == null) {
            Document empty = new Document("gameIds", List.of());
            wishlistColl.insertOne(empty);
            w.setId(empty.getObjectId("_id").toHexString());
            w.setGameIds(new ArrayList<>());
        } else {
            w.setId(doc.getObjectId("_id").toHexString());
            @SuppressWarnings("unchecked")
            List<String> ids = (List<String>) doc.get("gameIds");
            w.setGameIds(ids != null ? ids : new ArrayList<>());
        }
        return w;
    }

    public static void updateWishlist(Wishlist w) {
        Document setDoc = new Document("gameIds", w.getGameIds());

        if (w.getId() == null || w.getId().isBlank()) {
            Document fullDoc = new Document("gameIds", w.getGameIds());
            wishlistColl.insertOne(fullDoc);
            String newId = fullDoc.getObjectId("_id").toHexString();
            w.setId(newId);
        } else {
            ObjectId oid = new ObjectId(w.getId());
            wishlistColl.updateOne(
                Filters.eq("_id", oid),
                new Document("$set", setDoc),
                new UpdateOptions().upsert(true)
            );
        }
    }
    
}