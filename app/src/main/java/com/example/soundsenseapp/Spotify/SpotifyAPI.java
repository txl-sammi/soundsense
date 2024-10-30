package com.example.soundsenseapp.Spotify;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SpotifyAPI {
    private final String CLIENT_ID = "ac2f7e13ec9b4280a7111db9ea8e8283";
    private final String CLIENT_SECRET = "a78b23e4ffab4f76800fd6888709eb75";
    private String token;

    public SpotifyAPI () throws IOException, JSONException {
        token = String.valueOf(new GetTokenTask().execute());
    }

    private String getToken() throws IOException, JSONException {
        String authString = CLIENT_ID + ":" + CLIENT_SECRET;
        String authBase64 = Base64.getEncoder().encodeToString(authString.getBytes(StandardCharsets.UTF_8));
        String url = "https://accounts.spotify.com/api/token";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Basic " + authBase64);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        String data = "grant_type=client_credentials";
        try (OutputStream os = connection.getOutputStream()) {
            os.write(data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Log.e("SpotifyAPI", "Error in getToken output stream: " + e.getMessage(), e);
        }
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to get token: " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        JSONObject jsonResult = new JSONObject(response.toString());
        return jsonResult.getString("access_token");
    }

    private class GetTokenTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                return getToken();
            } catch (Exception e) {
                Log.e("SpotifyAPI", "Error getting token: " + e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String token) {
            if (token != null) {
                Log.d("SpotifyAPI", "Token: " + token);
            } else {
                Log.e("SpotifyAPI", "Failed to retrieve token.");
            }
        }
    }

    public ArrayList<SongFormat> getSongsByGenre(String genre, int limit) throws IOException {
        String encodedGenre = URLEncoder.encode(genre, "UTF-8");
        String url = "https://api.spotify.com/v1/recommendations?seed_genres=" + encodedGenre +
                "&limit="+limit+"&min_popularity=10&max_popularity=50";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to search for genre: " + responseCode + " " + connection.getResponseMessage());
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            JSONObject jsonObject =  new JSONObject(response.toString());
            JSONArray tracks = jsonObject.getJSONArray("tracks");
            ArrayList trackList = new ArrayList<SongFormat>();
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                String albumCoverUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                String trackUrl = track.getJSONObject("external_urls").getString("spotify");
                SongFormat trackFormat = new SongFormat(trackName, artistName, albumCoverUrl, trackUrl);
                trackList.add(trackFormat);
            }
            return trackList;
        } catch (Exception e) {
            throw new IOException("Error reading response: " + e.getMessage(), e);
        }
    }

    public ArrayList<SongFormat> getSongsByTempo(String genre, int minTempo, int maxTempo, int limit) throws Exception {
        String encodedGenre = URLEncoder.encode(genre, "UTF-8");
        String url = "https://api.spotify.com/v1/recommendations?seed_genres=" + URLEncoder.encode(genre, "UTF-8") +
                "&limit=" + limit +
                "&min_tempo=" + minTempo +
                "&max_tempo=" + maxTempo +
                "&min_popularity=10" +
                "&max_popularity=50";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to search for genre: " + responseCode + " " + connection.getResponseMessage());
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            JSONObject jsonObject =  new JSONObject(response.toString());
            JSONArray tracks = jsonObject.getJSONArray("tracks");
            ArrayList trackList = new ArrayList<SongFormat>();
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                String albumCoverUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                String trackUrl = track.getJSONObject("external_urls").getString("spotify");
                SongFormat trackFormat = new SongFormat(trackName, artistName, albumCoverUrl, trackUrl);
                trackList.add(trackFormat);
            }
            return trackList;
        } catch (Exception e) {
            throw new IOException("Error reading response: " + e.getMessage(), e);
        }
    }

    public ArrayList<SongFormat> getSongsByEnergy(String genre, int minEnergy, int maxEnergy, int limit) throws Exception {
        String encodedGenre = URLEncoder.encode(genre, "UTF-8");
        String url = "https://api.spotify.com/v1/recommendations?seed_genres=" + URLEncoder.encode(genre, "UTF-8") +
                "&limit=" + limit +
                "&min_energy=" + minEnergy +
                "&max_energy=" + maxEnergy +
                "&min_popularity=10" +
                "&max_popularity=50";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to search for genre: " + responseCode + " " + connection.getResponseMessage());
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            JSONObject jsonObject =  new JSONObject(response.toString());
            JSONArray tracks = jsonObject.getJSONArray("tracks");
            ArrayList trackList = new ArrayList<SongFormat>();
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                String albumCoverUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                String trackUrl = track.getJSONObject("external_urls").getString("spotify");
                SongFormat trackFormat = new SongFormat(trackName, artistName, albumCoverUrl, trackUrl);
                trackList.add(trackFormat);
            }
            return trackList;
        } catch (Exception e) {
            throw new IOException("Error reading response: " + e.getMessage(), e);
        }
    }

    public ArrayList<SongFormat> getSongsByDanceability(String genre, int minDanceability, int maxDanceability, int limit) throws Exception {
        String encodedGenre = URLEncoder.encode(genre, "UTF-8");
        String url = "https://api.spotify.com/v1/recommendations?seed_genres=" + URLEncoder.encode(genre, "UTF-8") +
                "&limit=" + limit +
                "&min_danceability=" + minDanceability +
                "&min_danceability=" + maxDanceability +
                "&min_popularity=10" +
                "&max_popularity=50";
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed to search for genre: " + responseCode + " " + connection.getResponseMessage());
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            JSONObject jsonObject =  new JSONObject(response.toString());
            JSONArray tracks = jsonObject.getJSONArray("tracks");
            ArrayList trackList = new ArrayList<SongFormat>();
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                String trackName = track.getString("name");
                String artistName = track.getJSONArray("artists").getJSONObject(0).getString("name");
                String albumCoverUrl = track.getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");
                String trackUrl = track.getJSONObject("external_urls").getString("spotify");
                SongFormat trackFormat = new SongFormat(trackName, artistName, albumCoverUrl, trackUrl);
                trackList.add(trackFormat);
            }
            return trackList;
        } catch (Exception e) {
            throw new IOException("Error reading response: " + e.getMessage(), e);
        }
    }
}