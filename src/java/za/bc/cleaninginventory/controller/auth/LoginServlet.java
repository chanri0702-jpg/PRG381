package za.bc.cleaninginventory.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import za.bc.cleaninginventory.model.entity.Employee;
import za.bc.cleaninginventory.service.auth.AuthenticationService;

/**
 * Controller servlet handling user login requests.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    private final AuthenticationService authService = new AuthenticationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("error", "Email and Password cannot be empty.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
            return;
        }

        Employee employee = authService.authenticate(email.trim(), password);
        if (employee != null) {
            HttpSession session = request.getSession(true);
            session.setMaxInactiveInterval(120); // 2 minutes server-side timeout
            session.setAttribute("currentUser", employee);
            session.setAttribute("userRole", employee.getRole());
            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            request.setAttribute("error", "Invalid email or password.");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
