package za.bc.cleaninginventory.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Filter to enforce authentication and role-based authorization across pages.
 */
@WebFilter(urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());

        // Bypassing login, registration, and static resources
        if (path.startsWith("/login") || 
            path.startsWith("/register") || 
            path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/images/")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        boolean loggedIn = (session != null && session.getAttribute("currentUser") != null);

        if (!loggedIn) {
            // Redirect unauthenticated users to login
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("userRole");

        // Role-Based Access Control (RBAC)
        // Storekeeper is restricted from accessing Reports
        if (path.startsWith("/reports") && !"SUPERVISOR".equalsIgnoreCase(role)) {
            // Unauthorized access: redirect to dashboard with an error
            httpRequest.setAttribute("error", "Access Denied: You do not have permissions to view reports.");
            httpRequest.getRequestDispatcher("/dashboard").forward(httpRequest, httpResponse);
            return;
        }

        // User is authenticated and authorized, proceed
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
