package ui;

import dao.UserDao;
import model.User;

import java.util.List;
import java.util.Scanner;

/** Administrator tools for reviewing users and approving registrations. */
public final class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;

    public AdminMenu(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\\n--- ADMIN MENU ---");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Logout");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> printUsers(userDao.getAllUsers());
                case "2" -> printUsers(userDao.getPendingUsers());
                case "3" -> approveUser();
                case "4" -> running = false;
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        System.out.println("Logged out successfully.");
    }

    private void approveUser() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("There are no pending registrations.");
            return;
        }
        System.out.println("\\nPending registrations:");
        printUsers(pending);
        System.out.print("Enter target User ID (0 to cancel): ");
        String input = scanner.nextLine().trim();
        if (input.equals("0")) {
            System.out.println("Approval cancelled.");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid User ID.");
            return;
        }

        boolean isPending = pending.stream().anyMatch(user -> user.id() == userId);
        if (!isPending) {
            System.out.println("That ID is not in the pending registration list.");
            return;
        }
        if (userDao.approveUser(userId)) {
            System.out.println("User account approved successfully.");
        } else {
            System.out.println("Approval failed. The account may no longer be pending.");
        }
    }

    /** Renders any user list with aligned ID, username, role, and status columns. */
    private void printUsers(List<User> users) {
        String border = "+" + "-".repeat(5) + "+" + "-".repeat(15) + "+"
                + "-".repeat(12) + "+" + "-".repeat(10) + "+";
        System.out.println(border);
        System.out.printf("|%-5s|%-15s|%-12s|%-10s|%n", "ID", "USERNAME", "ROLE", "STATUS");
        System.out.println(border);
        if (users.isEmpty()) {
            System.out.printf("|%-5s|%-15s|%-12s|%-10s|%n", "-", "No users found", "-", "-");
        } else {
            for (User user : users) {
                System.out.printf("|%-5s|%-15s|%-12s|%-10s|%n",
                        user.id(), shorten(user.username(), 15), shorten(user.roles(), 12),
                        shorten(user.status(), 10));
            }
        }
        System.out.println(border);
    }

    private String shorten(String value, int maxLength) {
        if (value == null) return "";
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
