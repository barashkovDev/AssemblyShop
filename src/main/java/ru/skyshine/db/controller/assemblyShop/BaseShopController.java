package ru.skyshine.db.controller.assemblyShop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Controller;
import ru.skyshine.db.repository.assemplyShop.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BaseShopController {
    @Autowired
    ComponentsWarehouseRep componentsWarehouseRep;
    @Autowired
    DetailRep detailRep;
    @Autowired
    ProductsWarehouseRep productsWarehouseRep;
    @Autowired
    ProductRep productRep;
    @Autowired
    ManufacturingRep manufacturingRep;
    @Autowired
    MonthlyPlanRep monthlyPlanRep;
    @Autowired
    MarketRep marketRep;
    @Autowired
    GoodsRep goodsRep;
    @Autowired
    StorageRep storageRep;
    @Autowired
    ContentRep contentRep;

    protected Map<String, Object> deleteRecord(CrudRepository rep, String delIdEntry) {
        Map<String, Object> response = new HashMap<>();
        try {
            rep.deleteById(Integer.valueOf(delIdEntry));
            response.put("success", "true");
        } catch (Exception e) {
            response.put("success", "false");
        }
        return response;
    }
}
