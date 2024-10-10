package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.assemplyShop.ComponentsWarehouse;
import ru.skyshine.db.model.assemplyShop.Detail;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.plugins.CustomFilter.FilterTablePlugin;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class ComponentsWarehouseShopController extends BaseShopController {

    FilterTablePlugin filterTablePlugin;
    private ParamMap paramMap;
    private List<ComponentsWarehouse> components;

    @GetMapping("/componentsWarehouse")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Склад комплектующих");
        model.addAttribute("mode", true);

        paramMap = new ParamMap(List.of("Код ячейки", "Код детали", "Наименование детали", "Количество наличия", "Максимальное количество"),
                List.of("code", "codeDetail.code", "codeDetail.name", "availability", "maximum"));
        filterTablePlugin = new FilterTablePlugin(model, paramMap, "/assemblyShop/componentsWarehouse");

        setComponentsAllData();
        displayTable(model);
        return "components_warehouse";
    }

    @PostMapping(value = "/componentsWarehouse", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter, Model model) {
        filterData(selectTypeFilter, selectColumnFilter, valueFilter);
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(components, paramMap.getParamRef()));
        response.put("result", setResultForFootTable());
        return response;
    }

    @PostMapping(value = "/componentsWarehouse", params = "action=getInfoSelectCW")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {
        ComponentsWarehouse cw = componentsWarehouseRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("detail", cw.getCodeDetail().getCode());
        response.put("availability", cw.getAvailability());
        response.put("maximum", cw.getMaximum());
        return response;
    }

    @PostMapping(value = "/componentsWarehouse", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/componentsWarehouse";
    }

    @PostMapping(value = "/componentsWarehouse", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeCW, String dropdownChangeDetail, String changeAvailability, String changeMaximum) {
        ComponentsWarehouse existRecord = componentsWarehouseRep.getById(Integer.valueOf(dropdownChangeCW));
        Detail detail = detailRep.getById(Integer.valueOf(dropdownChangeDetail));
        if (existRecord.getCodeDetail() != detail)
            existRecord.setCodeDetail(detail);
        if (!changeAvailability.isEmpty())
            existRecord.setAvailability(Integer.valueOf(changeAvailability));
        if (!changeMaximum.isEmpty())
            existRecord.setMaximum(Integer.valueOf(changeMaximum));
        componentsWarehouseRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/componentsWarehouse", params = "action=add")
    public String addRecord(@RequestParam String dropdownAddDetail, String addAvailability, String addMaximum) {
        ComponentsWarehouse newRecord = new ComponentsWarehouse();
        newRecord.setCodeDetail(detailRep.getById(Integer.valueOf(dropdownAddDetail)));
        if (!addAvailability.isEmpty())
            newRecord.setAvailability(Integer.valueOf(addAvailability));
        if (!addMaximum.isEmpty())
            newRecord.setMaximum(Integer.valueOf(addMaximum));
        componentsWarehouseRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/componentsWarehouse", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(componentsWarehouseRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(components, paramMap.getParamRef()));
        model.addAttribute("result", setResultForFootTable());
        model.addAttribute("allEntryThisModel", components.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getCode());
                    map.put("text", component.getCode());
                    return map;
                })
                .collect(Collectors.toList()));
        model.addAttribute("allDetail", detailRep.findAll(Sort.by("code").ascending()).stream()
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
        int totalCurrentQuantity = 0;
        for (ComponentsWarehouse component : components) {
            totalCurrentQuantity += component.getAvailability() != null ? component.getAvailability() : 0;
        }
        result.add(Integer.toString(totalCurrentQuantity));

        int totalMaximumQuantity = 0;
        for (ComponentsWarehouse component : components) {
            totalMaximumQuantity += component.getMaximum() != null ? component.getMaximum() : 0;
        }
        result.add(Integer.toString(totalMaximumQuantity));
        return result;
    }

    private void setComponentsAllData() {
        components = componentsWarehouseRep.findAll(Sort.by("code").ascending());
    }

    private void filterData(String selectTypeFilter, String selectColumnFilter, String valueFilter) {
        setComponentsAllData();
        components = filterTablePlugin.getFilterData(components, selectTypeFilter, selectColumnFilter, valueFilter);
    }
}
