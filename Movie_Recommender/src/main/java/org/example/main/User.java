package org.example.main;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class User {
    private static final String USER_DATA_FILE = "users.json";
    private static final Map<String, UserInfo> registeredUsers = new HashMap<>();
    private static final Map<String, Integer> userIds = new HashMap<>();
    private static final Set<Integer> usedIds = new HashSet<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        loadUserData();
    }

    private static void loadUserData() {
        try (Reader reader = new FileReader(USER_DATA_FILE)) {
            UserData userData = gson.fromJson(reader, UserData.class);
            if (userData != null) {
                Map<String, UserInfo> userInfoData = userData.getRegisteredUsers();
                Map<String, Integer> userIdsData = userData.getUserIds();
                if (userInfoData != null && userIdsData != null) {
                    registeredUsers.putAll(userInfoData);
                    userIds.putAll(userIdsData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveUserData() {
        UserData userData = new UserData(registeredUsers, userIds);
        try (Writer writer = new FileWriter(USER_DATA_FILE)) {
            gson.toJson(userData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String registerUser(String username, String password, String name, String surname) {
        if (registeredUsers.containsKey(username)) {
            return "Username already exists. Please choose another username.";
        }
        String hashedPassword = hashPassword(password);
        registeredUsers.put(username, new UserInfo(hashedPassword, name, surname));
        String userId = generateUserId(username);
        userIds.put(username, Integer.parseInt(userId));
        saveUserData(); // Save user data after registration
        return "User registration successful. Your user ID is: " + userId;
    }

    public static boolean loginUser(String username, String password) {
        if (!registeredUsers.containsKey(username)) {
            return false; // Username does not exist
        }
        String hashedPassword = hashPassword(password);
        return registeredUsers.get(username).getPassword().equals(hashedPassword);
    }

    public static String getName(String username) {
        UserInfo userInfo = registeredUsers.get(username);
        if (userInfo != null) {
            return userInfo.getName() + " " + userInfo.getSurname();
        }
        return null;
    }

    public static int getUserId(String username) {
        return userIds.get(username);
    }
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateUserId(String username) {
        int id = Math.abs(username.hashCode()); // Generate ID from username's hash code
        while (usedIds.contains(id)) {
            // If ID is already used, generate a new one by adding 1
            id++;
        }
        usedIds.add(id);
        return Integer.toString(id);
    }

    private static class UserData {
        private Map<String, UserInfo> registeredUsers;
        private Map<String, Integer> userIds;

        public UserData(Map<String, UserInfo> registeredUsers, Map<String, Integer> userIds) {
            this.registeredUsers = registeredUsers;
            this.userIds = userIds;
        }

        public Map<String, UserInfo> getRegisteredUsers() {
            return registeredUsers;
        }

        public Map<String, Integer> getUserIds() {
            return userIds;
        }
    }

    public static class UserInfo {
        private String password;
        private String name;
        private String surname;

        public UserInfo(String password, String name, String surname) {
            this.password = password;
            this.name = name;
            this.surname = surname;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }

        public String getSurname() {
            return surname;
        }
    }
}