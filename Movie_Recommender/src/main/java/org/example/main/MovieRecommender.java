package org.example.main;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;



public class MovieRecommender {
    public static Map<Integer, Map<Integer, Double>> userItemMatrix = new HashMap<>();
    public static Map<Integer, String> movieTitles = new HashMap<>();
    public static int userCount = 0; // Track the number of users

    public MovieRecommender() {

    }

    public static Map<Integer, String> imdbIds = new HashMap<>();

    public static void loadRatingsFromCSV(String filename) {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] line;
            boolean headerSkipped = false;
            while ((line = reader.readNext()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue; // Skip the header line
                }
                int movieId = Integer.parseInt(line[0].trim()); // Trim whitespace from movie ID
                int userId = Integer.parseInt(line[3].trim()); // Trim whitespace from user ID
                double rating = Double.parseDouble(line[4].trim()); // Trim whitespace from rating
                // Store movie titles
                movieTitles.put(movieId, line[1].trim()); // Trim whitespace from movie title

                // Store IMDb IDs
                imdbIds.put(movieId, line[5].trim()); // Assuming IMDb ID is in the 6th column

                userItemMatrix.computeIfAbsent(userId, k -> new HashMap<>()).put(movieId, rating);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    public int registerNewUser(Scanner scanner) {


        int userId = ++userCount; // Assign a unique user ID

        List<Integer> topRatedMovies = getTopRatedMovies(100); // Get top 100 rated movies after 1980
        List<Integer> ratedMovies = new ArrayList<>();
        List<Double> ratings = new ArrayList<>();

        Set<Integer> alreadyRatedMovies = new HashSet<>(); // Keep track of movies already rated

        System.out.println("Please rate the following movies (scale: 1-5), or enter -1 to skip:");
        int count = 0;
        Random random = new Random();
        while (count < 10) {
            // Generate a random index to select a movie from the top rated movies
            int randomIndex = random.nextInt(topRatedMovies.size());
            int movieId = topRatedMovies.get(randomIndex);

            // Check if the movie has already been rated or is from before the 1980s
            if (alreadyRatedMovies.contains(movieId) || !isAfter1970(movieTitles.get(movieId))) {
                continue;
            }

            // Print movie title and get rating from user
            String movieTitle = movieTitles.get(movieId);
            System.out.print("Enter rating for movie \"" + movieTitle + "\": ");
            double rating;
            rating = scanner.nextInt();


            if (rating >= 1 && rating <= 5) {
                ratedMovies.add(movieId);
                ratings.add(rating);
                count++;

                // Add rated movie to the set of already rated movies
                alreadyRatedMovies.add(movieId);
            } else if (rating == -1) {
                // Add movie to the set of already rated movies even if skipped
                alreadyRatedMovies.add(movieId);
                continue;
            } else {
                System.out.println("Invalid rating. Please rate between 1 to 5.");
            }
        }

        // Add user ratings to user-item matrix
        userItemMatrix.put(userId, new HashMap<>());
        for (int i = 0; i < ratedMovies.size(); i++) {
            int movieId = ratedMovies.get(i);
            double rating = ratings.get(i);
            userItemMatrix.get(userId).put(movieId, rating);
        }

        return userId;
    }

    public static boolean isAfter1970(String movieTitle) {
        // Extract the year from the movie title
        String yearString = movieTitle.substring(movieTitle.length() -5 , movieTitle.length() - 1);
        int year = Integer.parseInt(yearString);

        // Check if the year is after 1980
        return year > 1969;
    }

    public static List<Integer> getTopRatedMovies(int n) {
        int minRatingsThreshold = 110;
        List<Map.Entry<Integer, Double>> sortedMovies;

        do {
            // Map to store the total rating and count of ratings for each movie
            Map<Integer, List<Double>> movieRatings = new HashMap<>();

            // Iterate over each user's ratings
            for (Map.Entry<Integer, Map<Integer, Double>> userRatings : userItemMatrix.entrySet()) {
                for (Map.Entry<Integer, Double> ratingEntry : userRatings.getValue().entrySet()) {
                    int movieId = ratingEntry.getKey();
                    double rating = ratingEntry.getValue();

                    // Update the movie's total rating and count of ratings
                    movieRatings.computeIfAbsent(movieId, k -> new ArrayList<>(Arrays.asList(0.0, 0.0)));
                    List<Double> ratingData = movieRatings.get(movieId);
                    ratingData.set(0, ratingData.get(0) + rating); // Total rating
                    ratingData.set(1, ratingData.get(1) + 1); // Count of ratings
                }
            }

            // List to store movies with average ratings
            sortedMovies = new ArrayList<>();

            // Calculate the average rating for each movie with more than the threshold number of ratings
            for (Map.Entry<Integer, List<Double>> entry : movieRatings.entrySet()) {
                int movieId = entry.getKey();
                double totalRating = entry.getValue().get(0);
                double countRating = entry.getValue().get(1);

                if (countRating >= minRatingsThreshold) {
                    double averageRating = totalRating / countRating;
                    sortedMovies.add(Map.entry(movieId, averageRating));
                }
            }

            // Sort the movies by their average ratings in descending order
            sortedMovies.sort((m1, m2) -> Double.compare(m2.getValue(), m1.getValue()));

            // Decrease the threshold if not enough movies are found
            minRatingsThreshold--;

        } while (sortedMovies.size() == n && minRatingsThreshold > 0);

        // Get the top `n` movies released after 1980
        List<Integer> topRatedMovies = new ArrayList<>();
        for (int i = 0; i < Math.min(n, sortedMovies.size()); i++) {
            int movieId = sortedMovies.get(i).getKey();
            if (isAfter1970(movieTitles.get(movieId))) {
                topRatedMovies.add(movieId);
            }
        }

        return topRatedMovies;
    }




    public static List<Integer> generateRecommendations(int userId) {
        List<Integer> recommendedMovies = new ArrayList<>();

        Map<Integer, Double> userRatings = userItemMatrix.get(userId);

        if (userRatings != null) {
            Map<Integer, Double> similarities = new HashMap<>();
            for (Map.Entry<Integer, Map<Integer, Double>> entry : userItemMatrix.entrySet()) {

                int otherUserId = entry.getKey();
                if (otherUserId != userId) { // Exclude the current user
                    Map<Integer, Double> otherUserRatings = entry.getValue();
                    double similarity = calculateCosineSimilarity(userRatings, otherUserRatings);
                    // System.out.println(similarity);
                    similarities.put(otherUserId, similarity);
                }

            }
            List<Map.Entry<Integer, Double>> sortedSimilarities = new ArrayList<>(similarities.entrySet());
            sortedSimilarities.sort((s1, s2) -> Double.compare(s2.getValue(), s1.getValue())); // Sort by similarity in descending order

            for (Map.Entry<Integer, Double> similarityEntry : sortedSimilarities) {
                int otherUserId = similarityEntry.getKey();
                Map<Integer, Double> otherUserRatings = userItemMatrix.get(otherUserId);
                for (Map.Entry<Integer, Double> ratingEntry : otherUserRatings.entrySet()) {
                    int movieId = ratingEntry.getKey();
                    if (!userRatings.containsKey(movieId)) { // Exclude movies already rated by the user
                        recommendedMovies.add(movieId);
                    }
                    if (recommendedMovies.size() >= 10) { // Limit recommendations to 10 movies
                        break;
                    }
                }
                if (recommendedMovies.size() >= 10) {
                    break;
                }
            }
        }
        System.out.println("matrix2"+userItemMatrix.get(userId));
        return recommendedMovies;
    }

    static double calculateCosineSimilarity(Map<Integer, Double> userRatings1, Map<Integer, Double> userRatings2) {
        // Compute dot product
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

        // Avoid division by zero
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }

        // Compute cosine similarity
        return dotProduct / (norm1 * norm2);
    }


        public static void main(String[] args) {
            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to Movie Recommendation App!");

            System.out.println("Do you want to register? (yes/no)");
            String registerChoice = scanner.nextLine().trim();
            if (registerChoice.equalsIgnoreCase("yes")) {
                System.out.println("Please choose a username:");
                String username = scanner.nextLine().trim();
                System.out.println("Please choose a password:");
                String password = scanner.nextLine().trim();
                System.out.println("Please provide your name:");
                String name = scanner.nextLine().trim();
                System.out.println("Please provide your surname:");
                String surname = scanner.nextLine().trim();

                String registrationResult = User.registerUser(username, password, name, surname);
                System.out.println(registrationResult);
                MovieRecommender recommender = new MovieRecommender();
                loadRatingsFromCSV("src/main/dataset/merged_movies_small.csv");
                List<Integer> recommendations = generateRecommendations(MainController.UserID);
                System.out.println("Recommended movies based on your ratings:");
                for (int i = 0; i < recommendations.size(); i++) {
                    int movieId = recommendations.get(i);
                    String movieTitle = movieTitles.get(movieId);
                    System.out.println((i + 1) + ": " + movieTitle);
                }
            } else {
                System.out.println("Please login:");
                System.out.println("Username:");
                String username = scanner.nextLine().trim();
                System.out.println("Password:");
                String password = scanner.nextLine().trim();

                if (User.loginUser(username, password)) {
                    String fullName = User.getName(username);
                    if (fullName != null) {
                        System.out.println("Welcome back, " + fullName + "!");
                        MovieRecommender recommender = new MovieRecommender();
                        loadRatingsFromCSV("src/main/dataset/merged_movies_small.csv");



                        int userId = recommender.registerNewUser(scanner);

                        List<Integer> recommendations = generateRecommendations(userId);
                        System.out.println("Recommended movies based on your ratings:");
                        for (int i = 0; i < recommendations.size(); i++) {
                            int movieId = recommendations.get(i);
                            String movieTitle = movieTitles.get(movieId);
                            System.out.println((i + 1) + ": " + movieTitle);
                        }
                    } else {
                        System.out.println("Welcome back!");
                    }
                } else {
                    System.out.println("Invalid username or password.");
                }
            }

            scanner.close();
        }
    }