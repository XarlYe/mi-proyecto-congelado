package services;

import com.google.gson.*;

import models.Game;

import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class APIService {

	private static final String API_BASE_URL = "https://api.igdb.com/v4";
	private final String accessToken;
	private final String clientId;
	private final HttpClient httpClient;

	public APIService(String accessToken, String clientId) {
		this.accessToken = accessToken;
		this.clientId = clientId;
		this.httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
				.connectTimeout(Duration.ofSeconds(30)).build();
	}

	public JsonArray executeQuery(String endpoint, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(API_BASE_URL + "/" + endpoint))
				.header("Authorization", "Bearer " + accessToken).header("Client-ID", clientId)
				.header("Content-Type", "text/plain").POST(HttpRequest.BodyPublishers.ofString(body)).build();

		HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			return JsonParser.parseString(response.body()).getAsJsonArray();
		} else {
			throw new RuntimeException("API Error: " + response.statusCode() + " - " + response.body());
		}
	}

	public Game parseGame(JsonObject json) {
		Game game = new Game();
		game.setId(json.get("id").getAsInt());
		game.setName(json.get("name").getAsString());

		if (json.has("platforms") && json.get("platforms").isJsonArray()) {
		    List<String> platformNames = new ArrayList<>();
		    for (JsonElement el : json.getAsJsonArray("platforms")) {
		        if (!el.isJsonNull()) {
		            JsonObject pObj = el.getAsJsonObject();
		            String abbr = null;
		            if (pObj.has("abbreviation") && !pObj.get("abbreviation").isJsonNull()) {
		                abbr = pObj.get("abbreviation").getAsString();
		            }
		            if (abbr == null || abbr.isBlank()) {
		                if (pObj.has("name") && !pObj.get("name").isJsonNull()) {
		                    abbr = pObj.get("name").getAsString();
		                } else {
		                    abbr = "Unknown";
		                }
		            }
		            platformNames.add(abbr);
		        }
		    }
		    game.setPlatforms(platformNames);
		}

		if (json.has("first_release_date") && !json.get("first_release_date").isJsonNull()) {
			long timestamp = json.get("first_release_date").getAsLong();
			LocalDate fecha = Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
			game.setReleaseDate(fecha);
		}

		if (json.has("rating") && !json.get("rating").isJsonNull()) {
			game.setRating(json.get("rating").getAsDouble());
		}

		if (json.has("cover") && json.get("cover").isJsonObject()) {
			JsonObject coverObj = json.getAsJsonObject("cover");
			if (coverObj.has("image_id")) {
				game.setCoverId(coverObj.get("image_id").getAsString());
			}
		}

		if (json.has("summary") && !json.get("summary").isJsonNull()) {
			game.setSummary(json.get("summary").getAsString());
		}

		if (json.has("genres")) {
			List<String> names = new ArrayList<>();
			for (JsonElement el : json.getAsJsonArray("genres")) {
				JsonObject g = el.getAsJsonObject();
				names.add(g.get("name").getAsString());
			}
			game.setGenres(names);
		}

		if (json.has("screenshots") && json.get("screenshots").isJsonArray()) {
			JsonArray shots = json.getAsJsonArray("screenshots");
			for (JsonElement element : shots) {
				JsonObject shotObj = element.getAsJsonObject();
				if (shotObj.has("image_id")) {
					game.getScreenshotIds().add(shotObj.get("image_id").getAsString());
				}
			}
		}

		if (json.has("dlcs") && !json.get("dlcs").isJsonNull()) {
			JsonArray dlcs = json.getAsJsonArray("dlcs");
			for (JsonElement element : dlcs) {
				game.getDlcIds().add(element.getAsInt());
			}
		}

		if (json.has("url") && !json.get("url").isJsonNull()) {
			game.setUrl(json.get("url").getAsString());
		}

		return game;
	}
	
	public List<Game> getGamesByIds(List<Integer> ids) {
	    if (ids == null || ids.isEmpty()) return Collections.emptyList();
	    String joined = ids.stream().map(Object::toString).collect(Collectors.joining(","));
	    String requestBody = "where id = (" + joined + "); fields id, name; limit " + ids.size() + ";";
	    try {
	        JsonArray results = executeQuery("games", requestBody);
	        List<Game> games = new ArrayList<>();
	        for (JsonElement el : results) {
	            JsonObject obj = el.getAsJsonObject();
	            Game g = new Game();
	            g.setId(obj.get("id").getAsInt());
	            g.setName(obj.get("name").getAsString());
	            games.add(g);
	        }
	        return games;
	    } catch (Exception e) {
	        System.err.println("Error fetching DLC names: " + e.getMessage());
	        return Collections.emptyList();
	    }
	}

	public List<Game> parseGames(JsonArray jsonArray) {
		List<Game> games = new ArrayList<>();
		for (JsonElement element : jsonArray) {
			games.add(parseGame(element.getAsJsonObject()));
		}
		return games;
	}

	public Game getGameDetails(int id) {
		try {
			String requestBody = "where id = " + id + "; " + "fields id, name, platforms.abbreviation, first_release_date, rating, "
					+ "cover.image_id, summary, genres.name, screenshots.image_id, dlcs, url; " + "limit 1;";
			JsonArray results = executeQuery("games", requestBody);
			if (results.size() > 0) {
				return parseGame(results.get(0).getAsJsonObject());
			}
			return null;
		} catch (Exception e) {
			System.err.println("Error getting game details: " + e.getMessage());
			return null;
		}
	}
	

	public List<Game> searchGames(String query) {
		try {
			String requestBody = "search \"" + query + "\"; " + "fields id, name, platforms.abbreviation, first_release_date, "
					+ "rating, genres.name; " + "limit 50;";

			JsonArray results = executeQuery("games", requestBody);
			return parseGames(results);
		} catch (Exception e) {
			System.err.println("Error searching games: " + e.getMessage());
			return new ArrayList<>();
		}
	}
}
