package org.example.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RateMovie extends MovieRecommender implements Initializable {
    @FXML
    Button star1;
    @FXML
    Button star2;
    @FXML
    Button star3;
    @FXML
    Button star4;
    @FXML
    Button star5;
    @FXML
    Button DNW;
    @FXML
    Label movieName;

    static double movierate = 0;

    static int movieId;
    static int movieid;

    public static Map<Integer, String> movieTitles2 = new HashMap<>();



    public void setmovierate1() {
        movierate = 1;
        rateMovie(movierate);
        try {
            refresh();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setmovierate2() {
        movierate = 2;
        rateMovie(movierate);
        try {
            refresh();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void setmovierate3() {
        movierate = 3;
        rateMovie(movierate);
        try {
            refresh();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void setmovierate4() {
        movierate = 4;
        rateMovie(movierate);
        try {
            refresh();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void setmovierate5() {
        movierate = 5;
        rateMovie(movierate);
        try {
            refresh();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    public void setmovierateDNW() {
        movierate = -1;
        rateMovie(movierate);
        try {
            refresh();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void rateMovie(double movierate){
        if (movierate >= 1 && movierate <= 5) {

            MainController.ratedMovies.add(movieid);
            MainController.movierates.add(movierate);
            // Add rated movie to the set of already rated movies
            MainController.alreadyRatedMovies.add(movieid);
            MainController.RatedMovieCount+=1;
        } else if (movierate == -1) {
            // Add movie to the set of already rated movies even if skipped
            MainController.alreadyRatedMovies.add(movieid);
    }
    }


    public String movieName() {
        Random random = new Random();
        int randomIndex = random.nextInt(MainController.topRatedMovies.size());
        movieId = MainController.topRatedMovies.get(randomIndex);
        movieid = movieId;

        if (MainController.alreadyRatedMovies.size()==100){
            star1.setDisable(true);
            star2.setDisable(true);
            star3.setDisable(true);
            star4.setDisable(true);
            star5.setDisable(true);
            DNW.setDisable(true);
            return "You Already Voted Top 100 Movies";
        }
        else if (MainController.alreadyRatedMovies.contains(movieId) ) {
            return movieName();
        }
        else {

            String movieTitle = MainController.movieTitles2.get(movieId);


            return movieTitle;
        }
    }

    public void refresh() throws IOException {
        Stage stage1 = (Stage) movieName.getScene().getWindow();
        stage1.close();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ratepage.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Rating");
        stage.setScene(scene);
        stage.show();
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        movieName.setText(movieName());



        star1.setOnMouseEntered(e -> {
            star1.setStyle("-fx-background-color: gold");

        });
        star1.setOnMouseExited(e -> {

            star1.setStyle("-fx-border-color: #ffffff");
            star2.setStyle("-fx-border-color: #ffffff");
            star3.setStyle("-fx-border-color: #ffffff");
            star4.setStyle("-fx-border-color: #ffffff");
            star5.setStyle("-fx-border-color: #ffffff");
        });
        star2.setOnMouseEntered(e -> {
            star1.setStyle("-fx-background-color: gold");
            star2.setStyle("-fx-background-color: gold");

        });
        star2.setOnMouseExited(e -> {

            star1.setStyle("-fx-border-color: #ffffff");
            star2.setStyle("-fx-border-color: #ffffff");
            star3.setStyle("-fx-border-color: #ffffff");
            star4.setStyle("-fx-border-color: #ffffff");
            star5.setStyle("-fx-border-color: #ffffff");
        });
        star3.setOnMouseEntered(e -> {
            star1.setStyle("-fx-background-color: gold");
            star2.setStyle("-fx-background-color: gold");
            star3.setStyle("-fx-background-color: gold");

        });
        star3.setOnMouseExited(e -> {

            star1.setStyle("-fx-border-color: #ffffff");
            star2.setStyle("-fx-border-color: #ffffff");
            star3.setStyle("-fx-border-color: #ffffff");
            star4.setStyle("-fx-border-color: #ffffff");
            star5.setStyle("-fx-border-color: #ffffff");
        });
        star4.setOnMouseEntered(e -> {
            star1.setStyle("-fx-background-color: gold");
            star2.setStyle("-fx-background-color: gold");
            star3.setStyle("-fx-background-color: gold");
            star4.setStyle("-fx-background-color: gold");

        });
        star4.setOnMouseExited(e -> {

            star1.setStyle("-fx-border-color: #ffffff");
            star2.setStyle("-fx-border-color: #ffffff");
            star3.setStyle("-fx-border-color: #ffffff");
            star4.setStyle("-fx-border-color: #ffffff");
            star5.setStyle("-fx-border-color: #ffffff");
        });
        star5.setOnMouseEntered(e -> {
            star1.setStyle("-fx-background-color: gold");
            star2.setStyle("-fx-background-color: gold");
            star3.setStyle("-fx-background-color: gold");
            star4.setStyle("-fx-background-color: gold");
            star5.setStyle("-fx-background-color: gold");

        });
        star5.setOnMouseExited(e -> {

            star1.setStyle("-fx-border-color: #ffffff");
            star2.setStyle("-fx-border-color: #ffffff");
            star3.setStyle("-fx-border-color: #ffffff");
            star4.setStyle("-fx-border-color: #ffffff");
            star5.setStyle("-fx-border-color: #ffffff");

        });

    }
}
