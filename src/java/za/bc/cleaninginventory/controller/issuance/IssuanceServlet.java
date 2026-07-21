/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package za.bc.cleaninginventory.controller.issuance;

import za.bc.cleaninginventory.model.dao.cleaner.CleanerDAO;
import za.bc.cleaninginventory.model.dao.issuance.IssuanceDAO;
import za.bc.cleaninginventory.model.dao.issuance.RequestDAO;
import za.bc.cleaninginventory.model.entity.Cleaner;
import za.bc.cleaninginventory.model.entity.Issuance;
import za.bc.cleaninginventory.model.entity.Request;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
/**
 *
 * @author chanr
 */
@WebServlet(name = "IssuanceServlet", urlPatterns = {"/issuance"})
public class IssuanceServlet extends HttpServlet {

   private final RequestDAO requestDAO = new RequestDAO();
    private final CleanerDAO cleanerDAO = new CleanerDAO();
    private final IssuanceDAO issuanceDAO = new IssuanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String employeeNumber = validateStorekeeperSession(session);
        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        moveFlashMessages(session, req);

        try {
            List<Request> approvedRequests = requestDAO.findAllApproved();
            List<Cleaner> cleaners = cleanerDAO.getAllCleaners();
            List<Issuance> issuanceHistory = issuanceDAO.getIssuanceHistory();

            req.setAttribute("approvedRequests", approvedRequests);
            req.setAttribute("cleaners", cleaners);
            req.setAttribute("issuanceHistory", issuanceHistory);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("issuance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String employeeNumber = validateStorekeeperSession(session);
        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int storekeeperEmpId = Integer.parseInt(employeeNumber);

        String reqIdParam = req.getParameter("reqId");
        String prodIdParam = req.getParameter("prodId");
        String cleanerIdParam = req.getParameter("cleanerId");
        String quantityParam = req.getParameter("quantity");

        if (isEmpty(reqIdParam) || isEmpty(prodIdParam) || isEmpty(cleanerIdParam) || isEmpty(quantityParam)) {
            session.setAttribute("flashError", "Please select a cleaner and confirm the quantity.");
            resp.sendRedirect("issuance");
            return;
        }

        try {
            int reqId = Integer.parseInt(reqIdParam);
            int prodId = Integer.parseInt(prodIdParam);
            int cleanerId = Integer.parseInt(cleanerIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                session.setAttribute("flashError", "Quantity must be greater than zero.");
                resp.sendRedirect("issuance");
                return;
            }

            IssuanceDAO.IssueResult result = issuanceDAO.issueStock(reqId, prodId, cleanerId, quantity, storekeeperEmpId);

            switch (result) {
                case SUCCESS:
                    session.setAttribute("flashSuccess", "Stock issued and request marked as issued.");
                    break;
                case NOT_APPROVED:
                    session.setAttribute("flashError", "That request is no longer approved — it may have already been issued.");
                    break;
                case CLEANER_NOT_FOUND:
                    session.setAttribute("flashError", "Selected cleaner could not be found.");
                    break;
                case INSUFFICIENT_STOCK:
                    session.setAttribute("flashError", "Not enough stock at this cleaner's campus to issue that quantity.");
                    break;
            }

        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid issuance details.");
        } catch (SQLException e) {
            session.setAttribute("flashError", "Database error: " + e.getMessage());
        }

        resp.sendRedirect("issuance");
    }

    // Confirms there's a logged-in employee with STOREKEEPER role; returns their emp_id as a String, or null
    private String validateStorekeeperSession(HttpSession session) {
        if (session == null) return null;

        String employeeNumber = (String) session.getAttribute("employeeNumber");
        String role = (String) session.getAttribute("role");

        if (employeeNumber == null || !"STOREKEEPER".equals(role)) {
            return null;
        }
        return employeeNumber;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void moveFlashMessages(HttpSession session, HttpServletRequest req) {
        if (session == null) return;

        Object success = session.getAttribute("flashSuccess");
        Object error = session.getAttribute("flashError");

        if (success != null) {
            req.setAttribute("successMessage", success);
            session.removeAttribute("flashSuccess");
        }
        if (error != null) {
            req.setAttribute("errorMessage", error);
            session.removeAttribute("flashError");
        }
    }

}
