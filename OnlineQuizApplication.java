import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Scanner;

public class OnlineQuizApplication {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/QUIZZ";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Chethan@123";
    private static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Welcome to the Online Quiz Application!");
                System.out.println("1. Admin Login\n2. User Portal\n3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> adminLogin(scanner);
                    case 2 -> userPortal(scanner);
                    case 3 -> {
                        System.out.println("Exiting application. Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void adminLogin(Scanner scanner) {
        final String ADMIN_USERNAME = "admin";
        final String ADMIN_PASSWORD = "admin@123";

        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            System.out.println("Admin login successful!");
            adminMenu(scanner);
        } else {
            System.out.println("Invalid admin credentials.");
        }
    }

    private static void adminMenu(Scanner scanner) {
        try {
            while (true) {
                System.out.println("\nAdmin Menu:");
                System.out.println("1. Add Quiz\n2. Add Question to Quiz\n3. Delete Question\n4. View All Quizzes\n5. Logout");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> addQuiz(scanner);
                    case 2 -> addQuestion(scanner);
                    case 3 -> deleteQuestion(scanner);
                    case 4 -> viewAllQuizzes();
                    case 5 -> {
                        System.out.println("Logging out as Admin.");
                        return;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userPortal(Scanner scanner) {
        while (true) {
            System.out.println("\nUser Portal:");
            System.out.println("1. Register\n2. Login\n3. Exit to Main Menu");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerUser(scanner);
                case 2 -> loginUser(scanner);
                case 3 -> {
                    System.out.println("Returning to Main Menu.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO uss (username, password_hash) VALUES (?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            stmt.executeUpdate();
            System.out.println("User registered successfully!");
        } catch (SQLException e) {
            System.out.println("Error: Username already exists or invalid data.");
        }
    }

    private static void loginUser(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT user_id FROM uss WHERE username = ? AND password_hash = ?")) {
            stmt.setString(1, username);
            stmt.setString(2, hashPassword(password));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                System.out.println("Login successful!");
                userMenu(scanner, userId);
            } else {
                System.out.println("Invalid username or password.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void userMenu(Scanner scanner, int userId) {
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. Take Quiz\n2. View Quiz History\n3. Logout");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> takeQuiz(scanner, userId);
                case 2 -> viewQuizHistory(userId);
                case 3 -> {
                    System.out.println("Logging out.");
                    return;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void addQuiz(Scanner scanner) throws SQLException {
        System.out.print("Enter the title of the new quiz: ");
        String title = scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO quizzess (title) VALUES (?)")) {
            stmt.setString(1, title);
            stmt.executeUpdate();
            System.out.println("Quiz added successfully!");
        }
    }

    private static void addQuestion(Scanner scanner) throws SQLException {
        viewAllQuizzes();
        System.out.print("Enter Quiz ID to add questionss to: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Enter the question text: ");
        String questionText = scanner.nextLine();
        System.out.print("Enter option 1: ");
        String option1 = scanner.nextLine();
        System.out.print("Enter option 2: ");
        String option2 = scanner.nextLine();
        System.out.print("Enter option 3: ");
        String option3 = scanner.nextLine();
        System.out.print("Enter option 4: ");
        String option4 = scanner.nextLine();
        System.out.print("Enter the correct option (1-4): ");
        int correctOption = scanner.nextInt();

        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO questionss (quiz_id, question_text, option1, option2, option3, option4, correct_option) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, quizId);
            stmt.setString(2, questionText);
            stmt.setString(3, option1);
            stmt.setString(4, option2);
            stmt.setString(5, option3);
            stmt.setString(6, option4);
            stmt.setInt(7, correctOption);
            stmt.executeUpdate();
            System.out.println("Question added successfully!");
        }
    }

    private static void deleteQuestion(Scanner scanner) throws SQLException {
        viewAllQuizzes();
        System.out.print("Enter Quiz ID to delete questionss from: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();

        try (PreparedStatement stmt = connection.prepareStatement("SELECT question_id, question_text FROM questionss WHERE quiz_id = ?")) {
            stmt.setInt(1, quizId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                System.out.println(rs.getInt("question_id") + ". " + rs.getString("question_text"));
            }

            System.out.print("Enter the Question ID to delete: ");
            int questionId = scanner.nextInt();
            try (PreparedStatement deleteStmt = connection.prepareStatement("DELETE FROM questionss WHERE question_id = ?")) {
                deleteStmt.setInt(1, questionId);
                deleteStmt.executeUpdate();
                System.out.println("Question deleted successfully!");
            }
        }
    }

    private static void viewAllQuizzes() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM quizzess")) {

            System.out.println("Available Quizzes:");
            while (rs.next()) {
                System.out.println(rs.getInt("quiz_id") + ". " + rs.getString("title"));
            }
        }
    }

private static void takeQuiz(Scanner scanner, int userId) {
    try (Statement stmt = connection.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT quiz_id, title FROM quizzess")) {

        System.out.println("Available Quizzes:");
        while (rs.next()) {
            System.out.println(rs.getInt("quiz_id") + ". " + rs.getString("title"));
        }

        System.out.print("Enter Quiz ID to take: ");
        int quizId = scanner.nextInt();
        scanner.nextLine();

        // Check if there are any questions for the selected quiz
        int totalQuestions = 0;
        try (PreparedStatement countStmt = connection.prepareStatement("SELECT COUNT(*) AS question_count FROM questionss WHERE quiz_id = ?")) {
            countStmt.setInt(1, quizId);
            ResultSet countRs = countStmt.executeQuery();
            if (countRs.next()) {
                totalQuestions = countRs.getInt("question_count");
            }
        }

        if (totalQuestions == 0) {
            System.out.println("Sorry, no questions are available for this quiz.");
            return;
        }

        // Proceed with the quiz if questions are available
        int score = 0;

        try (PreparedStatement stmt2 = connection.prepareStatement("SELECT * FROM questionss WHERE quiz_id = ?")) {
            stmt2.setInt(1, quizId);
            ResultSet rs2 = stmt2.executeQuery();

            while (rs2.next()) {
                System.out.println(rs2.getString("question_text"));
                System.out.println("1. " + rs2.getString("option1"));
                System.out.println("2. " + rs2.getString("option2"));
                System.out.println("3. " + rs2.getString("option3"));
                System.out.println("4. " + rs2.getString("option4"));
                System.out.print("Enter your answer (1-4): ");
                int answer = scanner.nextInt();
                scanner.nextLine();

                if (answer == rs2.getInt("correct_option")) {
                    score++;
                }
            }
        }

        System.out.println("Quiz completed!");
        System.out.println("Your score: " + score + "/" + totalQuestions);

        // Save quiz attempt to history
        try (PreparedStatement saveStmt = connection.prepareStatement(
                "INSERT INTO quiz_historyy (user_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)")) {
            saveStmt.setInt(1, userId);
            saveStmt.setInt(2, quizId);
            saveStmt.setInt(3, score);
            saveStmt.setInt(4, totalQuestions);
            saveStmt.executeUpdate();
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    private static void viewQuizHistory(int userId) {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT quizzess.title, quiz_historyy.score, quiz_historyy.total_questions " +
                        "FROM quiz_historyy " +
                        "JOIN quizzess ON quiz_historyy.quiz_id = quizzess.quiz_id " +
                        "WHERE quiz_historyy.user_id = ?")) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Your Quiz History:");
            while (rs.next()) {
                System.out.printf("Quiz: %s | Score: %d/%d%n",
                        rs.getString("title"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}

