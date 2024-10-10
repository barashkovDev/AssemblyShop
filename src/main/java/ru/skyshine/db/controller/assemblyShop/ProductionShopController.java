package ru.skyshine.db.controller.assemblyShop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.assemplyShop.ComponentsWarehouse;
import ru.skyshine.db.model.assemplyShop.ProductsWarehouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class ProductionShopController extends BaseShopController {
    private List<Object[]> production;
    private Integer curScheme;

    @GetMapping("/production")
    public String startPage(Model model) {
        model.addAttribute("title", "Производство");
        model.addAttribute("mode", true);

        production = manufacturingRep.getProduction();
        displayTable(model);
        setProductionProduct(model);
        return "production";
    }

    @PostMapping(value = "/production", params = "action=produceInfo")
    @ResponseBody
    public Map<String, Object> getProduceInfoByProductId(@RequestParam String scheme) {
        this.curScheme = Integer.valueOf(scheme);
        Map<String, Object> response = new HashMap<>();
        List<Object[]> infoDetailScheme = manufacturingRep.getProduceInfoByProductId(this.curScheme);
        Object maximumWarehouseVolume = productsWarehouseRep.getMaximumWarehouseVolumeProductId(this.curScheme);
        int maximumProduction = Integer.parseInt(maximumWarehouseVolume.toString());
        int curDetailMax = 0;
        for (Object[] info : infoDetailScheme) {
            curDetailMax = Integer.parseInt(info[3].toString()) / Integer.parseInt(info[2].toString());
            maximumProduction = Math.min(curDetailMax, maximumProduction);
            if (maximumProduction == 0)
                break;
        }
        response.put("infoSchemeTable", infoDetailScheme);
        response.put("maximumProduction", maximumProduction); //идет в рассчет свободные ячейки и доступные материалы
        return response;
    }

    @PostMapping(value = "/production", params = "action=produce")
    @ResponseBody
    public Map<String, Object> getProduce(@RequestParam String amount) {
        int needAmountAdd = Integer.parseInt(amount);
        Map<String, Object> response = new HashMap<>();
        int needAmount = 0, maxSubFromCell = 0, maxAddToCell = 0;
        ComponentsWarehouse curCW = null;
        // Удаление деталей со склада для изготовления
        for (Object[] curDetail : manufacturingRep.getDetailAndAmountForProductId(curScheme)) { // [0] - код, [1] - необходимое количество
            needAmount = Integer.parseInt(curDetail[1].toString()) * needAmountAdd;
            List<ComponentsWarehouse> listDetailCells = componentsWarehouseRep.getComponentsWarehouseForDetail(Integer.parseInt(curDetail[0].toString()));
            for (int i = 0; i < listDetailCells.size() && needAmount != 0; i++) {
                curCW = listDetailCells.get(i);
                maxSubFromCell = Math.min(needAmount, curCW.getAvailability());
                curCW.setAvailability(curCW.getAvailability() - maxSubFromCell);
                needAmount -= maxSubFromCell;
            }
            componentsWarehouseRep.saveAll(listDetailCells);
        }
        List<ProductsWarehouse> allFreeCell = productsWarehouseRep.getFreeWarehouseCellsByCodeProduct(curScheme);
        ProductsWarehouse curPW = null;
        for (int i = 0; i < allFreeCell.size() && needAmountAdd != 0; i++) {
            curPW = allFreeCell.get(i);
            maxAddToCell = Math.min(needAmountAdd, curPW.getMaximum());
            curPW.setAvailability(curPW.getAvailability() + maxAddToCell);
            needAmountAdd -= maxAddToCell;
        }
        productsWarehouseRep.saveAll(allFreeCell);
        //обновление таблицы "Изготовление
        production = manufacturingRep.getProduction();
        response.put("production", production);
        return response;
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", List.of("Код", "Наименование", "Текущее количество", "Объем склада", "Свободное количество ячеек"));
        model.addAttribute("dataTable", production);
    }

    private void setProductionProduct(Model model) {
        model.addAttribute("allProduction", production.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", row[0]);
                    map.put("text", row[1]);
                    return map;
                })
                .collect(Collectors.toList()));
    }
}
