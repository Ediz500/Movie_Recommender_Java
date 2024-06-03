package org.example.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.security.auth.login.LoginContext;
import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Register extends User {
    @FXML
    TextField Username;
    @FXML
    TextField Password;
    @FXML
    TextField Name;
    @FXML
    TextField Surname;
    @FXML
    Label warning;
    @FXML
    Hyperlink Login;
    @FXML
    Button Register;
    @FXML
    Hyperlink HaveAccount;
    private static final String USER_DATA_FILE = "users.json";
    private static final Map<String, UserInfo> registeredUsers = new HashMap<>();
    private static final Map<String, Integer> userIds = new HashMap<>();
    private static final Set<Integer> usedIds = new HashSet<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void exit() throws IOException {
        Stage stage1 = (Stage) warning.getScene().getWindow();
        stage1.close();
    }
    public void registerUser() throws IOException {
        String username = Username.getText();
        String password = Password.getText();
        String name = Name.getText();
        String surname = Surname.getText();
        if (username.equals("") || password.equals("") || name.equals("") || surname.equals("")) {
            warning.setText("Please fill all the fields.");
        } else {
            warning.setText(registerUser(username, password, name, surname));
            exit();

        }

    }
}
