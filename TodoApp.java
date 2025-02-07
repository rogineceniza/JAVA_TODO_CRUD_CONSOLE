import java.sql.*;
import java.util.Scanner;

public class TodoApp {
    private Connection connection;
    private Scanner scanner;

    public TodoApp() {
        try {
            connection = DatabaseConfig.getConnection();
            scanner = new Scanner(System.in);
            createTable();
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
            System.exit(1);
        }
    }

    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INT AUTO_INCREMENT PRIMARY KEY,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                status VARCHAR(20) DEFAULT 'pending'
            )
        """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

        public void addTask(String title, String description) {
            String sql = "INSERT INTO tasks (title, description) VALUES (?, ?)";
            
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                pstmt.setString(1, title);
                pstmt.setString(2, description);
                pstmt.executeUpdate();
                System.out.println("Task added successfully!");
            } catch (SQLException e) {
                System.out.println("Error adding task: " + e.getMessage());
            }
        }

    public void viewTasks() {
        String sql = "SELECT * FROM tasks";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (!rs.isBeforeFirst()) {
                System.out.println("No tasks found!");
                return;
            }

            System.out.println("\nYour Tasks:");
            System.out.println("-".repeat(50));
            
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Description: " + rs.getString("description"));
                System.out.println("Status: " + rs.getString("status"));
                System.out.println("-".repeat(50));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing tasks: " + e.getMessage());
        }
    }

    public void editTask(int taskId, String title, String description) {
        String sql = "UPDATE tasks SET title = ?, description = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, description);
            pstmt.setInt(3, taskId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Task not found!");
            } else {
                System.out.println("Task updated successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error updating task: " + e.getMessage());
        }
    }

    public void deleteTask(int taskId) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, taskId);
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("Task not found!");
            } else {
                System.out.println("Task deleted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting task: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (scanner != null) {
                scanner.close();
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }

    public void run() {
        while (true) {
            System.out.println("\n=== TODO Application ===");
            System.out.println("1. Add Task");
            System.out.println("2. View Tasks");
            System.out.println("3. Edit Task");
            System.out.println("4. Delete Task");
            System.out.println("5. Exit");
            
            System.out.print("Enter your choice (1-5): ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();
                    addTask(title, description);
                    
                    break;
                    
                case "2":
                    viewTasks();
                    break;
                    
                case "3":
                    System.out.print("Enter task ID to edit: ");
                    int editId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter new title: ");
                    String newTitle = scanner.nextLine();
                    System.out.print("Enter new description: ");
                    String newDescription = scanner.nextLine();
                    editTask(editId, newTitle, newDescription);
                    break;
                    
                case "4":
                    System.out.print("Enter task ID to delete: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());
                    deleteTask(deleteId);
                    break;
                    
                case "5":
                    close();
                    System.out.println("Goodbye!");
                    return;
                    
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    public static void main(String[] args) {
        TodoApp app = new TodoApp();
        app.run();
    }
} 