/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author chanr
 */

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */

@WebServlet("/IssueRequestServlet")
public class IssueRequestServlet extends HttpServlet {

    private final StockRequestDAO dao = new StockRequestDAO();

    // Show the empty form
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("employee_request.jsp").forward(req, resp);
    }

    // Handle form submission
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String employeeNumber = req.getParameter("employeeNumber");
        String employeeName = req.getParameter("employeeName");
        String material = req.getParameter("material");
        String quantityStr = req.getParameter("quantity");
        String reason = req.getParameter("reason");

        // Basic validation
        if (isEmpty(employeeNumber) || isEmpty(employeeName) || isEmpty(material)
                || isEmpty(quantityStr) || isEmpty(reason)) {
            req.setAttribute("errorMessage", "Please fill in all fields.");
            req.getRequestDispatcher("employee_request.jsp").forward(req, resp);
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            req.setAttribute("errorMessage", "Quantity must be a positive number.");
            req.getRequestDispatcher("employee_request.jsp").forward(req, resp);
            return;
        }

        StockRequest request = new StockRequest(employeeNumber, employeeName, material, quantity, reason);

        try {
            dao.insertRequest(request);
            req.setAttribute("successMessage", "Your request has been submitted.");
        } catch (SQLException e) {
            req.setAttribute("errorMessage", "Database error: " + e.getMessage());
        }

        req.getRequestDispatcher("employee_request.jsp").forward(req, resp);
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>


