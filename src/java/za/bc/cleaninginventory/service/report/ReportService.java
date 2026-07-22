package za.bc.cleaninginventory.service.report;

import za.bc.cleaninginventory.model.dao.report.ReportDAO;
import za.bc.cleaninginventory.model.dto.ReportDTO;

import java.util.List;

public class ReportService {

    private final ReportDAO reportDAO = new ReportDAO();

    public List<ReportDTO> getProductsReport() {

        return reportDAO.getProductsReport();

    }

    public List<ReportDTO> getRequestsReport() {

        return reportDAO.getRequestsReport();

    }
    
    public List<ReportDTO> getStockReport() {

        return reportDAO.getStockReport();

    }
    
    public List<ReportDTO> getSuppliersReport() {

        return reportDAO.getSuppliersReport();

    }
    
    public List<ReportDTO> getOrdersReport() {

        return reportDAO.getOrdersReport();

    }
}