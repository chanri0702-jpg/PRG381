package za.bc.cleaninginventory.service.material;

import za.bc.cleaninginventory.model.dao.material.MaterialDAO;
import za.bc.cleaninginventory.model.entity.Material;
import za.bc.cleaninginventory.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;

public class MaterialService{
    
    private MaterialDAO materialDAO;
    
    public MaterialService(){
        this.materialDAO = new MaterialDAO();
    }
    
   //Create material with validation
    public boolean createMaterial(Material material){
       //Validate material before creation
        if (!validateMaterial(material)){
            return false;
        }
        
       //Check for duplicate name
        if (isMaterialNameExists(material.getName(), null)){
            return false;
        }
        
        return materialDAO.createMaterial(material);
    }
    
   //Get material by ID
    public Material getMaterialById(Long prodId){
        if (prodId == null || prodId <= 0){
            return null;
        }
        return materialDAO.getMaterialById(prodId);
    }
    
   //Get all materials
    public List<Material> getAllMaterials(){
        return materialDAO.getAllMaterials();
    }
    
   //Search materials with filters
    public List<Material> searchMaterials(String searchTerm, String supplierId, String campusId){
        return materialDAO.searchMaterials(searchTerm, supplierId, campusId);
    }
    
   //Get low stock materials
    public List<Material> getLowStockMaterials(){
        return materialDAO.getLowStockMaterials();
    }
    
   //Update material with validation - FIXED
    public boolean updateMaterial(Material material){
       //Validate material before update
        if (!validateMaterial(material)){
            System.out.println("Material validation failed");
            return false;
        }
        
       //Check for duplicate name (excluding current material)
        if (isMaterialNameExists(material.getName(), material.getProdId())){
            System.out.println("Duplicate name found");
            return false;
        }
        
       //First update the product details
        boolean productUpdated = materialDAO.updateMaterial(material);
        System.out.println("Product updated: " + productUpdated);
        
        if (!productUpdated){
            return false;
        }
        
       //Then update stock quantity if provided
        boolean stockUpdated = true;
        if (material.getStockQuantity() != null && material.getCampId() != null && material.getCampId() > 0){
            stockUpdated = materialDAO.updateStockQuantity(
                material.getProdId(), 
                material.getCampId(), 
                material.getStockQuantity()
            );
            System.out.println("Stock updated: " + stockUpdated);
        }
        
        return productUpdated && stockUpdated;
    }
    
   //Delete material
    public boolean deleteMaterial(Long prodId){
        if (prodId == null || prodId <= 0){
            return false;
        }
        
       //Check if material exists
        Material existingMaterial = materialDAO.getMaterialById(prodId);
        if (existingMaterial == null){
            return false;
        }
        
        return materialDAO.deleteMaterial(prodId);
    }
    
   //Update stock quantity with validation
    public boolean updateStockQuantity(Long prodId, Integer campId, int newQuantity){
        if (prodId == null || prodId <= 0 || campId == null || campId <= 0){
            return false;
        }
        
        if (newQuantity < 0){
            return false;
        }
        
        return materialDAO.updateStockQuantity(prodId, campId, newQuantity);
    }
    
   //Get distinct suppliers
    public List<MaterialDAO.Supplier> getDistinctSuppliers(){
        return materialDAO.getDistinctSuppliers();
    }
    
   //Get distinct campuses
    public List<MaterialDAO.Campus> getDistinctCampuses(){
        return materialDAO.getDistinctCampuses();
    }
    
   //Get total material count
    public int getTotalMaterialCount(){
        return materialDAO.getTotalMaterialCount();
    }
    
   //Get low stock count
    public int getLowStockCount(){
        return materialDAO.getLowStockCount();
    }
    
   //Check if material name exists (for duplicate validation)
    public boolean isMaterialNameExists(String name, Long excludeProdId){
        List<Material> materials = materialDAO.getAllMaterials();
        return materials.stream()
                .anyMatch(m -> m.getName().equalsIgnoreCase(name) && 
                              (excludeProdId == null || !m.getProdId().equals(excludeProdId)));
    }
    
   //Validate material
    private boolean validateMaterial(Material material){
       //Check for null
        if (material == null){
            return false;
        }
        
       //Validate name (required, min 2 chars, max 150 chars)
        if (!ValidationUtil.isValidString(material.getName(), 2, 150)){
            return false;
        }
        
       //Validate description (optional, max 1000 chars)
        if (material.getDescription() != null && 
            !ValidationUtil.isValidString(material.getDescription(), 0, 1000)){
            return false;
        }
        
       //Validate price (must be positive)
        if (material.getPrice() == null || 
            !ValidationUtil.isValidPositive(material.getPrice().doubleValue())){
            return false;
        }
        
       //Validate supplier ID (must be positive)
        if (material.getBusId() == null || material.getBusId() <= 0){
            return false;
        }
        
       //Stock is optional for validation (can be null)
        if (material.getStockQuantity() != null && 
            material.getStockQuantity() < 0){
            return false;
        }
        
        return true;
    }
    
   //Check if material has sufficient stock
    public boolean hasSufficientStock(Long prodId, Integer campId, int requestedQuantity){
        Material material = getMaterialById(prodId);
        if (material == null || material.getCampId() == null || !material.getCampId().equals(campId)){
            return false;
        }
        return material.hasStock(requestedQuantity);
    }
    
   //Deduct stock (used when issuing materials)
    public boolean deductStock(Long prodId, Integer campId, int quantity){
        Material material = getMaterialById(prodId);
        if (material == null || !material.hasStock(quantity) || material.getCampId() == null){
            return false;
        }
        
        material.deductStock(quantity);
        return materialDAO.updateStockQuantity(prodId, campId, material.getStockQuantity());
    }
    
   //Add stock (applicable when restocking)
    public boolean addStock(Long prodId, Integer campId, int quantity){
        Material material = getMaterialById(prodId);
        if (material == null || quantity <= 0 || material.getCampId() == null){
            return false;
        }
        
        material.addStock(quantity);
        return materialDAO.updateStockQuantity(prodId, campId, material.getStockQuantity());
    }
}