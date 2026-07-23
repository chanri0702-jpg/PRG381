package za.bc.cleaninginventory.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import za.bc.cleaninginventory.service.auth.AuthenticationService;

/**
 * Controller servlet handling user registration requests.
 */
@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String role = request.getParameter("role");

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Passwords do not match.");
            keepFormFields(request, name, surname, email, role);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }

        try {
            authService.register(
                name != null ? name.trim() : null,
                surname != null ? surname.trim() : null,
                email != null ? email.trim() : null, 
                password, 
                role,
                1 // Defaulting campId to 1 since it's not in the UI
            );
            request.setAttribute("success", "Registration successful! Please log in.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            keepFormFields(request, name, surname, email, role);
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    private void keepFormFields(HttpServletRequest request, String name, String surname, String email, String role) {
        request.setAttribute("enteredName", name);
        request.setAttribute("enteredSurname", surname);
        request.setAttribute("enteredEmail", email);
        request.setAttribute("enteredRole", role);
    }
}
