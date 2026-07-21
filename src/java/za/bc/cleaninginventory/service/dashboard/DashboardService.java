package za.bc.cleaninginventory.service.dashboard;

import za.bc.cleaninginventory.model.dao.dashboard.DashboardDAO;
import za.bc.cleaninginventory.model.dto.DashboardDTO;
import za.bc.cleaninginventory.model.dto.LowStockDTO;
import za.bc.cleaninginventory.model.dto.RecentRequestDTO;

import java.util.List;

public class DashboardService {

    private final DashboardDAO dashboardDAO = new DashboardDAO();

    public DashboardDTO getDashboardStatistics() {
        return dashboardDAO.getDashboardStatistics();
    }

}