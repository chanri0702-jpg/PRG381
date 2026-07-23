package za.bc.cleaninginventory.controller.material;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import za.bc.cleaninginventory.model.dao.material.MaterialDAO;
import za.bc.cleaninginventory.model.entity.Material;
import za.bc.cleaninginventory.service.material.MaterialService;

@WebServlet(name = "MaterialServlet", urlPatterns = {"/materials"})
public class MaterialServlet extends HttpServlet {

    private MaterialService materialService;

    @Override
    public void init() {
        materialService = new MaterialService();
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

                case "get":
                    getMaterialJson(request, response);
                    break;

                case "search":
                    searchMaterials(request, response);
                    break;

                case "list":
                default:
                    listMaterials(request, response);
                    break;
            }

        } catch (SQLException | IllegalArgumentException exception) {
            throw new ServletException(
                    "Unable to process material request.",
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

        String action = request.getParameter("action");

        if (action == null || action.isBlank()) {
            action = "create";
        }

        try {
            switch (action) {
                case "create":
                case "add":
                    createMaterial(request, response);
                    break;

                case "update":
                    updateMaterial(request, response);
                    break;

                case "delete":
                    deleteMaterial(request, response);
                    break;

                default:
                    response.sendRedirect(
                            request.getContextPath() + "/materials"
                    );
                    break;
            }

        } catch (SQLException exception) {
            throw new ServletException(
                    "A material database operation failed.",
                    exception
            );
        }
    }

    private void listMaterials(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        List<Material> materials = materialService.getAllMaterials();
        List<MaterialDAO.Supplier> suppliers = materialService.getDistinctSuppliers();
        List<MaterialDAO.Campus> campuses = materialService.getDistinctCampuses();

        request.setAttribute("materials", materials);
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("campuses", campuses);
        request.setAttribute("lowStockCount", materialService.getLowStockCount());
        request.setAttribute("totalMaterials", materialService.getTotalMaterialCount());
        request.setAttribute("viewMode", "list");

        // Forward to /materials/materials.jsp
        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void searchMaterials(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        String searchTerm = request.getParameter("searchTerm");
        String supplierId = request.getParameter("supplierId");
        String campusId = request.getParameter("campusId");

        List<Material> materials = materialService.searchMaterials(searchTerm, supplierId, campusId);
        List<MaterialDAO.Supplier> suppliers = materialService.getDistinctSuppliers();
        List<MaterialDAO.Campus> campuses = materialService.getDistinctCampuses();

        request.setAttribute("materials", materials);
        request.setAttribute("suppliers", suppliers);
        request.setAttribute("campuses", campuses);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("selectedSupplier", supplierId);
        request.setAttribute("selectedCampus", campusId);
        request.setAttribute("lowStockCount", materialService.getLowStockCount());
        request.setAttribute("totalMaterials", materialService.getTotalMaterialCount());
        request.setAttribute("viewMode", "list");

        // Forward to /materials/materials.jsp
        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void showAddPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        request.setAttribute("suppliers", materialService.getDistinctSuppliers());
        request.setAttribute("campuses", materialService.getDistinctCampuses());
        request.setAttribute("isEdit", false);
        request.setAttribute("viewMode", "add");

        // Forward to /materials/materials.jsp
        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void showEditPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Long materialId = getIdFromRequest(request);

        Material material = materialService.getMaterialById(materialId);

        if (material == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?error=Material+was+not+found"
            );
            return;
        }

        request.setAttribute("material", material);
        request.setAttribute("suppliers", materialService.getDistinctSuppliers());
        request.setAttribute("campuses", materialService.getDistinctCampuses());
        request.setAttribute("isEdit", true);
        request.setAttribute("viewMode", "edit");

        // Forward to /materials/materials.jsp
        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void showViewPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Long materialId = getIdFromRequest(request);

        Material material = materialService.getMaterialById(materialId);

        if (material == null) {
            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?error=Material+was+not+found"
            );
            return;
        }

        request.setAttribute("material", material);
        request.setAttribute("viewMode", "view");

        // Forward to /materials/materials.jsp
        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void getMaterialJson(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {
            Long materialId = getIdFromRequest(request);
            Material material = materialService.getMaterialById(materialId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (material != null) {
                String json = String.format(
                    "{\"success\":true,\"material\":{"
                    + "\"prodId\":%d,"
                    + "\"name\":\"%s\","
                    + "\"description\":\"%s\","
                    + "\"price\":%.2f,"
                    + "\"stockQuantity\":%d,"
                    + "\"busId\":%d,"
                    + "\"campId\":%d,"
                    + "\"supplierName\":\"%s\","
                    + "\"campusName\":\"%s\""
                    + "}}",
                    material.getProdId(),
                    escapeJson(material.getName()),
                    escapeJson(material.getDescription() != null ? material.getDescription() : ""),
                    material.getPrice().doubleValue(),
                    material.getStockQuantity() != null ? material.getStockQuantity() : 0,
                    material.getBusId(),
                    material.getCampId() != null ? material.getCampId() : 0,
                    escapeJson(material.getSupplierName() != null ? material.getSupplierName() : ""),
                    escapeJson(material.getCampusName() != null ? material.getCampusName() : "")
                );
                response.getWriter().write(json);
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Material not found\"}");
            }

        } catch (Exception exception) {
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Error loading material: " 
                + escapeJson(exception.getMessage()) + "\"}"
            );
        }
    }

    private void createMaterial(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Material material = buildMaterialFromRequest(request);

        try {
            boolean created = materialService.createMaterial(material);

            if (!created) {
                throw new IllegalArgumentException(
                        "Material could not be added."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?success=Material+added+successfully"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("material", material);
            request.setAttribute("suppliers", materialService.getDistinctSuppliers());
            request.setAttribute("campuses", materialService.getDistinctCampuses());
            request.setAttribute("isEdit", false);
            request.setAttribute("viewMode", "add");

            // Forward to /materials/materials.jsp
            request.getRequestDispatcher("/materials/materials.jsp")
                    .forward(request, response);
        }
    }

    private void updateMaterial(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        Material material = buildMaterialFromRequest(request);

        try {
            material.setProdId(
                    parsePositiveLong(
                            request.getParameter("prodId"),
                            "Invalid material ID."
                    )
            );

            boolean updated = materialService.updateMaterial(material);

            if (!updated) {
                throw new IllegalArgumentException(
                        "Material could not be updated."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?success=Material+updated+successfully"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("material", material);
            request.setAttribute("suppliers", materialService.getDistinctSuppliers());
            request.setAttribute("campuses", materialService.getDistinctCampuses());
            request.setAttribute("isEdit", true);
            request.setAttribute("viewMode", "edit");

            // Forward to /materials/materials.jsp
            request.getRequestDispatcher("/materials/materials.jsp")
                    .forward(request, response);
        }
    }

    private void deleteMaterial(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException {

        try {
            Long materialId =
                    parsePositiveLong(
                            request.getParameter("prodId"),
                            "Invalid material ID."
                    );

            boolean deleted = materialService.deleteMaterial(materialId);

            if (!deleted) {
                throw new IllegalArgumentException(
                        "Material was not found."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?success=Material+deleted+successfully"
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            listMaterials(request, response);
        }
    }

    private Material buildMaterialFromRequest(
            HttpServletRequest request
    ) {

        Material material = new Material();

        material.setName(
                request.getParameter("name")
        );

        material.setDescription(
                request.getParameter("description")
        );

        String priceStr = request.getParameter("price");
        try {
            if (priceStr != null && !priceStr.isBlank()) {
                material.setPrice(new BigDecimal(priceStr));
            }
        } catch (NumberFormatException exception) {
            material.setPrice(BigDecimal.ZERO);
        }

        String busIdStr = request.getParameter("busId");
        try {
            if (busIdStr != null && !busIdStr.isBlank()) {
                material.setBusId(Long.parseLong(busIdStr));
            }
        } catch (NumberFormatException exception) {
            material.setBusId(0L);
        }

        String stockStr = request.getParameter("stockQuantity");
        try {
            if (stockStr != null && !stockStr.isBlank()) {
                material.setStockQuantity(Integer.parseInt(stockStr));
            }
        } catch (NumberFormatException exception) {
            material.setStockQuantity(0);
        }

        String campIdStr = request.getParameter("campId");
        try {
            if (campIdStr != null && !campIdStr.isBlank()) {
                material.setCampId(Integer.parseInt(campIdStr));
            }
        } catch (NumberFormatException exception) {
            material.setCampId(0);
        }

        return material;
    }

    private Long getIdFromRequest(
            HttpServletRequest request
    ) {

        return parsePositiveLong(
                request.getParameter("id"),
                "Invalid material ID."
        );
    }

    private Long parsePositiveLong(
            String value,
            String errorMessage
    ) {

        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        try {
            long parsedValue =
                    Long.parseLong(value);

            if (parsedValue <= 0) {
                throw new IllegalArgumentException(
                        errorMessage
                );
            }

            return parsedValue;

        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    errorMessage
            );
        }
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}