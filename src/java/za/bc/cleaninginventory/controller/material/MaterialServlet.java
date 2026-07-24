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
import jakarta.servlet.http.HttpSession;
import za.bc.cleaninginventory.model.dao.material.MaterialDAO;
import za.bc.cleaninginventory.model.entity.Material;
import za.bc.cleaninginventory.model.entity.Employee;
import za.bc.cleaninginventory.service.material.MaterialService;

@WebServlet(name = "MaterialServlet", urlPatterns ={"/materials"})
public class MaterialServlet extends HttpServlet{

    private MaterialService materialService;

    @Override
    public void init(){
        materialService = new MaterialService();
        System.out.println("=== MaterialServlet Initialized ===");
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException{

        String action = request.getParameter("action");

        if (action == null || action.isBlank()){
            action = "list";
        }

        // Restrict add/edit views to Storekeeper only
        if ("add".equals(action) || "edit".equals(action)) {
            HttpSession session = request.getSession(false);
            Employee currentUser = (Employee) (session != null ? session.getAttribute("currentUser") : null);
            String role = (currentUser != null) ? currentUser.getRole() : null;
            if (!"STOREKEEPER".equalsIgnoreCase(role)) {
                response.sendRedirect(request.getContextPath() + "/materials");
                return;
            }
        }

        System.out.println("=== MaterialServlet.doGet() ===");
        System.out.println("Action: " + action);

        try{
            switch (action){
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

        } catch (SQLException | IllegalArgumentException exception){
            exception.printStackTrace();
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
    ) throws ServletException, IOException{

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        Employee currentUser = (Employee) (session != null ? session.getAttribute("currentUser") : null);
        String role = (currentUser != null) ? currentUser.getRole() : null;

        if (!"STOREKEEPER".equalsIgnoreCase(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied: Only Storekeepers are allowed to create, update or delete materials.");
            return;
        }

        String action = request.getParameter("action");

        if (action == null || action.isBlank()){
            action = "create";
        }

        System.out.println("=== MaterialServlet.doPost() ===");
        System.out.println("Action: " + action);

        try{
            switch (action){
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

        } catch (SQLException exception){
            exception.printStackTrace();
            throw new ServletException(
                    "A material database operation failed.",
                    exception
            );
        }
    }

    private void listMaterials(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== listMaterials() called ===");

        try{
            List<Material> materials = materialService.getAllMaterials();
            List<MaterialDAO.Supplier> suppliers = materialService.getDistinctSuppliers();
            List<MaterialDAO.Campus> campuses = materialService.getDistinctCampuses();

            System.out.println("Materials count: " + materials.size());
            System.out.println("Suppliers count: " + suppliers.size());
            System.out.println("Campuses count: " + campuses.size());

            request.setAttribute("materials", materials);
            request.setAttribute("suppliers", suppliers);
            request.setAttribute("campuses", campuses);
            request.setAttribute("lowStockCount", materialService.getLowStockCount());
            request.setAttribute("totalMaterials", materialService.getTotalMaterialCount());
            request.setAttribute("viewMode", "list");
            
           //Clear search parameters
            request.setAttribute("searchTerm", "");
            request.setAttribute("selectedSupplier", "All");
            request.setAttribute("selectedCampus", "All");

            System.out.println("Forwarding to /materials/materials.jsp with viewMode=list");
            request.getRequestDispatcher("/materials/materials.jsp")
                    .forward(request, response);

        } catch (Exception e){
            e.printStackTrace();
            throw new ServletException("Error listing materials: " + e.getMessage(), e);
        }
    }

    private void searchMaterials(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== searchMaterials() called ===");

        try{
            String searchTerm = request.getParameter("searchTerm");
            String supplierId = request.getParameter("supplierId");
            String campusId = request.getParameter("campusId");

           //Set default values for null parameters
            if (searchTerm == null || searchTerm.isBlank()){
                searchTerm = "";
            }
            if (supplierId == null || supplierId.isBlank()){
                supplierId = "All";
            }
            if (campusId == null || campusId.isBlank()){
                campusId = "All";
            }

            boolean hasSearchTerm = !searchTerm.trim().isEmpty();
            boolean hasSupplier = !supplierId.equals("All");
            boolean hasCampus = !campusId.equals("All");

            List<Material> materials;

            if (hasSearchTerm || hasSupplier || hasCampus){
                materials = materialService.searchMaterials(searchTerm, supplierId, campusId);
                System.out.println("Search returned " + materials.size() + " results");
            } else{
                materials = materialService.getAllMaterials();
                System.out.println("No filters, returning all " + materials.size() + " materials");
            }

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

            System.out.println("Forwarding to /materials/materials.jsp with viewMode=list (search results)");
            request.getRequestDispatcher("/materials/materials.jsp")
                    .forward(request, response);

        } catch (Exception e){
            e.printStackTrace();
            request.setAttribute("errorMessage", "Search error: " + e.getMessage());
            listMaterials(request, response);
        }
    }

    private void showAddPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== showAddPage() called ===");

        request.setAttribute("suppliers", materialService.getDistinctSuppliers());
        request.setAttribute("campuses", materialService.getDistinctCampuses());
        request.setAttribute("isEdit", false);
        request.setAttribute("viewMode", "add");

        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void showEditPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== showEditPage() called ===");

        Long materialId = getIdFromRequest(request);
        System.out.println("Loading material with ID: " + materialId);

        Material material = materialService.getMaterialById(materialId);
        System.out.println("Retrieved material: " + material);

        if (material == null){
            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?error=Material+was+not+found"
            );
            return;
        }

       //Explicitly log the stock and campus values
        System.out.println("Material Stock: " + material.getStockQuantity());
        System.out.println("Material Campus ID: " + material.getCampId());

        request.setAttribute("material", material);
        request.setAttribute("suppliers", materialService.getDistinctSuppliers());
        request.setAttribute("campuses", materialService.getDistinctCampuses());
        request.setAttribute("isEdit", true);
        request.setAttribute("viewMode", "edit");

        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void showViewPage(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== showViewPage() called ===");

        Long materialId = getIdFromRequest(request);

        Material material = materialService.getMaterialById(materialId);

        if (material == null){
            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?error=Material+was+not+found"
            );
            return;
        }

        request.setAttribute("material", material);
        request.setAttribute("viewMode", "view");

        request.getRequestDispatcher("/materials/materials.jsp")
                .forward(request, response);
    }

    private void getMaterialJson(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException{

        try{
            Long materialId = getIdFromRequest(request);
            Material material = materialService.getMaterialById(materialId);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            if (material != null){
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
            } else{
                response.getWriter().write("{\"success\":false,\"message\":\"Material not found\"}");
            }

        } catch (Exception exception){
            response.getWriter().write(
                "{\"success\":false,\"message\":\"Error loading material: " 
                + escapeJson(exception.getMessage()) + "\"}"
            );
        }
    }

    private void createMaterial(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        Material material = buildMaterialFromRequest(request);

        try{
            boolean created = materialService.createMaterial(material);

            if (!created){
                throw new IllegalArgumentException(
                        "Material could not be added."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?success=Material+added+successfully"
            );

        } catch (IllegalArgumentException exception){
            exception.printStackTrace();

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            request.setAttribute("material", material);
            request.setAttribute("suppliers", materialService.getDistinctSuppliers());
            request.setAttribute("campuses", materialService.getDistinctCampuses());
            request.setAttribute("isEdit", false);
            request.setAttribute("viewMode", "add");

            request.getRequestDispatcher("/materials/materials.jsp")
                    .forward(request, response);
        }
    }

    private void updateMaterial(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== updateMaterial() called ===");

        try{
           //Get all parameters
            String prodIdStr = request.getParameter("prodId");
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            String priceStr = request.getParameter("price");
            String busIdStr = request.getParameter("busId");
            String stockStr = request.getParameter("stockQuantity");
            String campIdStr = request.getParameter("campId");

            System.out.println("Update Parameters:");
            System.out.println("  prodId: " + prodIdStr);
            System.out.println("  name: " + name);
            System.out.println("  description: " + description);
            System.out.println("  price: " + priceStr);
            System.out.println("  busId: " + busIdStr);
            System.out.println("  stock: " + stockStr);
            System.out.println("  campId: " + campIdStr);

           //Validate required fields
            if (prodIdStr == null || prodIdStr.isBlank()){
                throw new IllegalArgumentException("Material ID is required.");
            }
            if (name == null || name.isBlank()){
                throw new IllegalArgumentException("Material name is required.");
            }

            Long prodId = Long.parseLong(prodIdStr);
            BigDecimal price = (priceStr != null && !priceStr.isBlank()) ? new BigDecimal(priceStr) : BigDecimal.ZERO;
            Long busId = (busIdStr != null && !busIdStr.isBlank()) ? Long.parseLong(busIdStr) : 0L;
            Integer stockQuantity = (stockStr != null && !stockStr.isBlank()) ? Integer.parseInt(stockStr) : 0;
            Integer campId = (campIdStr != null && !campIdStr.isBlank()) ? Integer.parseInt(campIdStr) : 0;

           //Create the material object containing all values
            Material material = new Material();
            material.setProdId(prodId);
            material.setName(name);
            material.setDescription(description != null ? description : "");
            material.setPrice(price);
            material.setBusId(busId);
            material.setStockQuantity(stockQuantity);
            material.setCampId(campId);

            System.out.println("Material to update:");
            System.out.println("  Stock: " + stockQuantity);
            System.out.println("  Campus: " + campId);

           //Update the material (this will update both product and stock)
            boolean updated = materialService.updateMaterial(material);

            if (!updated){
                throw new IllegalArgumentException("Material could not be updated.");
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?success=Material+updated+successfully"
            );

        } catch (IllegalArgumentException exception){
            exception.printStackTrace();
            System.err.println("Error in updateMaterial: " + exception.getMessage());

           //Get the material for the form
            String prodIdStr = request.getParameter("prodId");
            if (prodIdStr != null && !prodIdStr.isBlank()){
                try{
                    Long prodId = Long.parseLong(prodIdStr);
                    Material material = materialService.getMaterialById(prodId);
                    request.setAttribute("material", material);
                } catch (Exception e){
                   //Ignore
                }
            }

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );
            request.setAttribute("suppliers", materialService.getDistinctSuppliers());
            request.setAttribute("campuses", materialService.getDistinctCampuses());
            request.setAttribute("isEdit", true);
            request.setAttribute("viewMode", "edit");

            request.getRequestDispatcher("/materials/materials.jsp")
                    .forward(request, response);
        }
    }

    private void deleteMaterial(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws SQLException, ServletException, IOException{

        System.out.println("=== deleteMaterial() called ===");

        try{
            Long materialId =
                    parsePositiveLong(
                            request.getParameter("prodId"),
                            "Invalid material ID."
                    );

            boolean deleted = materialService.deleteMaterial(materialId);

            if (!deleted){
                throw new IllegalArgumentException(
                        "Material was not found."
                );
            }

            response.sendRedirect(
                    request.getContextPath()
                    + "/materials?success=Material+deleted+successfully"
            );

        } catch (IllegalArgumentException exception){
            exception.printStackTrace();

            request.setAttribute(
                    "errorMessage",
                    exception.getMessage()
            );

            listMaterials(request, response);
        }
    }

    private Material buildMaterialFromRequest(
            HttpServletRequest request
    ){

        Material material = new Material();

        material.setName(
                request.getParameter("name")
        );

        material.setDescription(
                request.getParameter("description")
        );

        String priceStr = request.getParameter("price");
        try{
            if (priceStr != null && !priceStr.isBlank()){
                material.setPrice(new BigDecimal(priceStr));
            }
        } catch (NumberFormatException exception){
            material.setPrice(BigDecimal.ZERO);
        }

        String busIdStr = request.getParameter("busId");
        try{
            if (busIdStr != null && !busIdStr.isBlank()){
                material.setBusId(Long.parseLong(busIdStr));
            }
        } catch (NumberFormatException exception){
            material.setBusId(0L);
        }

        String stockStr = request.getParameter("stockQuantity");
        try{
            if (stockStr != null && !stockStr.isBlank()){
                material.setStockQuantity(Integer.parseInt(stockStr));
            }
        } catch (NumberFormatException exception){
            material.setStockQuantity(0);
        }

        String campIdStr = request.getParameter("campId");
        try{
            if (campIdStr != null && !campIdStr.isBlank()){
                material.setCampId(Integer.parseInt(campIdStr));
            }
        } catch (NumberFormatException exception){
            material.setCampId(0);
        }

        return material;
    }

    private Long getIdFromRequest(
            HttpServletRequest request
    ){

        return parsePositiveLong(
                request.getParameter("id"),
                "Invalid material ID."
        );
    }

    private Long parsePositiveLong(
            String value,
            String errorMessage
    ){

        if (value == null || value.isBlank()){
            throw new IllegalArgumentException(
                    errorMessage
            );
        }

        try{
            long parsedValue =
                    Long.parseLong(value);

            if (parsedValue <= 0){
                throw new IllegalArgumentException(
                        errorMessage
                );
            }

            return parsedValue;

        } catch (NumberFormatException exception){
            throw new IllegalArgumentException(
                    errorMessage
            );
        }
    }

    private String escapeJson(String value){
        if (value == null){
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}