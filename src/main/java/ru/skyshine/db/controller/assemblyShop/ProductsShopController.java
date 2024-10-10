package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.assemplyShop.CompositeKeys.KeyManufacturing;
import ru.skyshine.db.model.assemplyShop.Detail;
import ru.skyshine.db.model.assemplyShop.Manufacturing;
import ru.skyshine.db.model.assemplyShop.Product;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.plugins.CustomFilter.FilterTablePlugin;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class ProductsShopController extends BaseShopController {
    FilterTablePlugin filterTablePlugin;
    private ParamMap paramMapDetails;
    private ParamMap paramMapScheme;
    private List<Product> products;
    //Для детализации "Схема"
    private Product productScheme;
    private Detail detailScheme;

    @GetMapping("/products")
    public String startPage(Model model) {
        model.addAttribute("title", "Изделия");
        model.addAttribute("mode", true);

        paramMapDetails = new ParamMap(List.of("Код", "Наименование", "Вес", "Цвет", "Цена", "Описание"),
                List.of("code", "name", "weight", "color", "price", "description"));

        paramMapScheme = new ParamMap(List.of("Код детали", "Наименование детали", "Количество"),
                List.of("codeDetail.code", "codeDetail.name", "need"));

        filterTablePlugin = new FilterTablePlugin(model, paramMapDetails, "/assemblyShop/products");

        setProductsAllData();
        displayTable(model);
        return "products";
    }

    @PostMapping(value = "/products", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        filterData(selectTypeFilter, selectColumnFilter, valueFilter);
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(products, paramMapDetails.getParamRef()));
        return response;
    }

    @PostMapping(value = "/products", params = "action=getInfoSelectProduct")
    @ResponseBody
    public Map<String, Object> getInfoSelectProduct(@RequestParam String id) {
        Product product = productRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("name", product.getName());
        response.put("weight", product.getWeight());
        response.put("color", product.getColor());
        response.put("price", product.getPrice());
        response.put("description", product.getDescription());
        return response;
    }

    @PostMapping(value = "/products", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/products";
    }

    @PostMapping(value = "/products", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeProduct, String changeName, String changeWeight,
                               String changeColor, String changePrice, String changeDescription) {
        Product existRecord = productRep.getById(Integer.valueOf(dropdownChangeProduct));
        if (!changeName.isEmpty())
            existRecord.setName(changeName);
        if (!changeWeight.isEmpty())
            existRecord.setWeight(Double.valueOf(changeWeight));
        if (!changeColor.isEmpty())
            existRecord.setColor(changeColor);
        if (!changePrice.isEmpty())
            existRecord.setPrice(Double.valueOf(changePrice));
        if (!changeDescription.isEmpty())
            existRecord.setDescription(changeDescription);
        productRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/products", params = "action=add")
    public String addRecord(@RequestParam String addName, String addWeight,
                            String addColor, String addPrice, String addDescription) {
        Product newRecord = new Product();
        if (!addName.isEmpty())
            newRecord.setName(addName);
        if (!addWeight.isEmpty())
            newRecord.setWeight(Double.valueOf(addWeight));
        if (!addColor.isEmpty())
            newRecord.setColor(addColor);
        if (!addPrice.isEmpty())
            newRecord.setPrice(Double.valueOf(addPrice));
        if (!addDescription.isEmpty())
            newRecord.setDescription(addDescription);
        productRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/products", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(productRep, delIdEntry);
    }

    @PostMapping(value = "/products", params = "action=getScheme")
    @ResponseBody
    public Map<String, Object> getSchemeSelectProduct(@RequestParam String scheme) {
        productScheme = productRep.getById(Integer.valueOf(scheme));
        List<Manufacturing> products = manufacturingRep.getAllByProduct(productScheme);
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(products, paramMapScheme.getParamRef()));
        response.put("detailsForAdd", manufacturingRep.getAvailableAddDetail(Integer.valueOf(scheme)));
        return response;
    }

    @PostMapping(value = "/products", params = "action=getDetailForScheme")
    @ResponseBody
    public Map<String, Object> getInfoDetailSchema(@RequestParam String detail) {
        detailScheme = detailRep.getById(Integer.valueOf(detail));
        Map<String, Object> response = new HashMap<>();
        response.put("need", manufacturingRep.getById(new KeyManufacturing(productScheme, detailScheme)).getNeed());
        return response;
    }

    @PostMapping(value = "/products", params = "action=changeSchema")
    @ResponseBody
    public Map<String, Object> changeSchemeRecord(@RequestParam String changeNeed) {
        Manufacturing existRecord = manufacturingRep.getById(new KeyManufacturing(productScheme, detailScheme));
        if (!changeNeed.isEmpty())
            existRecord.setNeed(Integer.valueOf(changeNeed));
        manufacturingRep.save(existRecord);
        return getSchemeSelectProduct(productScheme.getCode().toString());
    }

    @PostMapping(value = "/products", params = "action=addSchema")
    @ResponseBody
    public Map<String, Object> addSchemeRecord(@RequestParam String addDetail, String addNeed) {
        Manufacturing newRecord = new Manufacturing(
                productRep.getById(productScheme.getCode()), detailRep.getById(Integer.valueOf(addDetail)), Integer.valueOf(addNeed));
        manufacturingRep.save(newRecord);
        return getSchemeSelectProduct(productScheme.getCode().toString());
    }

    @PostMapping(value = "/products", params = "action=deleteSchema")
    @ResponseBody
    public Map<String, Object> deleteSchemeRecord(@RequestParam String delDetail) {
        manufacturingRep.deleteById(new KeyManufacturing(
                productRep.getById(productScheme.getCode()), detailRep.getById(Integer.valueOf(delDetail))));
        return getSchemeSelectProduct(productScheme.getCode().toString());
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMapDetails.getParamName());
        model.addAttribute("dataTable", Converter.objToString(products, paramMapDetails.getParamRef()));
        model.addAttribute("allEntryThisModel", products.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getCode());
                    map.put("text", component.getCode());
                    return map;
                })
                .collect(Collectors.toList()));
        model.addAttribute("nameColumnsSchemeTable", paramMapScheme.getParamName());
        model.addAttribute("allScheme", products.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getCode());
                    map.put("text", String.format("%d - %s", component.getCode(), component.getName()));
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private void setProductsAllData() {
        products = productRep.findAll(Sort.by("code").ascending());
    }

    private void filterData(String selectTypeFilter, String selectColumnFilter, String valueFilter) {
        setProductsAllData();
        products = filterTablePlugin.getFilterData(products, selectTypeFilter, selectColumnFilter, valueFilter);
    }
}

