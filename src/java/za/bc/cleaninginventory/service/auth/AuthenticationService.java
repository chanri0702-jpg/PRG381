package za.bc.cleaninginventory.service.auth;

import za.bc.cleaninginventory.model.dao.auth.EmployeeDAO;
import za.bc.cleaninginventory.model.entity.Employee;
import za.bc.cleaninginventory.util.PasswordUtil;
import za.bc.cleaninginventory.util.ValidationUtil;

/**
 * Service class for Authentication and Registration business logic.
 */
public class AuthenticationService {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

    /**
     * Authenticates an employee by email and password.
     * @param email the email
     * @param password the plain text password
     * @return Employee object on success, or null on failure
     */
    public Employee authenticate(String email, String password) {
        if (email == null || password == null) {
            return null;
        }
        Employee employee = employeeDAO.getEmployeeByEmail(email);
        if (employee != null && PasswordUtil.checkPassword(password, employee.getPassword())) {
            return employee;
        }
        return null;
    }

    /**
     * Registers a new employee in the system.
     * @param name the employee's first name
     * @param surname the employee's surname
     * @param email the email address
     * @param password the plain text password
     * @param role the name of the role (e.g. STOREKEEPER, SUPERVISOR)
     * @param campId the campus id
     * @return the registered Employee object
     * @throws IllegalArgumentException if validation or database insertion fails
     */
    public Employee register(String name, String surname, String email, String password, String role, int campId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        if (surname == null || surname.trim().isEmpty()) {
            throw new IllegalArgumentException("Surname cannot be empty.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (employeeDAO.emailExists(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        if (!"STOREKEEPER".equals(role) && !"SUPERVISOR".equals(role)) {
            throw new IllegalArgumentException("Role must be STOREKEEPER or SUPERVISOR.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        Employee employee = new Employee(0, campId, name, surname, role, passwordHash, email);
        
        boolean created = employeeDAO.createEmployee(employee);
        if (!created) {
            throw new IllegalArgumentException("Registration failed due to a database error.");
        }

        return employee;
    }

    /**
     * Programmatically seeds default Storekeeper and Supervisor accounts if they do not exist.
     */
    public void seedDefaultUsers() {
        try {
            // Seed Storekeeper
            if (!employeeDAO.emailExists("storekeeper@university.edu")) {
                register("System", "Storekeeper", "storekeeper@university.edu", "storekeeper123", "STOREKEEPER", 1);
                System.out.println("Default STOREKEEPER seeded.");
            }
            // Seed Supervisor
            if (!employeeDAO.emailExists("supervisor@university.edu")) {
                register("System", "Supervisor", "supervisor@university.edu", "supervisor123", "SUPERVISOR", 1);
                System.out.println("Default SUPERVISOR seeded.");
            }
        } catch (Exception e) {
            System.err.println("Failed to seed default users: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
