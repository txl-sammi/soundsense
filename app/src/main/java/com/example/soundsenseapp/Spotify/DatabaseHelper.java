package com.example.soundsenseapp.Spotify;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "spotify.db";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper instance;

    private static final String CREATE_PLAYLISTS_TABLE =
            "CREATE TABLE IF NOT EXISTS Playlists (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL UNIQUE, " +
                    "creationDate DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_SONGS_TABLE =
            "CREATE TABLE IF NOT EXISTS Songs (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "playlist_id INTEGER, " +
                    "name TEXT, " +
                    "artist TEXT, " +
                    "albumCover TEXT, " +
                    "spotifyLink TEXT, " +
                    "FOREIGN KEY (playlist_id) REFERENCES Playlists(id));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_PLAYLISTS_TABLE);
            db.execSQL(CREATE_SONGS_TABLE);
            Log.d("DatabaseHelper", "Tables created successfully.");
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating tables: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Playlists");
        db.execSQL("DROP TABLE IF EXISTS Songs");
        onCreate(db);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static void savePlaylist(DatabaseHelper dbHelper, String playlistName, ArrayList<SongFormat> songs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String insertPlaylistSQL = "INSERT INTO Playlists (name, creationDate) VALUES (?, ?)";
        String insertSongSQL = "INSERT INTO Songs (playlist_id, name, artist, albumCover, spotifyLink) VALUES (?, ?, ?, ?, ?)";

        db.beginTransaction();
        try {
            SQLiteStatement playlistStmt = db.compileStatement(insertPlaylistSQL);
            playlistStmt.bindString(1, playlistName);
            playlistStmt.bindString(2, LocalDate.now().toString());
            long playlistId = playlistStmt.executeInsert();

            SQLiteStatement songStmt = db.compileStatement(insertSongSQL);
            for (SongFormat song : songs) {
                songStmt.bindLong(1, playlistId);
                songStmt.bindString(2, song.name);
                songStmt.bindString(3, song.artist);
                songStmt.bindString(4, song.albumCover);
                songStmt.bindString(5, song.spotifyLink);
                songStmt.executeInsert();
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public static ArrayList<SongFormat> getPlaylistSongsById(DatabaseHelper dbHelper, int playlistId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectSongsSQL = "SELECT name, artist, albumCover, spotifyLink FROM Songs WHERE playlist_id = ?";
        ArrayList<SongFormat> songs = new ArrayList<>();

        try (Cursor cursor = db.rawQuery(selectSongsSQL, new String[]{String.valueOf(playlistId)})) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow("artist"));
                String albumCover = cursor.getString(cursor.getColumnIndexOrThrow("albumCover"));
                String spotifyLink = cursor.getString(cursor.getColumnIndexOrThrow("spotifyLink"));
                songs.add(new SongFormat(name, artist, albumCover, spotifyLink));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return songs;
    }

    public static ArrayList<Playlist> getAllPlaylists(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectPlaylistsSQL = "SELECT id, name, creationDate FROM Playlists";
        ArrayList<Playlist> playlists = new ArrayList<>();
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (Cursor cursor = db.rawQuery(selectPlaylistsSQL, null)) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String creationDateString = cursor.getString(cursor.getColumnIndexOrThrow("creationDate"));
                LocalDate creationDate = LocalDate.parse(creationDateString);
                String formattedDate = creationDate.format(outputFormatter);

                playlists.add(new Playlist(id, name, formattedDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
        return playlists;
    }

}
