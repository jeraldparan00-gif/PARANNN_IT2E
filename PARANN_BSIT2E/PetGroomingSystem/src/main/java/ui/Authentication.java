package ui;

import dao.UserDao;
import model.User;

import java.util.Optional;
import java.util.Scanner;

/** Handles registration, login, and routing to the user's role menu. */
public final class Authentication {
    private static final String[] ROLES = {"ADMIN", "GROOMER", "RECEPTIONIST"};
    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=====================================");
            System.out.println(" PET GROOMING APPOINTMENT & BILLING");
            System.out.println("=====================================");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> { System.out.println("Goodbye!"); running = false; }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void register() {
        System.out.println("\n--- Register Account ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) { System.out.println("Username cannot be empty."); return; }
        if (userDao.usernameExists(username)) { System.out.println("Username already taken."); return; }
        System.out.print("Password: ");
        String password = scanner.nextLine();
        if (password.isEmpty()) { System.out.println("Password cannot be empty."); return; }

        System.out.println("Select role:");
        for (int i = 0; i < ROLES.length; i++) System.out.println((i + 1) + ". " + ROLES[i]);
        System.out.print("Role: ");
        int index;
        try { index = Integer.parseInt(scanner.nextLine().trim()) - 1; }
        catch (NumberFormatException e) { index = -1; }
        if (index < 0 || index >= ROLES.length) { System.out.println("Invalid role."); return; }
        if (userDao.register(username, password, ROLES[index]))
            System.out.println("Account created! Status: PENDING (waiting for approval).");
        else System.out.println("Registration failed.");
    }

    private void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        Optional<User> result = userDao.login(username, password);
        if (result.isEmpty()) { System.out.println("Invalid username or password."); return; }

        User user = result.get();
        if (!"APPROVED".equalsIgnoreCase(user.status()) && !"ACTIVE".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is " + user.status() + ". Please wait for approval.");
            return;
        }
        switch (user.roles().toUpperCase()) {
            case "ADMIN", "ADMINISTRATOR", "OWNER", "SHOP OWNER" -> new AdminMenu(userDao, scanner).show();
            case "GROOMER", "STAFF" -> new UserMenu(user, scanner, userDao).show();
            case "RECEPTIONIST", "CASHIER" -> new UserMenu(user, scanner, userDao).show();
            default -> System.out.println("No menu is configured for role: " + user.roles());
        }
    }
}
