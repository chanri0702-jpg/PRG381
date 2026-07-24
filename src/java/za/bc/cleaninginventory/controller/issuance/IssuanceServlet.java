/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package za.bc.cleaninginventory.controller.issuance;

import za.bc.cleaninginventory.model.entity.Employee;
import za.bc.cleaninginventory.model.dao.cleaner.CleanerDAO;
import za.bc.cleaninginventory.model.dao.issuance.IssuanceDAO;
import za.bc.cleaninginventory.model.entity.Cleaner;
import za.bc.cleaninginventory.model.entity.Issuance;
import za.bc.cleaninginventory.model.dto.ProductStockDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * NOTE: flash messages (carrying a success/error message across the
 * redirect in doPost) are still not fully implemented — see the TODO
 * below. currentUser is read straight from HttpSession here because
 * AuthenticationFilter only checks the session and does not forward the
 * user onto the request.
 *
 * @author chanr
 */
@WebServlet(name = "IssuanceServlet", urlPatterns = {"/issuance"})
public class IssuanceServlet extends HttpServlet {

    private final CleanerDAO cleanerDAO = new CleanerDAO();
    private final IssuanceDAO issuanceDAO = new IssuanceDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        int storekeeperEmpId = currentUser.getEmpId();

        try {
            Integer campId = issuanceDAO.getEmployeeCampId(storekeeperEmpId);

            List<Cleaner> cleaners = (campId != null) ? cleanerDAO.getCleanersByCampus(campId) : new ArrayList<>();
            List<ProductStockDTO> availableStock = (campId != null) ? issuanceDAO.getProductStockForCampus(campId) : new ArrayList<>();
            List<Issuance> issuanceHistory = issuanceDAO.getIssuanceHistory();

            req.setAttribute("cleaners", cleaners);
            req.setAttribute("availableStock", availableStock);
            req.setAttribute("issuanceHistory", issuanceHistory);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("/issuance/issuance.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        int storekeeperEmpId = currentUser.getEmpId();

        String prodIdParam = req.getParameter("prodId");
        String cleanerIdParam = req.getParameter("cleanerId");
        String quantityParam = req.getParameter("quantity");

        // TODO(session-owner): "errorMessage"/"successMessage" set here are
        // plain request attributes and will NOT survive the sendRedirect()
        // calls below. Replace with whatever flash-message mechanism you
        // implement (session-backed, cleared after one read, etc.) so the
        // message set here is still visible after the redirect to "issuance".
        if (isEmpty(prodIdParam) || isEmpty(cleanerIdParam) || isEmpty(quantityParam)) {
            req.setAttribute("errorMessage", "Please select a product, cleaner, and quantity.");
            resp.sendRedirect("issuance");
            return;
        }

        try {
            int prodId = Integer.parseInt(prodIdParam);
            int cleanerId = Integer.parseInt(cleanerIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                req.setAttribute("errorMessage", "Quantity must be greater than zero.");
                resp.sendRedirect("issuance");
                return;
            }

            IssuanceDAO.IssueResult result = issuanceDAO.issueStock(prodId, cleanerId, quantity, storekeeperEmpId);

            switch (result) {
                case SUCCESS:
                    req.setAttribute("successMessage", "Stock issued successfully.");
                    break;
                case CLEANER_NOT_FOUND:
                    req.setAttribute("errorMessage", "Selected cleaner could not be found.");
                    break;
                case INSUFFICIENT_STOCK:
                    req.setAttribute("errorMessage", "Not enough stock at your campus to issue that quantity.");
                    break;
            }

        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Invalid issuance details.");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        resp.sendRedirect("issuance");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

}