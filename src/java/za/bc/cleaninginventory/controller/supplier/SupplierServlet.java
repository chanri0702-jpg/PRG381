package za.bc.cleaninginventory.controller.supplier;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.http.HttpSession;
import za.bc.cleaninginventory.model.entity.Supplier;
import za.bc.cleaninginventory.model.entity.Employee;
import za.bc.cleaninginventory.service.supplier.SupplierService;

@WebServlet(name = "SupplierServlet", urlPatterns = {"/suppliers"})
public class SupplierServlet extends HttpServlet {

    private SupplierService supplierService;

    @Override
    public void init() {
        supplierService = new SupplierService();
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
                response.sendRedirect(request.getContextPath() + "/suppliers");
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
                    listSuppliers(request, response);
                    break;
            }

        } catch (SQLException | IllegalArgumentException exception) {
            throw new ServletException(
                    "Unable to process supplier request.",
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
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only Supervisors are allowed to create, update or delete suppliers.");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "create";
        }

        try {
            switch (action) {
                case "create":
                    createSupplier(request, response);
                    break;

                case "update":
                    updateSupplier(request, response);
                    break;

                case "delete":
                    deleteSupplier(request, response);
                    break;

                default:
                    response.sendRedirect(
                            request.getContextPath() + "/suppliers"
                    );
                    break;
            }

        } catch (SQLException exception) {
            throw new ServletException(
                    "A supplier database operation failed.",
                    exception
            );
        }
    }

    private void listSuppliers(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        List<Supplier> suppliers =
                supplierService.getAllSuppliers();

        request.setAttribute("suppliers", suppliers);

        request.getRequestDispatcher("/suppliers/list.jsp")
                .forward(request, response);
    }

    private void showAddPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        request.getRequestDispatcher("/suppliers/add.jsp")
                .forward(request, response);
    }

    private void showEditPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        int businessId = getIdFromRequest(request);

        Supplier supplier =
                supplierService.getSupplierById(businessId);

        if (supplier == null) {
            response.sendRedirect(
                    request.getContextPath() + "/suppliers"
            );
            return;
        }

        request.setAttribute("supplier", supplier);

        request.getRequestDispatcher("/suppliers/edit.jsp")
                .forward(request, response);
    }

    private void showViewPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        int businessId = getIdFromRequest(request);

        Supplier supplier =
                supplierService.getSupplierById(businessId);

        if (supplier == null) {
            response.sendRedirect(
                    request.getContextPath() + "/suppliers"
            );
            return;
        }

        request.setAttribute("supplier", supplier);

        request.getRequestDispatcher("/suppliers/view.jsp")
                .forward(request, response);
    }

    private void createSupplier(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Supplier supplier = buildSupplierFromRequest(request);

        try {
            supplierService.addSupplier(supplier);

            response.sendRedirect(
                    request.getContextPath() + "/suppliers"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("supplier", supplier);

            request.getRequestDispatcher("/suppliers/add.jsp")
                    .forward(request, response);
        }
    }

    private void updateSupplier(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Supplier supplier = buildSupplierFromRequest(request);

        try {
            supplier.setBusinessId(
                    Integer.parseInt(
                            request.getParameter("businessId")
                    )
            );

            supplierService.updateSupplier(supplier);

            response.sendRedirect(
                    request.getContextPath() + "/suppliers"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("supplier", supplier);

            request.getRequestDispatcher("/suppliers/edit.jsp")
                    .forward(request, response);
        }
    }

    private void deleteSupplier(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        try {
            int businessId = Integer.parseInt(
                    request.getParameter("businessId")
            );

            supplierService.deleteSupplier(businessId);

            response.sendRedirect(
                    request.getContextPath() + "/suppliers"
            );

        } catch (IllegalArgumentException exception) {

            List<Supplier> suppliers =
                    supplierService.getAllSuppliers();

            request.setAttribute("suppliers", suppliers);

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.getRequestDispatcher("/suppliers/list.jsp")
                    .forward(request, response);
        }
    }

    private Supplier buildSupplierFromRequest(
            HttpServletRequest request
    ) {

        Supplier supplier = new Supplier();

        supplier.setBusinessName(
                request.getParameter("businessName")
        );

        supplier.setDescription(
                request.getParameter("description")
        );

        supplier.setAddress(
                request.getParameter("address")
        );

        supplier.setArea(
                request.getParameter("area")
        );

        supplier.setCity(
                request.getParameter("city")
        );

        supplier.setProvince(
                request.getParameter("province")
        );

        supplier.setPostalCode(
                request.getParameter("postalCode")
        );

        supplier.setContactName(
                request.getParameter("contactName")
        );

        supplier.setContactSurname(
                request.getParameter("contactSurname")
        );

        supplier.setContactEmail(
                request.getParameter("contactEmail")
        );

        supplier.setContactPhone(
                request.getParameter("contactPhone")
        );

        return supplier;
    }

    private int getIdFromRequest(
            HttpServletRequest request
    ) {

        String idValue = request.getParameter("id");

        if (idValue == null || idValue.isBlank()) {
            throw new IllegalArgumentException(
                    "Supplier ID is required."
            );
        }

        try {
            return Integer.parseInt(idValue);

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid supplier ID."
            );
        }
    }
}
