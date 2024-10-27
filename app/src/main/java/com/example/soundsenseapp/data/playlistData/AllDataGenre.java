package com.example.soundsenseapp.data.playlistData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllDataGenre {
    
    // List of genres
    private static final String[] genres = {
        "acoustic", "afrobeat", "alt-rock", "alternative", "ambient", "anime", "black-metal", "bluegrass", "blues", 
        "bossanova", "brazil", "breakbeat", "british", "cantopop", "chicago-house", "children", "chill", "classical", 
        "club", "comedy", "country", "dance", "dancehall", "death-metal", "deep-house", "detroit-techno", "disco", 
        "disney", "drum-and-bass", "dub", "dubstep", "edm", "electro", "electronic", "emo", "folk", "forro", "french", 
        "funk", "garage", "german", "gospel", "goth", "grindcore", "groove", "grunge", "guitar", "happy", "hard-rock", 
        "hardcore", "hardstyle", "heavy-metal", "hip-hop", "holidays", "honky-tonk", "house", "idm", "indian", "indie", 
        "indie-pop", "industrial", "iranian", "j-dance", "j-idol", "j-pop", "j-rock", "jazz", "k-pop", "kids", "latin", 
        "latino", "malay", "mandopop", "metal", "metal-misc", "metalcore", "minimal-techno", "movies", "mpb", 
        "new-age", "new-release", "opera", "pagode", "party", "philippines-opm", "piano", "pop", "pop-film", 
        "post-dubstep", "power-pop", "progressive-house", "psych-rock", "punk", "punk-rock", "r-n-b", "rainy-day", 
        "reggae", "reggaeton", "road-trip", "rock", "rock-n-roll", "rockabilly", "romance", "sad", "salsa", "samba", 
        "sertanejo", "show-tunes", "singer-songwriter", "ska", "sleep", "songwriter", "soul", "soundtracks", "spanish", 
        "study", "summer", "swedish", "synth-pop", "tango", "techno", "trance", "trip-hop", "turkish", "work-out", 
        "world-music"
    };

    // Method to suggest genres based on input parameters (speed, location, temperature)
    public static List<String> suggestGenres(float speed, String location, float temperature) {
        List<String> suggestedGenres = new ArrayList<>();
        
        // Rule 1: Based on speed (m/s)
        if (speed <= 2) { // Slow movement like resting or light walking
            suggestedGenres.add("acoustic");
            suggestedGenres.add("ambient");
            suggestedGenres.add("chill");
        } else if (speed <= 5) { // Light jogging or moderate walking
            suggestedGenres.add("indie-pop");
            suggestedGenres.add("pop");
            suggestedGenres.add("alt-rock");
        } else if (speed <= 10) { // Running or fast walking
            suggestedGenres.add("edm");
            suggestedGenres.add("electronic");
            suggestedGenres.add("hip-hop");
        } else if (speed <= 20) { // Biking or brisk running
            suggestedGenres.add("progressive-house");
            suggestedGenres.add("techno");
            suggestedGenres.add("trance");
        } else if (speed <= 50) { // High-speed driving
            suggestedGenres.add("drum-and-bass");
            suggestedGenres.add("dubstep");
            suggestedGenres.add("electro");
        } else if (speed <= 100) { // Racing or extreme speed
            suggestedGenres.add("metal");
            suggestedGenres.add("hard-rock");
            suggestedGenres.add("hardstyle");
        } else { // Speed over 100 m/s, like airplanes
            suggestedGenres.add("industrial");
            suggestedGenres.add("trip-hop");
            suggestedGenres.add("techno");
        }
        
        // Rule 2: Based on location
        Map<String, String> locationGenres = getLocationGenres();
        
        if (locationGenres.containsKey(location)) {
            suggestedGenres.add(locationGenres.get(location));
        } else {
            // Default genre for unlisted cities or countries
            suggestedGenres.add("pop");
        }

        // Rule 3: Based on temperature (Celsius)
        if (temperature > 30) { // Hot weather
            suggestedGenres.add("summer");
            suggestedGenres.add("tropical");
        } else if (temperature < 10) { // Cold weather
            suggestedGenres.add("blues");
            suggestedGenres.add("classical");
        } else { // Moderate weather
            suggestedGenres.add("pop");
            suggestedGenres.add("folk");
        }

        return suggestedGenres;
    }

    // Method to create a map of location-based genres (major cities and countries)
    public static Map<String, String> getLocationGenres() {
        Map<String, String> locationGenres = new HashMap<>();
        
        locationGenres.put("New York", "hip-hop");
        locationGenres.put("Los Angeles", "rock");
        locationGenres.put("London", "british");
        locationGenres.put("Paris", "romance");
        locationGenres.put("Tokyo", "j-pop");
        locationGenres.put("Mumbai", "bollywood");
        locationGenres.put("Rio de Janeiro", "samba");
        locationGenres.put("Berlin", "techno");
        locationGenres.put("Sydney", "indie-pop");
        locationGenres.put("Seoul", "k-pop");
        locationGenres.put("Melbourne", "indie");
        locationGenres.put("Canberra", "classical");
        locationGenres.put("Beijing", "mandopop");
        locationGenres.put("Moscow", "classical");
        locationGenres.put("Mexico City", "latin");
        locationGenres.put("USA", "rock");
        locationGenres.put("India", "indian");
        locationGenres.put("Brazil", "brazil");
        locationGenres.put("Germany", "german");
        locationGenres.put("France", "french");
        locationGenres.put("UK", "british");
        locationGenres.put("China", "mandopop");
        locationGenres.put("Mexico", "latin");
        locationGenres.put("Canada", "country");
        locationGenres.put("Japan", "j-pop");
        locationGenres.put("South Korea", "k-pop");
        locationGenres.put("Australia", "indie");
        locationGenres.put("Italy", "opera");
        locationGenres.put("Spain", "salsa");
        locationGenres.put("Russia", "classical");
        locationGenres.put("Turkey", "turkish");
        locationGenres.put("South Africa", "afrobeat");
        locationGenres.put("Argentina", "tango");
        locationGenres.put("Egypt", "arabic");
        locationGenres.put("Nigeria", "afrobeat");
        locationGenres.put("Sweden", "swedish");
        locationGenres.put("Greece", "greek");
        locationGenres.put("Philippines", "philippines-opm");

        return locationGenres;
    }

    public static void main(String[] args) {
        // Example usage
        List<String> genres = suggestGenres(15.0f, "Melbourne", 25.0f);
        System.out.println("Suggested Genres: " + genres);
    }
}
