package za.bc.cleaninginventory.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import java.util.concurrent.atomic.AtomicInteger;
import za.bc.cleaninginventory.service.auth.AuthenticationService;

/**
 * Listener for monitoring session creation/destruction and initializing resources on startup.
 */
@WebListener
public class SessionListener implements HttpSessionListener, ServletContextListener {
    private static final AtomicInteger activeSessions = new AtomicInteger(0);
    private final AuthenticationService authService = new AuthenticationService();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("Cleaning Inventory Application starting up...");
        // Automatically seed default roles & admin users
        authService.seedDefaultUsers();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Cleaning Inventory Application shutting down...");
        try {
            za.bc.cleaninginventory.database.ConnectionPool.shutdown();
            System.out.println("Database connection pool closed.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        int count = activeSessions.incrementAndGet();
        System.out.println("Session created. Active sessions: " + count);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        int count = activeSessions.decrementAndGet();
        System.out.println("Session destroyed. Active sessions: " + count);
    }

    public static int getActiveSessionsCount() {
        return activeSessions.get();
    }
}
