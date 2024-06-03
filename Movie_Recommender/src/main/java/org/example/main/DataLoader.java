package org.example.main;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataLoader {

    public Map<Integer, Map<Integer, Double>> loadRatings(String ratingsFile) {
        Map<Integer, Map<Integer, Double>> userItemMatrix = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(ratingsFile))) {
            String[] line;
            boolean headerSkipped = false;
            while ((line = reader.readNext()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header line
                }
                int userId = Integer.parseInt(line[3]);
                int movieId = Integer.parseInt(line[0]);
                double rating = Double.parseDouble(line[4]);

                // Add rating to user-item matrix
                userItemMatrix.putIfAbsent(userId, new HashMap<>());
                userItemMatrix.get(userId).put(movieId, rating);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        return userItemMatrix;
    }

    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        Map<Integer, Map<Integer, Double>> userItemMatrix = dataLoader.loadRatings("src/main/dataset/merged_movies_small.csv");

        // Print the user-item matrix (for testing)
        for (Map.Entry<Integer, Map<Integer, Double>> entry : userItemMatrix.entrySet()) {
            int userId = entry.getKey();
            Map<Integer, Double> ratings = entry.getValue();
            System.out.println("User " + userId + ": " + ratings);
        }
    }
}
