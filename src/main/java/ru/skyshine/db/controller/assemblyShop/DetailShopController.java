package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.assemplyShop.Detail;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.plugins.CustomFilter.FilterTablePlugin;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class DetailShopController extends BaseShopController {
    FilterTablePlugin filterTablePlugin;
    private ParamMap paramMap;
    private List<Detail> details;

    @GetMapping("/details")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Детали");
        model.addAttribute("mode", true);

        paramMap = new ParamMap(List.of("Код", "Наименование", "Материал", "Вес", "Цвет", "Цена", "Описание"),
                List.of("code", "name", "material", "weight", "color", "price", "description"));
        filterTablePlugin = new FilterTablePlugin(model, paramMap, "/assemblyShop/markets");

        setDetailsAllData();
        displayTable(model);
        return "details";
    }

    @PostMapping(value = "/details", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        filterData(selectTypeFilter, selectColumnFilter, valueFilter);
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(details, paramMap.getParamRef()));
        return response;
    }

    @PostMapping(value = "/details", params = "action=getInfoSelectDetail")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {
        Detail detail = detailRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("name", detail.getName());
        response.put("material", detail.getMaterial());
        response.put("weight", detail.getWeight());
        response.put("color", detail.getColor());
        response.put("price", detail.getPrice());
        response.put("description", detail.getDescription());
        return response;
    }

    @PostMapping(value = "/details", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/details";
    }

    @PostMapping(value = "/details", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeDetail, String changeName, String changeMaterial, String changeWeight,
                               String changeColor, String changePrice, String changeDescription) {
        Detail existRecord = detailRep.getById(Integer.valueOf(dropdownChangeDetail));
        if (!changeName.isEmpty())
            existRecord.setName(changeName);
        if (!changeMaterial.isEmpty())
            existRecord.setMaterial(changeMaterial);
        if (!changeWeight.isEmpty())
            existRecord.setWeight(Double.valueOf(changeWeight));
        if (!changeColor.isEmpty())
            existRecord.setColor(changeColor);
        if (!changePrice.isEmpty())
            existRecord.setPrice(Double.valueOf(changePrice));
        if (!changeDescription.isEmpty())
            existRecord.setDescription(changeDescription);
        detailRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/details", params = "action=add")
    public String addRecord(@RequestParam String addName, String addMaterial, String addWeight,
                            String addColor, String addPrice, String addDescription) {
        Detail newRecord = new Detail();
        if (!addName.isEmpty())
            newRecord.setName(addName);
        if (!addMaterial.isEmpty())
            newRecord.setMaterial(addMaterial);
        if (!addWeight.isEmpty())
            newRecord.setWeight(Double.valueOf(addWeight));
        if (!addColor.isEmpty())
            newRecord.setColor(addColor);
        if (!addPrice.isEmpty())
            newRecord.setPrice(Double.valueOf(addPrice));
        if (!addDescription.isEmpty())
            newRecord.setDescription(addDescription);
        detailRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/details", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(detailRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(details, paramMap.getParamRef()));
        model.addAttribute("allEntryThisModel", details.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getCode());
                    map.put("text", component.getCode());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private void setDetailsAllData() {
        details = detailRep.findAll(Sort.by("code").ascending());
    }

    private void filterData(String selectTypeFilter, String selectColumnFilter, String valueFilter) {
        setDetailsAllData();
        details = filterTablePlugin.getFilterData(details, selectTypeFilter, selectColumnFilter, valueFilter);
    }
}
