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
    
    //Create the material with validation added
    public boolean createMaterial(Material material){
        // Validate material before its creation
        if (!validateMaterial(material)){
            return false;
        }
        
        //Check for any possible duplicate names
        if (isMaterialNameExists(material.getName(), null)){
            return false;
        }
        
        return materialDAO.createMaterial(material);
    }
    
    //Get the materials ID
    public Material getMaterialById(Long prodId){
        if (prodId == null || prodId <= 0){
            return null;
        }
        return materialDAO.getMaterialById(prodId);
    }
    
    //Get all the materials saved
    public List<Material> getAllMaterials(){
        return materialDAO.getAllMaterials();
    }
    
    //Search for materials with filters applied
    public List<Material> searchMaterials(String searchTerm, String supplierId, String campusId){
        return materialDAO.searchMaterials(searchTerm, supplierId, campusId);
    }
    
    //Display all low stock materials
    public List<Material> getLowStockMaterials(){
        return materialDAO.getLowStockMaterials();
    }
    
    //Update the materials with validation
    public boolean updateMaterial(Material material){
        // Validate material before update
        if (!validateMaterial(material)){
            return false;
        }
        
        //Check for duplicate name (excluding current material) saved
        if (isMaterialNameExists(material.getName(), material.getProdId())){
            return false;
        }
        
        return materialDAO.updateMaterial(material);
    }
    
    //Delete the selected material
    public boolean deleteMaterial(Long prodId){
        if (prodId == null || prodId <= 0){
            return false;
        }
        
        //Check if the material exists
        Material existingMaterial = materialDAO.getMaterialById(prodId);
        if (existingMaterial == null){
            return false;
        }
        
        return materialDAO.deleteMaterial(prodId);
    }
    
    //Update stocks quantity with validation
    public boolean updateStockQuantity(Long prodId, Integer campId, int newQuantity){
        if (prodId == null || prodId <= 0 || campId == null || campId <= 0){
            return false;
        }
        
        if (newQuantity < 0){
            return false;
        }
        
        return materialDAO.updateStockQuantity(prodId, campId, newQuantity);
    }
    
    //Get distinct (unique) suppliers
    public List<MaterialDAO.Supplier> getDistinctSuppliers(){
        return materialDAO.getDistinctSuppliers();
    }
    
    //Get distinct (unique) campuses
    public List<MaterialDAO.Campus> getDistinctCampuses(){
        return materialDAO.getDistinctCampuses();
    }
    
    //Get total material by count
    public int getTotalMaterialCount(){
        return materialDAO.getTotalMaterialCount();
    }
    
    //Get low stock total by count
    public int getLowStockCount(){
        return materialDAO.getLowStockCount();
    }
    
    //Check if the material name exists (for duplicate validation)
    public boolean isMaterialNameExists(String name, Long excludeProdId){
        List<Material> materials = materialDAO.getAllMaterials();
        return materials.stream()
                .anyMatch(m -> m.getName().equalsIgnoreCase(name) && 
                              (excludeProdId == null || !m.getProdId().equals(excludeProdId)));
    }
    
    //Validation for material
    private boolean validateMaterial(Material material){
        // Check for null
        if (material == null){
            return false;
        }
        
        //Validate material name (required, min 2 chars, max 150 chars)
        if (!ValidationUtil.isValidString(material.getName(), 2, 150)){
            return false;
        }
        
        //Validate the description (optional, max 1000 chars)
        if (material.getDescription() != null && 
            !ValidationUtil.isValidString(material.getDescription(), 0, 1000)){
            return false;
        }
        
        //Validate the price (must be positive)
        if (material.getPrice() == null || 
            !ValidationUtil.isValidPositive(material.getPrice().doubleValue())){
            return false;
        }
        
        //Validate the supplier ID (must be positive)
        if (material.getBusId() == null || material.getBusId() <= 0){
            return false;
        }
        
        //Validate the stock quantity (must be non-negative)
        if (material.getStockQuantity() != null && 
            !ValidationUtil.isValidPositiveOrZero(material.getStockQuantity())){
            return false;
        }
        
        return true;
    }
    
    //Check if the material has sufficient stock available
    public boolean hasSufficientStock(Long prodId, Integer campId, int requestedQuantity){
        Material material = getMaterialById(prodId);
        if (material == null || material.getCampId() == null || !material.getCampId().equals(campId)){
            return false;
        }
        return material.hasStock(requestedQuantity);
    }
    
    //Deduct total stock (used when issuing materials)
    public boolean deductStock(Long prodId, Integer campId, int quantity){
        Material material = getMaterialById(prodId);
        if (material == null || !material.hasStock(quantity) || material.getCampId() == null){
            return false;
        }
        
        material.deductStock(quantity);
        return materialDAO.updateStockQuantity(prodId, campId, material.getStockQuantity());
    }
    
    //Add to stock (used when restocking)
    public boolean addStock(Long prodId, Integer campId, int quantity){
        Material material = getMaterialById(prodId);
        if (material == null || quantity <= 0 || material.getCampId() == null){
            return false;
        }
        
        material.addStock(quantity);
        return materialDAO.updateStockQuantity(prodId, campId, material.getStockQuantity());
    }
}