/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package za.bc.cleaninginventory.controller.issuance;

import za.bc.cleaninginventory.model.dao.issuance.RequestDAO;
import za.bc.cleaninginventory.model.dao.material.MaterialDAO;
import za.bc.cleaninginventory.model.entity.Product;
import za.bc.cleaninginventory.model.entity.Request;
import za.bc.cleaninginventory.service.issuance.EmailService;
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

/**
 * NOTE: flash messages (carrying a success/error message across the
 * redirect in doPost) are still not fully implemented — see the TODOs
 * below. currentUser is read straight from HttpSession here because
 * AuthenticationFilter only checks the session and does not forward the
 * user onto the request.
 *
 * @author chanr
 */
@WebServlet(name = "RequestServlet", urlPatterns = {"/request"})
public class RequestServlet extends HttpServlet {

    private final RequestDAO requestDAO = new RequestDAO();
    private final MaterialDAO productDAO = new MaterialDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        int empId = currentUser.getEmpId();

        try {
            String editReqIdParam = req.getParameter("editReqId");
            if (editReqIdParam != null) {
                int editReqId = Integer.parseInt(editReqIdParam);
                Request editingRequest = requestDAO.findById(editReqId);

                if (editingRequest != null && editingRequest.getEmpID() == empId) {
                    req.setAttribute("editingRequest", editingRequest);
                } else {
                    req.setAttribute("errorMessage", "That request could not be found.");
                }
            }

            List<Product> products = productDAO.getProductsForIssuance();
            List<Request> myRequests = requestDAO.findByEmployee(empId);

            req.setAttribute("products", products);
            req.setAttribute("myRequests", myRequests);

        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Invalid request reference.");
        }

        req.getRequestDispatcher("/issuance/issueRequest.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        Employee currentUser = (Employee) session.getAttribute("currentUser");
        int empId = currentUser.getEmpId();

        String action = req.getParameter("action");

        // TODO(session-owner): "errorMessage"/"successMessage" set in the
        // handle* methods below are plain request attributes and will NOT
        // survive the sendRedirect("request") call at the end of this
        // method. Replace with whatever flash-message mechanism you
        // implement so the message is still visible after the redirect.
        switch (action == null ? "" : action) {
            case "create":
                handleCreate(req, empId);
                break;
            case "update":
                handleUpdate(req, empId);
                break;
            case "delete":
                handleDelete(req, empId);
                break;
            default:
                req.setAttribute("errorMessage", "Unknown action.");
        }

        resp.sendRedirect("request");
    }

    private void handleCreate(HttpServletRequest req, int empId) {
        ValidatedInput input = validateInput(req);
        if (input == null) {
            req.setAttribute("errorMessage", "Please fill in all fields correctly.");
            return;
        }

        try {
            Request newRequest = new Request(empId, input.prodId, input.quantity, input.priority, input.description);
            int newReqId = requestDAO.insertRequest(newRequest);
            req.setAttribute("successMessage", "Your request has been submitted.");

            if ("URGENT".equals(input.priority)) {
                notifyStorekeepersOfUrgentRequest(newReqId);
            }
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }
    }

    private void notifyStorekeepersOfUrgentRequest(int reqId) {
        try {
            RequestDAO.UrgentNotificationInfo info = requestDAO.getNotificationInfo(reqId);
            if (info == null) {
                System.err.println("[URGENT EMAIL] No notification info found for req " + reqId);
                return;
            }

            System.err.println("[URGENT EMAIL] campId=" + info.campId + " product=" + info.productName);

            List<String> storekeeperEmails = requestDAO.getStorekeeperEmailsByCampus(info.campId);
            System.err.println("[URGENT EMAIL] Found " + storekeeperEmails.size() + " storekeeper(s) for campId " + info.campId);

            if (storekeeperEmails.isEmpty()) {
                System.err.println("[URGENT EMAIL] No storekeepers on this campus - nothing to send");
                return;
            }

            EmailService.sendUrgentRequestNotificationAsync(
                    storekeeperEmails, info.requesterName, info.productName, info.quantity, info.description);
            System.err.println("[URGENT EMAIL] Submitted send task for: " + storekeeperEmails);

        } catch (SQLException e) {
            System.err.println("[URGENT EMAIL] SQL error: " + e.getMessage());
        }
    }

    private void handleUpdate(HttpServletRequest req, int empId) {
        ValidatedInput input = validateInput(req);
        String reqIdParam = req.getParameter("reqId");

        if (input == null || reqIdParam == null) {
            req.setAttribute("errorMessage", "Please fill in all fields correctly.");
            return;
        }

        try {
            int reqId = Integer.parseInt(reqIdParam);
            Request updated = new Request(empId, input.prodId, input.quantity, input.priority, input.description);
            updated.setId(reqId);

            boolean success = requestDAO.updateRequest(updated);
            if (success) {
                req.setAttribute("successMessage", "Your request has been updated.");
                if ("URGENT".equals(input.priority)) {
                    notifyStorekeepersOfUrgentRequest(reqId);
                }
            } else {
                req.setAttribute("errorMessage", "Could not update the request — it may have already been actioned.");
            }
        } catch (NumberFormatException | SQLException e) {
            req.setAttribute("errorMessage", "Could not update the request: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest req, int empId) {
        String reqIdParam = req.getParameter("reqId");
        if (reqIdParam == null) {
            req.setAttribute("errorMessage", "Missing request reference.");
            return;
        }

        try {
            int reqId = Integer.parseInt(reqIdParam);
            boolean success = requestDAO.deleteRequest(reqId, empId);
            if (success) {
                req.setAttribute("successMessage", "Your request has been deleted.");
            } else {
                req.setAttribute("errorMessage", "Could not delete the request — it may have already been actioned.");
            }
        } catch (NumberFormatException | SQLException e) {
            req.setAttribute("errorMessage", "Could not delete the request: " + e.getMessage());
        }
    }

    private ValidatedInput validateInput(HttpServletRequest req) {
        String prodIdParam = req.getParameter("prodId");
        String quantityParam = req.getParameter("quantity");
        String priority = req.getParameter("priority");
        String description = req.getParameter("description");

        if (isEmpty(prodIdParam) || isEmpty(quantityParam) || isEmpty(priority) || isEmpty(description)) {
            return null;
        }

        try {
            int prodId = Integer.parseInt(prodIdParam);
            int quantity = Integer.parseInt(quantityParam);

            if (quantity <= 0) {
                return null;
            }

            if (!priority.equals("LOW") && !priority.equals("NORMAL")
                    && !priority.equals("HIGH") && !priority.equals("URGENT")) {
                return null;
            }

            return new ValidatedInput(prodId, quantity, priority, description);

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static class ValidatedInput {

        final int prodId;
        final int quantity;
        final String priority;
        final String description;

        ValidatedInput(int prodId, int quantity, String priority, String description) {
            this.prodId = prodId;
            this.quantity = quantity;
            this.priority = priority;
            this.description = description;
        }
    }

}