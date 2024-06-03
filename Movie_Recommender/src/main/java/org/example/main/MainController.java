package org.example.main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController  {
    @FXML
    TextField Username;
    @FXML
    PasswordField Password;
    @FXML
    Label warning;
    @FXML
    Button Login;
    @FXML
    Hyperlink Register;
    @FXML
    Hyperlink DontHaveAccount;
    static int UserID;

    static int RatedMovieCount;

    static String UserNAME;
    public static List<Integer> topRatedMovies;
    public static Map<Integer, String> movieTitles2 = new HashMap<>();

    public static Set<Integer> alreadyRatedMovies = new HashSet<>(); // Keep track of movies already rated
    public static List<Integer> ratedMovies = new ArrayList<>();
    public static List<Double> movierates = new ArrayList<>();




    public void register() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("register.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();
    }
    public void login() throws IOException {
        if(userControl()) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("mainpage.fxml"));
            Stage stage = (Stage)warning.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load(), 604, 400);
            stage.setTitle("Mainpage");
            stage.setScene(scene);
            stage.show();
        }
    }
    public boolean userControl() {
        if(User.loginUser(Username.getText(), Password.getText())) {
            UserID = User.getUserId(Username.getText());
            UserNAME = User.getName(Username.getText());
            alreadyRatedMovies.clear();
            ratedMovies.clear();
            movierates.clear();
            RatedMovieCount=0;
            return true;
        } else {
            warning.setText("Username or password is incorrect.");
            return false;
        }
    }

}
