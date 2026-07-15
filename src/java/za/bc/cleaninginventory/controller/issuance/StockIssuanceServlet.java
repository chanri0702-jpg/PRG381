/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.bc.cleaninginventory.controller.issuance;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/supervisor-orders")
public class SupervisorOrderServlet extends HttpServlet {

    private final StockRequestDAO dao = new StockRequestDAO();

    // Show all requests
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            List<StockRequest> requests = dao.getAllRequests();
            req.setAttribute("requests", requests);
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("supervisor_orders.jsp").forward(req, resp);
    }

    // Approve or reject a request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idParam = req.getParameter("id");
        String action = req.getParameter("action"); // "APPROVE" or "REJECT"

        try {
            int id = Integer.parseInt(idParam);
            String newStatus = "APPROVE".equals(action) ? "APPROVED" : "REJECTED";
            dao.updateStatus(id, newStatus);
        } catch (NumberFormatException | SQLException e) {
            req.setAttribute("errorMessage", "Could not update request: " + e.getMessage());
        }

        // Redirect back so refreshing the page doesn't resubmit the action
        resp.sendRedirect("supervisor-orders");
    }
}
