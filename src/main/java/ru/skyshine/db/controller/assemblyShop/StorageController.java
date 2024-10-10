package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.tradingCompany.Goods;
import ru.skyshine.db.model.tradingCompany.Storage;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class StorageController extends BaseShopController {
    private ParamMap paramMap;
    private List<Storage> storages;

    @GetMapping("/storages")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Склад");
        model.addAttribute("mode", true);
        model.addAttribute("allEntryGoodsModel", goodsRep.findAll().stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));

        paramMap = new ParamMap(List.of("Код ячейки", "Наименование товара", "Количество", "Максимальное количество"),
                List.of("id", "idGoods.name", "quantity", "maxQuantity"));

        setGoodsAllData();
        displayTable(model);
        return "storages";
    }

    @PostMapping(value = "/storages", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(storages, paramMap.getParamRef()));
        return response;
    }

    @PostMapping(value = "/storages", params = "action=getInfoSelectStorages")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {

        Storage storage = storageRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("name", storage.getIdGoods());
        response.put("quantity", storage.getQuantity());
        response.put("max_quantity", storage.getMaxQuantity());
        return response;
    }

    @PostMapping(value = "/storages", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/storages";
    }

    @PostMapping(value = "/storages", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeStorages, String dropdownChangeGoods, String changeQuantity, String changeMaxQuantity) {
        Storage existRecord = storageRep.getById(Integer.valueOf(dropdownChangeStorages));
        if (!dropdownChangeGoods.isEmpty())
            existRecord.setIdGoods(goodsRep.getById(Integer.valueOf(dropdownChangeGoods)));
        if (!changeQuantity.isEmpty())
            existRecord.setQuantity(Integer.valueOf(changeQuantity));
        if (!changeMaxQuantity.isEmpty())
            existRecord.setMaxQuantity(Integer.valueOf(changeMaxQuantity));
        storageRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/storages", params = "action=add")
    public String addRecord(@RequestParam String dropdownAddGoods, String addQuantity, String addMaxQuantity) {
        Storage newRecord = new Storage();
        if (!dropdownAddGoods.isEmpty())
            newRecord.setIdGoods(goodsRep.getById(Integer.valueOf(dropdownAddGoods)));
        if (!addQuantity.isEmpty())
            newRecord.setQuantity(Integer.valueOf(addQuantity));
        if (!addMaxQuantity.isEmpty())
            newRecord.setMaxQuantity(Integer.valueOf(addMaxQuantity));
        storageRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/storages", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(storageRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(storages, paramMap.getParamRef()));
        model.addAttribute("allEntryThisModel", storages.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private void setGoodsAllData() {
       storages = storageRep.findAll(Sort.by("id").ascending());
    }
}
