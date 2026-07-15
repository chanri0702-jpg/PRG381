package za.bc.cleaninginventory.service.auth;

import za.bc.cleaninginventory.model.dao.auth.UserDAO;
import za.bc.cleaninginventory.model.entity.Role;
import za.bc.cleaninginventory.model.entity.User;
import za.bc.cleaninginventory.util.PasswordUtil;
import za.bc.cleaninginventory.util.ValidationUtil;

/**
 * Service class for Authentication and Registration business logic.
 */
public class AuthenticationService {
    private final UserDAO userDAO = new UserDAO();

    /**
     * Authenticates a user by username and password.
     * @param username the username
     * @param password the plain text password
     * @return User object on success, or null on failure
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        User user = userDAO.getUserByUsername(username);
        if (user != null && PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    /**
     * Registers a new user in the system.
     * @param username the username
     * @param password the plain text password
     * @param email the email address
     * @param roleName the name of the role (e.g. STOREKEEPER, SUPERVISOR)
     * @return the registered User object
     * @throws IllegalArgumentException if validation or database insertion fails
     */
    public User register(String username, String password, String email, String roleName) {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new IllegalArgumentException("Username must be between 3 and 20 alphanumeric characters.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        Role role = userDAO.getRoleByName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Role not found in the database: " + roleName);
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User(0, username, passwordHash, email, role);
        
        boolean created = userDAO.createUser(user);
        if (!created) {
            throw new IllegalArgumentException("Registration failed due to a database error.");
        }

        return user;
    }

    /**
     * Programmatically seeds default Storekeeper and Supervisor accounts if they do not exist.
     */
    public void seedDefaultUsers() {
        try {
            // Seed Storekeeper
            if (!userDAO.usernameExists("storekeeper")) {
                register("storekeeper", "storekeeper123", "storekeeper@university.edu", "STOREKEEPER");
                System.out.println("Default STOREKEEPER seeded.");
            }
            // Seed Supervisor
            if (!userDAO.usernameExists("supervisor")) {
                register("supervisor", "supervisor123", "supervisor@university.edu", "SUPERVISOR");
                System.out.println("Default SUPERVISOR seeded.");
            }
        } catch (Exception e) {
            System.err.println("Failed to seed default users: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
