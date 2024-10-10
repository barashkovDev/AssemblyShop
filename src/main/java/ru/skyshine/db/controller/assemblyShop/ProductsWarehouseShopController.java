package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.assemplyShop.Product;
import ru.skyshine.db.model.assemplyShop.ProductsWarehouse;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.plugins.CustomFilter.FilterTablePlugin;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class ProductsWarehouseShopController extends BaseShopController {

    FilterTablePlugin filterTablePlugin;
    private ParamMap paramMap;
    private List<ProductsWarehouse> products;

    @GetMapping("/productsWarehouse")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Склад изделий");
        model.addAttribute("mode", true);

        paramMap = new ParamMap(List.of("Код ячейки", "Код изделия", "Наименование изделия", "Количество наличия", "Максимальное количество"),
                List.of("code", "codeProduct.code", "codeProduct.name", "availability", "maximum"));
        filterTablePlugin = new FilterTablePlugin(model, paramMap, "/assemblyShop/productsWarehouse");

        setComponentsAllData();
        displayTable(model);
        return "products_warehouse";
    }

    @PostMapping(value = "/productsWarehouse", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        filterData(selectTypeFilter, selectColumnFilter, valueFilter);
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(products, paramMap.getParamRef()));
        response.put("result", setResultForFootTable());
        return response;
    }

    @PostMapping(value = "/productsWarehouse", params = "action=getInfoSelectPW")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {
        ProductsWarehouse pw = productsWarehouseRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("productList", pw.getCodeProduct().getCode());
        response.put("availability", pw.getAvailability());
        response.put("maximum", pw.getMaximum());
        return response;
    }

    @PostMapping(value = "/productsWarehouse", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/productsWarehouse";
    }

    @PostMapping(value = "/productsWarehouse", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangePW, String dropdownChangeProduct, String changeAvailability, String changeMaximum) {
        ProductsWarehouse existRecord = productsWarehouseRep.getById(Integer.valueOf(dropdownChangePW));
        Product product = productRep.getById(Integer.valueOf(dropdownChangeProduct));
        if (existRecord.getCodeProduct() != product)
            existRecord.setCodeProduct(product);
        if (!changeAvailability.isEmpty())
            existRecord.setAvailability(Integer.valueOf(changeAvailability));
        if (!changeMaximum.isEmpty())
            existRecord.setMaximum(Integer.valueOf(changeMaximum));
        productsWarehouseRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/productsWarehouse", params = "action=add")
    public String addRecord(@RequestParam String dropdownAddProduct, String addAvailability, String addMaximum) {
        ProductsWarehouse newRecord = new ProductsWarehouse();
        newRecord.setCodeProduct(productRep.getById(Integer.valueOf(dropdownAddProduct)));
        if (!addAvailability.isEmpty())
            newRecord.setAvailability(Integer.valueOf(addAvailability));
        if (!addMaximum.isEmpty())
            newRecord.setMaximum(Integer.valueOf(addMaximum));
        productsWarehouseRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/productsWarehouse", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(productsWarehouseRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(products, paramMap.getParamRef()));
        model.addAttribute("result", setResultForFootTable());
        model.addAttribute("allEntryThisModel", products.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getCode());
                    map.put("text", component.getCode());
                    return map;
                })
                .collect(Collectors.toList()));
        model.addAttribute("allProducts", productRep.findAll(Sort.by("code").ascending()).stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getCode());
                    map.put("text", component.getName());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private List<String> setResultForFootTable() {
        List<String> result = new ArrayList<>(Arrays.asList("", "", ""));
        int totalCurrentQuantity = 0, totalMaximumQuantity = 0;
        for (ProductsWarehouse component : products) {
            totalCurrentQuantity += component.getAvailability() != null ? component.getAvailability() : 0;
            totalMaximumQuantity += component.getMaximum() != null ? component.getMaximum() : 0;
        }
        result.add(Integer.toString(totalCurrentQuantity));
        result.add(Integer.toString(totalMaximumQuantity));
        return result;
    }

    private void setComponentsAllData() {
        products = productsWarehouseRep.findAll(Sort.by("code").ascending());
    }

    private void filterData(String selectTypeFilter, String selectColumnFilter, String valueFilter) {
        setComponentsAllData();
        products = filterTablePlugin.getFilterData(products, selectTypeFilter, selectColumnFilter, valueFilter);
    }
}
