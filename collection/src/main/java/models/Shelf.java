package models;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Shelf {

	private String id;
	private String name;
	private List<String> collectionIds = new ArrayList<>();
	private final ObservableList<GameCollection> collections = FXCollections.observableArrayList();

	public Shelf() {

	}

	public Shelf(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}


	public ObservableList<GameCollection> getCollections() {
		return collections;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getCollectionIds() {
		return collectionIds;
	}

	public void setCollectionIds(List<String> collectionIds) {
		this.collectionIds = (collectionIds != null ? collectionIds : new ArrayList<>());
	}

}
