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
import za.bc.cleaninginventory.model.entity.Employee;

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

/**
 * NOTE: flash messages (carrying a success/error message across the
 * redirect in doPost) are still not fully implemented — see the TODO
 * below. currentUser is read straight from HttpSession here because
 * AuthenticationFilter only checks the session and does not forward the
 * user onto the request.
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
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        int supervisorEmpId = currentUser.getEmpId();

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

        String action = req.getParameter("action");
        if ("reject".equals(action)) {
            handleReject(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        int supervisorEmpId = currentUser.getEmpId();

        String[] selectedReqIds = req.getParameterValues("selectedReqIds");
        String[] manualProdIds = req.getParameterValues("manualProdId");
        String[] manualQuantities = req.getParameterValues("manualQuantity");
        String[] manualPrices = req.getParameterValues("manualPrice");
        String[] manualCampIds = req.getParameterValues("manualCampId");

        boolean hasSelected = selectedReqIds != null && selectedReqIds.length > 0;
        boolean hasManual = manualProdIds != null && manualProdIds.length > 0;

        // TODO(session-owner): "errorMessage"/"successMessage" set in this
        // method are plain request attributes and will NOT survive the
        // sendRedirect("orders") calls below. Replace with whatever
        // flash-message mechanism you implement so the message is still
        // visible after the redirect.
        if (!hasSelected && !hasManual) {
            req.setAttribute("errorMessage", "Select at least one request or add a product manually.");
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
                        req.setAttribute("errorMessage", "Quantities must be positive and price cannot be negative.");
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
                        req.setAttribute("errorMessage", "Quantities must be positive and price cannot be negative.");
                        resp.sendRedirect("orders");
                        return;
                    }
                    lines.add(new OrderDAO.OrderLine(null, prodId, quantity, price, campId));
                }
            }

            boolean success = orderDAO.placeMultiProductOrder(lines, supervisorEmpId);

            if (success) {
                req.setAttribute("successMessage", "Order placed for " + lines.size() + " item(s).");
            } else {
                req.setAttribute("errorMessage", "Could not place the order.");
            }

        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Invalid order details.");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        resp.sendRedirect("orders");
    }

    private void handleReject(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reqIdParam = req.getParameter("reqId");
        if (isEmpty(reqIdParam)) {
            req.setAttribute("errorMessage", "Missing request reference.");
            resp.sendRedirect("orders");
            return;
        }
        try {
            int reqId = Integer.parseInt(reqIdParam);
            boolean success = requestDAO.rejectRequest(reqId);
            if (success) {
                req.setAttribute("successMessage", "Request #" + reqId + " has been rejected.");
            } else {
                req.setAttribute("errorMessage", "Could not reject the request — it may have already been actioned.");
            }
        } catch (NumberFormatException | SQLException e) {
            req.setAttribute("errorMessage", "Could not reject the request: " + e.getMessage());
        }
        resp.sendRedirect("orders");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}