/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package za.bc.cleaninginventory.controller.issuance;

import za.bc.cleaninginventory.model.dao.issuance.RequestDAO;
import za.bc.cleaninginventory.model.dao.issuance.OrderDAO;
import za.bc.cleaninginventory.model.entity.Order;
import za.bc.cleaninginventory.model.entity.Product;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;
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
        
        //FAKE INFO-REMOVE LATER
        session = req.getSession(true);
        session.setAttribute("employeeNumber", "100002");
        session.setAttribute("role", "SUPERVISOR");
        
        String employeeNumber = validateSupervisorSession(session);
        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int supervisorEmpId = Integer.parseInt(employeeNumber);
        moveFlashMessages(session, req);

        try {
            Integer campId = requestDAO.getEmployeeCampId(supervisorEmpId);
            List<Request> pendingRequests = (campId != null)
                    ? requestDAO.findAllPendingByCampus(campId)
                    : new ArrayList<>();
            List<Order> orderHistory = orderDAO.getOrderHistory();
            Map<Integer, String> campuses = orderDAO.getAllCampuses();
            Map<String, List<Product>> businessProducts = orderDAO.getAllProductsGroupedByBusiness();

            req.setAttribute("pendingRequests", pendingRequests);
            req.setAttribute("orderHistory", orderHistory);
            req.setAttribute("campuses", campuses);
            req.setAttribute("businessProducts", businessProducts);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("/issuance/orders.jsp").forward(req, resp);
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

        String action = req.getParameter("action");
        if ("reject".equals(action)) {
            handleReject(req, resp, session);
            return;
        }

        int supervisorEmpId = Integer.parseInt(employeeNumber);
        String[] selectedReqIds = req.getParameterValues("selectedReqIds");
        String[] manualProdIds = req.getParameterValues("manualProdId");
        String[] manualQuantities = req.getParameterValues("manualQuantity");
        String[] manualPrices = req.getParameterValues("manualPrice");
        String[] manualCampIds = req.getParameterValues("manualCampId");

        boolean hasSelected = selectedReqIds != null && selectedReqIds.length > 0;
        boolean hasManual = manualProdIds != null && manualProdIds.length > 0;

        if (!hasSelected && !hasManual) {
            session.setAttribute("flashError", "Select at least one request or add a product manually.");
            resp.sendRedirect("orders");
            return;
        }

        try {
            List<OrderDAO.OrderLine> lines = new ArrayList<>();

            if (hasSelected) {
                for (String reqIdStr : selectedReqIds) {
                    int reqId = Integer.parseInt(reqIdStr);
                    int prodId = Integer.parseInt(req.getParameter("prodId_" + reqId));
                    int quantity = Integer.parseInt(req.getParameter("quantity_" + reqId));
                    BigDecimal price = new BigDecimal(req.getParameter("price_" + reqId));

                    if (quantity <= 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                        session.setAttribute("flashError", "Quantities must be positive and price cannot be negative.");
                        resp.sendRedirect("orders");
                        return;
                    }
                    lines.add(new OrderDAO.OrderLine(reqId, prodId, quantity, price, null));
                }
            }

            if (hasManual) {
                for (int i = 0; i < manualProdIds.length; i++) {
                    if (manualProdIds[i] == null || manualProdIds[i].isBlank()) continue;

                    int prodId = Integer.parseInt(manualProdIds[i]);
                    int quantity = Integer.parseInt(manualQuantities[i]);
                    BigDecimal price = new BigDecimal(manualPrices[i]);
                    int campId = Integer.parseInt(manualCampIds[i]);

                    if (quantity <= 0 || price.compareTo(BigDecimal.ZERO) < 0) {
                        session.setAttribute("flashError", "Quantities must be positive and price cannot be negative.");
                        resp.sendRedirect("orders");
                        return;
                    }
                    lines.add(new OrderDAO.OrderLine(null, prodId, quantity, price, campId));
                }
            }

            boolean success = orderDAO.placeMultiProductOrder(lines, supervisorEmpId);

            if (success) {
                session.setAttribute("flashSuccess", "Order placed for " + lines.size() + " item(s).");
            } else {
                session.setAttribute("flashError", "Could not place the order.");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("flashError", "Invalid order details.");
        } catch (SQLException e) {
            session.setAttribute("flashError", "Database error: " + e.getMessage());
        }

        resp.sendRedirect("orders");
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
        String reqIdParam = req.getParameter("reqId");
        if (isEmpty(reqIdParam)) {
            session.setAttribute("flashError", "Missing request reference.");
            resp.sendRedirect("orders");
            return;
        }
        try {
            int reqId = Integer.parseInt(reqIdParam);
            boolean success = requestDAO.rejectRequest(reqId);
            if (success) {
                session.setAttribute("flashSuccess", "Request #" + reqId + " has been rejected.");
            } else {
                session.setAttribute("flashError", "Could not reject the request — it may have already been actioned.");
            }
        } catch (NumberFormatException | SQLException e) {
            session.setAttribute("flashError", "Could not reject the request: " + e.getMessage());
        }
        resp.sendRedirect("orders");
    }

    private String validateSupervisorSession(HttpSession session) {
        if (session == null) return null;
        String employeeNumber = (String) session.getAttribute("employeeNumber");
        String role = (String) session.getAttribute("role");
        if (employeeNumber == null || !"SUPERVISOR".equals(role)) return null;
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
