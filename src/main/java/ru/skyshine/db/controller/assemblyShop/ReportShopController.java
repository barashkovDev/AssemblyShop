package ru.skyshine.db.controller.assemblyShop;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.skyshine.db.model.assemplyShop.ComponentsWarehouse;
import ru.skyshine.db.model.assemplyShop.MonthlyPlan;
import ru.skyshine.db.model.assemplyShop.ProductsWarehouse;
import ru.skyshine.db.model.tradingCompany.Goods;
import ru.skyshine.report.excel.assemplyShop.*;

import java.util.List;

@Controller
@RequestMapping("/assemblyShop")
public class ReportShopController extends BaseShopController {

    @GetMapping("/reports")
    public String listReports(Model model) {
        model.addAttribute("title", "Отчеты");
        return "reports_shop";
    }

    @GetMapping(value = "/download/componentsWarehouse")
    public void exportComponentsWarehouseReport(HttpServletResponse response) {
        try {
            List<ComponentsWarehouse> data = componentsWarehouseRep.findAll(Sort.by("code").ascending());

            ComponentsWarehouseReport report = new ComponentsWarehouseReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }

    @GetMapping(value = "/download/details")
    public void exportDetailsReport(HttpServletResponse response) {
        try {
            List<Object[]> data = detailRep.detailsInfo();

            DetailsReport report = new DetailsReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }

    @GetMapping(value = "/download/products")
    public void exportProductsReport(HttpServletResponse response) {
        try {
            List<Object[]> data = productRep.productsInfo();

            ProductsReport report = new ProductsReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }

    @GetMapping(value = "/download/productsWarehouse")
    public void exportProductsWarehouseReport(HttpServletResponse response) {
        try {
            List<ProductsWarehouse> data = productsWarehouseRep.findAll(Sort.by("code").ascending());

            ProductsWarehouseReport report = new ProductsWarehouseReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }

    @GetMapping(value = "/download/monthlyPlan")
    public void exportMonthlyPlanReport(HttpServletResponse response) {
        try {
            List<MonthlyPlan> data = monthlyPlanRep.findAll(Sort.by("month", "availability").ascending());
            System.out.println(data);

            MonthlyPlanReport report = new MonthlyPlanReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }

    @GetMapping(value = "/download/goodsCount")
    public void exportGoodsCountReport(HttpServletResponse response) {
        try {
            List<Object[]> data = goodsRep.goodsInfo();
            System.out.println(data);

            GoodsReport report = new GoodsReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }

    @GetMapping(value = "/download/goodsMaxCapacity")
    public void exportGoodsMaxCapacityReport(HttpServletResponse response) {
        try {
            List<Object[]> data = goodsRep.goodsMaxCapacity();
            System.out.println(data);

            GoodsMaxCapacityReport report = new GoodsMaxCapacityReport(null, data);
            if (!report.createReport()) {
                System.out.println("Ошибка в создании " + report.getNameFile());
                return;
            }
            report.export(response);
        } catch (Exception e) {
            System.out.println("Ошибка в отправке отчета");
        }
    }
}
