package za.bc.cleaninginventory.controller.report;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import za.bc.cleaninginventory.service.report.ReportService;

import java.io.IOException;

@WebServlet("/reports")
public class ReportServlet extends HttpServlet {

    private ReportService reportService;

    @Override
    public void init() {
        reportService = new ReportService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String type = request.getParameter("type");

        if (type == null || type.isBlank()) {
            type = "products";
        }

        switch (type) {

            case "requests":
                request.setAttribute("requests",
                        reportService.getRequestsReport());
                break;

            case "orders":
                request.setAttribute("orders",
                        reportService.getOrdersReport());
                break;

            case "stock":
                request.setAttribute("stock",
                        reportService.getStockReport());
                break;

            case "suppliers":
                request.setAttribute("suppliers",
                        reportService.getSuppliersReport());
                break;

            default:
                request.setAttribute("products",
                        reportService.getProductsReport());
                type = "products";
                break;
        }

        request.setAttribute("reportType", type);

        request.getRequestDispatcher("/reports/reports.jsp")
               .forward(request, response);
    }
}