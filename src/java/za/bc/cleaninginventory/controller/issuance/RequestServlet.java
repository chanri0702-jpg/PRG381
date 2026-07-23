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

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.io.PrintWriter;

/**
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
        
        //temp login details for testing
        session = req.getSession(true);
        session.setAttribute("employeeNumber", "100000");
        //REMOVE AFTER
        
        String employeeNumber = (session != null) ? (String) session.getAttribute("employeeNumber") : null;

        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int empId;
        try {
            empId = Integer.parseInt(employeeNumber);
        } catch (NumberFormatException e) {
            resp.sendRedirect("login.jsp");
            return;
        }

        moveFlashMessages(session, req);

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

            List<Product> products = productDAO.getAllProducts();
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
        String employeeNumber = (session != null) ? (String) session.getAttribute("employeeNumber") : null;

        if (employeeNumber == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int empId;
        try {
            empId = Integer.parseInt(employeeNumber);
        } catch (NumberFormatException e) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String action = req.getParameter("action");

        switch (action == null ? "" : action) {
            case "create":
                handleCreate(req, empId, session);
                break;
            case "update":
                handleUpdate(req, empId, session);
                break;
            case "delete":
                handleDelete(req, empId, session);
                break;
            default:
                session.setAttribute("flashError", "Unknown action.");
        }

        resp.sendRedirect("request");
    }

    private void handleCreate(HttpServletRequest req, int empId, HttpSession session) {
        ValidatedInput input = validateInput(req);
        if (input == null) {
            session.setAttribute("flashError", "Please fill in all fields correctly.");
            return;
        }

        try {
            Request newRequest = new Request(empId, input.prodId, input.quantity, input.priority, input.description);
            int newReqId = requestDAO.insertRequest(newRequest);
            session.setAttribute("flashSuccess", "Your request has been submitted.");

            if ("URGENT".equals(input.priority)) {
                notifyStorekeepersOfUrgentRequest(newReqId);
            }
        } catch (SQLException e) {
            session.setAttribute("flashError", "Database error: " + e.getMessage());
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

    private void handleUpdate(HttpServletRequest req, int empId, HttpSession session) {
        ValidatedInput input = validateInput(req);
        String reqIdParam = req.getParameter("reqId");

        if (input == null || reqIdParam == null) {
            session.setAttribute("flashError", "Please fill in all fields correctly.");
            return;
        }

        try {
            int reqId = Integer.parseInt(reqIdParam);
            Request updated = new Request(empId, input.prodId, input.quantity, input.priority, input.description);
            updated.setId(reqId);

            boolean success = requestDAO.updateRequest(updated);
            if (success) {
                session.setAttribute("flashSuccess", "Your request has been updated.");
                if ("URGENT".equals(input.priority)) {
                    notifyStorekeepersOfUrgentRequest(reqId);
                }
            } else {
                session.setAttribute("flashError", "Could not update the request — it may have already been actioned.");
            }
        } catch (NumberFormatException | SQLException e) {
            session.setAttribute("flashError", "Could not update the request: " + e.getMessage());
        }
    }

    private void handleDelete(HttpServletRequest req, int empId, HttpSession session) {
        String reqIdParam = req.getParameter("reqId");
        if (reqIdParam == null) {
            session.setAttribute("flashError", "Missing request reference.");
            return;
        }

        try {
            int reqId = Integer.parseInt(reqIdParam);
            boolean success = requestDAO.deleteRequest(reqId, empId);
            if (success) {
                session.setAttribute("flashSuccess", "Your request has been deleted.");
            } else {
                session.setAttribute("flashError", "Could not delete the request — it may have already been actioned.");
            }
        } catch (NumberFormatException | SQLException e) {
            session.setAttribute("flashError", "Could not delete the request: " + e.getMessage());
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
