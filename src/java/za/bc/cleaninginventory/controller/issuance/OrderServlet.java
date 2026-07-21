/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package za.bc.cleaninginventory.controller.issuance;

import za.bc.cleaninginventory.model.dao.issuance.RequestDAO;
import za.bc.cleaninginventory.model.dao.issuance.OrderDAO;
import za.bc.cleaninginventory.model.entity.Order;
import za.bc.cleaninginventory.model.entity.Request;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import za.bc.cleaninginventory.model.dao.issuance.RequestDAO;

/**
 *
 * @author chanr
 */
@WebServlet(name = "Order", urlPatterns = {"/orders"})
public class OrderServlet extends HttpServlet {

    private final RequestDAO requestDAO = new RequestDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String employeeNumber = validateSupervisorSession(session);
        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        moveFlashMessages(session, req);

        try {
            List<Request> pendingRequests = requestDAO.findAllPending();
            List<Order> orderHistory = orderDAO.getOrderHistory();

            req.setAttribute("pendingRequests", pendingRequests);
            req.setAttribute("orderHistory", orderHistory);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String employeeNumber = validateSupervisorSession(session);
        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int supervisorEmpId = Integer.parseInt(employeeNumber);

        String reqIdParam = req.getParameter("reqId");
        String prodIdParam = req.getParameter("prodId");
        String quantityParam = req.getParameter("quantity");

        if (isEmpty(reqIdParam) || isEmpty(prodIdParam) || isEmpty(quantityParam)) {
            session.setAttribute("flashError", "Missing order details.");
            resp.sendRedirect("orders");
            return;
        }

        try {
            int reqId = Integer.parseInt(reqIdParam);
            int prodId = Integer.parseInt(prodIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                session.setAttribute("flashError", "Quantity must be greater than zero.");
                resp.sendRedirect("orders");
                return;
            }

            boolean success = orderDAO.placeOrderForRequest(reqId, prodId, quantity, supervisorEmpId);

            if (success) {
                session.setAttribute("flashSuccess", "Order placed and request approved.");
            } else {
                session.setAttribute("flashError", "That request is no longer pending — it may have already been actioned.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid order details.");
        } catch (SQLException e) {
            session.setAttribute("flashError", "Database error: " + e.getMessage());
        }

        resp.sendRedirect("orders");
    }

    private String validateSupervisorSession(HttpSession session) {
        if (session == null) return null;

        String employeeNumber = (String) session.getAttribute("employeeNumber");
        String role = (String) session.getAttribute("role");

        if (employeeNumber == null || !"SUPERVISOR".equals(role)) {
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
