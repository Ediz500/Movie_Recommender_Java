package org.example.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Mainpage implements Initializable {

    @FXML
    Hyperlink Movie1;
    @FXML
    Hyperlink Movie2;
    @FXML
    Hyperlink Movie3;
    @FXML
    Hyperlink Movie4;
    @FXML
    public Label User;
    @FXML
    Button Rate;
    @FXML
    Hyperlink Movie5;
    @FXML
    Hyperlink Movie6;
    @FXML
    Hyperlink Movie7;
    @FXML
    Hyperlink Movie8;
    @FXML
    Hyperlink Movie9;
    @FXML
    Hyperlink Movie10;
    List<Integer> recommendations;

    public List<Integer> generateRecommendations(int userId) {
        List<Integer> recommendedMovies = new ArrayList<>();
        Map<Integer, Double> userRatings = MovieRecommender.userItemMatrix.get(userId);

        if (userRatings != null) {
            Map<Integer, Double> similarities = new HashMap<>();
            for (Map.Entry<Integer, Map<Integer, Double>> entry : MovieRecommender.userItemMatrix.entrySet()) {
                int otherUserId = entry.getKey();
                if (otherUserId != userId) { // Exclude the current user
                    Map<Integer, Double> otherUserRatings = entry.getValue();
                    double similarity = calculateCosineSimilarity(userRatings, otherUserRatings);
                    similarities.put(otherUserId, similarity);
                }
            }

            Map<Integer, Double> weightedRatings = new HashMap<>();
            Map<Integer, Double> totalSimilarity = new HashMap<>();

            for (Map.Entry<Integer, Double> similarityEntry : similarities.entrySet()) {
                int otherUserId = similarityEntry.getKey();
                double similarity = similarityEntry.getValue();

                Map<Integer, Double> otherUserRatings = MovieRecommender.userItemMatrix.get(otherUserId);

                for (Map.Entry<Integer, Double> ratingEntry : otherUserRatings.entrySet()) {
                    int movieId = ratingEntry.getKey();
                    double rating = ratingEntry.getValue();

                    if (!userRatings.containsKey(movieId)) { // Exclude movies already rated by the user
                        weightedRatings.put(movieId, weightedRatings.getOrDefault(movieId, 0.0) + similarity * rating);
                        totalSimilarity.put(movieId, totalSimilarity.getOrDefault(movieId, 0.0) + similarity);
                    }
                }
            }

            Map<Integer, Double> predictedRatings = new HashMap<>();
            for (Map.Entry<Integer, Double> entry : weightedRatings.entrySet()) {
                int movieId = entry.getKey();
                double totalWeightedRating = entry.getValue();
                double totalSim = totalSimilarity.get(movieId);
                predictedRatings.put(movieId, totalWeightedRating / (totalSim+0.17));
            }

            List<Map.Entry<Integer, Double>> sortedPredictedRatings = new ArrayList<>(predictedRatings.entrySet());
            sortedPredictedRatings.sort((e1, e2) -> Double.compare(e2.getValue(), e1.getValue())); // Sort by adjusted rating in descending order

            for (Map.Entry<Integer, Double> entry : sortedPredictedRatings) {
                recommendedMovies.add(entry.getKey());
                if (recommendedMovies.size() >= 10) {
                    break;
                }
            }
        }
        List<Integer> sortedList = recommendedMovies.stream().sorted().collect(Collectors.toList());
        System.out.println(sortedList);
        return recommendedMovies;
    }




    private double calculateCosineSimilarity(Map<Integer, Double> userRatings1, Map<Integer, Double> userRatings2) {
        double dotProduct = 0;
        double norm1 = 0;
        double norm2 = 0;
        for (Map.Entry<Integer, Double> entry : userRatings1.entrySet()) {
            int movieId = entry.getKey();
            double rating1 = entry.getValue();
            double rating2 = userRatings2.getOrDefault(movieId, 0.0);
            dotProduct += rating1 * rating2;
            norm1 += Math.pow(rating1, 2);
        }
        for (double rating : userRatings2.values()) {
            norm2 += Math.pow(rating, 2);
        }
        norm1 = Math.sqrt(norm1);
        norm2 = Math.sqrt(norm2);

        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        return dotProduct / (norm1 * norm2);
    }

    public void rateMovie() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ratepage.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Rating");
        stage.setScene(scene);
        stage.show();
    }

    public void refresh() throws IOException {
        Stage stage1 = (Stage) User.getScene().getWindow();
        stage1.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("MainPage");
        stage.setScene(scene);
        stage.show();
    }

    private String generateIMDbUrl(String imdbId) {
        // Ensure the IMDb ID has 7 digits by padding with leading zeros if necessary
        return "https://www.imdb.com/title/tt" + String.format("%07d", Integer.parseInt(imdbId));
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MovieRecommender.loadRatingsFromCSV("src/main/dataset/merged_movies_small.csv");
        MainController.topRatedMovies = MovieRecommender.getTopRatedMovies(100);
        MainController.movieTitles2 = MovieRecommender.movieTitles;

        User.setText(MainController.UserNAME.toUpperCase());

        MovieRecommender.userItemMatrix.put(MainController.UserID, new HashMap<>());

        for (int i = 0; i < MainController.ratedMovies.size(); i++) {
            int movieId = MainController.ratedMovies.get(i);
            double rating = MainController.movierates.get(i);
            MovieRecommender.userItemMatrix.get(MainController.UserID).put(movieId, rating);
        }

        if (MainController.RatedMovieCount >= 10) {
            int userId = MainController.UserID;
            recommendations = generateRecommendations(userId);
            Stack<Integer> ratedMovieIds = new Stack<>();
            for (int i = 0; i < MainController.ratedMovies.size(); i++) {
                ratedMovieIds.push(MainController.ratedMovies.get(i));
            }

            // Remove rated movies from recommendations
            for (int i = 0; i < ratedMovieIds.size(); i++) {
                recommendations.remove(ratedMovieIds.pop());
            }

            if (!recommendations.isEmpty()) {
                setMovieHyperlink(Movie1, recommendations.get(0));
                setMovieHyperlink(Movie2, recommendations.get(1));
                setMovieHyperlink(Movie3, recommendations.get(2));
                setMovieHyperlink(Movie4, recommendations.get(3));
                setMovieHyperlink(Movie5, recommendations.get(4));
                setMovieHyperlink(Movie6, recommendations.get(5));
                setMovieHyperlink(Movie7, recommendations.get(6));
                setMovieHyperlink(Movie8, recommendations.get(7));
                setMovieHyperlink(Movie9, recommendations.get(8));
                setMovieHyperlink(Movie10, recommendations.get(9));
            } else {
                setNoRecommendations();
            }
        } else {
            setNoRecommendations();
        }
    }

    private void setMovieHyperlink(Hyperlink hyperlink, int movieId) {
        String movieTitle = MovieRecommender.movieTitles.get(movieId);
        String imdbId = MovieRecommender.imdbIds.get(movieId);
        String imdbUrl = generateIMDbUrl(imdbId);

        hyperlink.setText(movieTitle);
        hyperlink.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new URL(imdbUrl).toURI());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    private void setNoRecommendations() {
        Movie1.setText("No Recommendation Yet");
        Movie2.setText("No Recommendation Yet");
        Movie3.setText("No Recommendation Yet");
        Movie4.setText("No Recommendation Yet");
        Movie5.setText("No Recommendation Yet");
        Movie6.setText("No Recommendation Yet");
        Movie7.setText("No Recommendation Yet");
        Movie8.setText("No Recommendation Yet");
        Movie9.setText("No Recommendation Yet");
        Movie10.setText("No Recommendation Yet");
    }

}
