package za.bc.cleaninginventory.controller.cleaner;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import za.bc.cleaninginventory.model.entity.Cleaner;
import za.bc.cleaninginventory.model.entity.Employee;
import za.bc.cleaninginventory.service.cleaner.CleanerService;

@WebServlet(name = "CleanerServlet", urlPatterns = {"/cleaners"})
public class CleanerServlet extends HttpServlet {

    private CleanerService cleanerService;

    @Override
    public void init() {
        cleanerService = new CleanerService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "list";
        }

        // Restrict add/edit views to Supervisor only
        if ("add".equals(action) || "edit".equals(action)) {
            HttpSession session = request.getSession(false);
            Employee currentUser = (Employee) (session != null ? session.getAttribute("currentUser") : null);
            String role = (currentUser != null) ? currentUser.getRole() : null;
            if (!"SUPERVISOR".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/cleaners");
                return;
            }
        }

        try {
            switch (action) {
                case "add":
                    showAddPage(request, response);
                    break;

                case "edit":
                    showEditPage(request, response);
                    break;

                case "view":
                    showViewPage(request, response);
                    break;

                case "list":
                default:
                    listCleaners(request, response);
                    break;
            }

        } catch (SQLException | IllegalArgumentException exception) {
            throw new ServletException(
                    "Unable to process cleaner request.",
                    exception
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Employee currentUser = (Employee) (session != null ? session.getAttribute("currentUser") : null);
        String role = (currentUser != null) ? currentUser.getRole() : null;

        if (!"SUPERVISOR".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only Supervisors are allowed to create, update or delete cleaners.");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "create";
        }

        try {
            switch (action) {
                case "create":
                    createCleaner(request, response);
                    break;

                case "update":
                    updateCleaner(request, response);
                    break;

                case "delete":
                    deleteCleaner(request, response);
                    break;

                default:
                    response.sendRedirect(
                            request.getContextPath() + "/cleaners"
                    );
                    break;
            }

        } catch (SQLException exception) {
            throw new ServletException(
                    "A cleaner database operation failed.",
                    exception
            );
        }
    }

    private void listCleaners(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        List<Cleaner> cleaners =
                cleanerService.getAllCleaners();

        Map<Integer, String> campuses =
                cleanerService.getAllCampuses();

        request.setAttribute("cleaners", cleaners);
        request.setAttribute("campuses", campuses);

        request.getRequestDispatcher("/cleaners/list.jsp")
                .forward(request, response);
    }

    private void showAddPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        request.setAttribute(
                "campuses",
                cleanerService.getAllCampuses()
        );

        request.getRequestDispatcher("/cleaners/add.jsp")
                .forward(request, response);
    }

    private void showEditPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        int cleanerId = getIdFromRequest(request);

        Cleaner cleaner =
                cleanerService.getCleanerById(cleanerId);

        if (cleaner == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/cleaners?error=Cleaner+was+not+found"
            );
            return;
        }

        request.setAttribute("cleaner", cleaner);

        request.setAttribute(
                "campuses",
                cleanerService.getAllCampuses()
        );

        request.getRequestDispatcher("/cleaners/edit.jsp")
                .forward(request, response);
    }

    private void showViewPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        int cleanerId = getIdFromRequest(request);

        Cleaner cleaner =
                cleanerService.getCleanerById(cleanerId);

        if (cleaner == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/cleaners?error=Cleaner+was+not+found"
            );
            return;
        }

        request.setAttribute("cleaner", cleaner);

        request.getRequestDispatcher("/cleaners/view.jsp")
                .forward(request, response);
    }

    private void createCleaner(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Cleaner cleaner = buildCleanerFromRequest(request);

        try {
            boolean created =
                    cleanerService.addCleaner(cleaner);

            if (!created) {
                throw new IllegalArgumentException(
                        "Cleaner could not be added."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/cleaners?success=Cleaner+added+successfully"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("cleaner", cleaner);

            request.setAttribute(
                    "campuses",
                    cleanerService.getAllCampuses()
            );

            request.getRequestDispatcher("/cleaners/add.jsp")
                    .forward(request, response);
        }
    }

    private void updateCleaner(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Cleaner cleaner = buildCleanerFromRequest(request);

        try {
            cleaner.setCleanerId(
                    parsePositiveInteger(
                            request.getParameter("cleanerId"),
                            "Invalid cleaner ID."
                    )
            );

            boolean updated =
                    cleanerService.updateCleaner(cleaner);

            if (!updated) {
                throw new IllegalArgumentException(
                        "Cleaner could not be updated."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/cleaners?success=Cleaner+updated+successfully"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("cleaner", cleaner);

            request.setAttribute(
                    "campuses",
                    cleanerService.getAllCampuses()
            );

            request.getRequestDispatcher("/cleaners/edit.jsp")
                    .forward(request, response);
        }
    }

    private void deleteCleaner(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        try {
            int cleanerId =
                    parsePositiveInteger(
                            request.getParameter("cleanerId"),
                            "Invalid cleaner ID."
                    );

            boolean deleted =
                    cleanerService.deleteCleaner(cleanerId);

            if (!deleted) {
                throw new IllegalArgumentException(
                        "Cleaner was not found."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/cleaners?success=Cleaner+deleted+successfully"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            listCleaners(request, response);
        }
    }

    private Cleaner buildCleanerFromRequest(
            HttpServletRequest request
    ) {

        Cleaner cleaner = new Cleaner();

        cleaner.setName(
                request.getParameter("name")
        );

        cleaner.setSurname(
                request.getParameter("surname")
        );

        cleaner.setPhone(
                request.getParameter("phone")
        );

        cleaner.setEmail(
                request.getParameter("email")
        );

        String campusId =
                request.getParameter("campusId");

        try {
            cleaner.setCampusId(
                    Integer.parseInt(campusId)
            );
        } catch (NumberFormatException exception) {
            cleaner.setCampusId(0);
        }

        return cleaner;
    }

    private int getIdFromRequest(
            HttpServletRequest request
    ) {

        return parsePositiveInteger(
                request.getParameter("id"),
                "Invalid cleaner ID."
        );
    }

    private int parsePositiveInteger(
            String value,
            String errorMessage
    ) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        try {
            int parsedValue =
                    Integer.parseInt(value);

            if (parsedValue <= 0) {
                throw new IllegalArgumentException(
                        errorMessage
                );
            }

            return parsedValue;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    errorMessage
            );
        }
    }
}
