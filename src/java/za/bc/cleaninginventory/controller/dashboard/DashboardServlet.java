package za.bc.cleaninginventory.controller.dashboard;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import za.bc.cleaninginventory.model.dto.DashboardDTO;
import za.bc.cleaninginventory.service.dashboard.DashboardService;

import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private DashboardService dashboardService;

    @Override
    public void init() throws ServletException {

        dashboardService = new DashboardService();

    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        DashboardDTO dashboard = dashboardService.getDashboardStatistics();

        request.setAttribute("dashboard", dashboard);

        request.getRequestDispatcher("/dashboard/dashboard.jsp")
                .forward(request, response);

    }

}