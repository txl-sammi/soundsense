package com.example.soundsenseapp.Spotify;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;

public class DatabaseHelper {
    private static final String URL = "jdbc:sqlite:spotify.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Method to create tables if they don't exist
    public static void createTables() {
        String createPlaylistsTable = " CREATE TABLE IF NOT EXISTS Playlists ( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL UNIQUE, creationDate DATETIME DEFAULT CURRENT_TIMESTAMP);";

        String createSongsTable = "CREATE TABLE IF NOT EXISTS Songs (id INTEGER PRIMARY KEY AUTOINCREMENT,playlist_id INTEGER,name TEXT,artist TEXT,albumCover TEXT,spotifyLink TEXT,FOREIGN KEY (playlist_id) REFERENCES Playlists(id));";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Execute the SQL statements to create the tables
            stmt.execute(createPlaylistsTable);
            stmt.execute(createSongsTable);
            System.out.println("Tables created or already exist.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void savePlaylist(String playlistName, ArrayList<SongFormat> songs) {
        String insertPlaylistSQL = "INSERT INTO Playlists (name, creationDate) VALUES (?, ?)";
        String insertSongSQL = "INSERT INTO Songs (playlist_id, name, artist, albumCover, spotifyLink) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.connect()) {
            // Insert Playlist and get the generated ID
            PreparedStatement playlistStmt = conn.prepareStatement(insertPlaylistSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            playlistStmt.setString(1, playlistName);
            playlistStmt.setDate(2, Date.valueOf(String.valueOf(LocalDate.now())));  // Set the current date
            playlistStmt.executeUpdate();

            // Retrieve playlist ID
            ResultSet rs = playlistStmt.getGeneratedKeys();
            if (rs.next()) {
                int playlistId = rs.getInt(1);

                // Insert Songs
                PreparedStatement songStmt = conn.prepareStatement(insertSongSQL);
                for (SongFormat song : songs) {
                    songStmt.setInt(1, playlistId);
                    songStmt.setString(2, song.name);
                    songStmt.setString(3, song.artist);
                    songStmt.setString(4, song.albumCover);
                    songStmt.setString(5, song.spotifyLink);
                    songStmt.addBatch();
                }
                songStmt.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<SongFormat> getPlaylistSongsById(int playlistId) {
        String selectSongsSQL = "SELECT name, artist, albumCover, spotifyLink FROM Songs WHERE playlist_id = ?";
        ArrayList<SongFormat> songs = new ArrayList<>();

        try (Connection conn = DatabaseHelper.connect()) {
            // Prepare statement to get songs by playlist ID
            PreparedStatement songStmt = conn.prepareStatement(selectSongsSQL);
            songStmt.setInt(1, playlistId);
            ResultSet songRs = songStmt.executeQuery();

            // Retrieve each song and add to the list
            while (songRs.next()) {
                String name = songRs.getString("name");
                String artist = songRs.getString("artist");
                String albumCover = songRs.getString("albumCover");
                String spotifyLink = songRs.getString("spotifyLink");
                songs.add(new SongFormat(name, artist, albumCover, spotifyLink));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }

    public static ArrayList<Playlist> getAllPlaylists() {
        String selectPlaylistsSQL = "SELECT id, name, creationDate FROM Playlists";
        ArrayList<Playlist> playlists = new ArrayList<>();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Desired format

        try (Connection conn = DatabaseHelper.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectPlaylistsSQL)) {

            // Iterate through the result set and add each playlist to the list
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                long creationDateMillis = rs.getLong("creationDate");

                // Convert milliseconds to LocalDateTime
                LocalDateTime creationDate = Instant.ofEpochMilli(creationDateMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                // Format the creation date
                String formattedDate = creationDate.format(outputFormatter);

                playlists.add(new Playlist(id, name, formattedDate));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playlists;
    }
}
