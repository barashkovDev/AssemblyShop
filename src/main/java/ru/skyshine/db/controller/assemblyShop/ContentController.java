package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.tradingCompany.Content;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class ContentController extends BaseShopController{
    private ParamMap paramMap;
    private List<Content> contents;

    @GetMapping("/contents")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Наличие");
        model.addAttribute("mode", true);
        model.addAttribute("allEntryGoodsModel", goodsRep.findAll().stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));

        model.addAttribute("allEntryMarketModel", marketRep.findAll().stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));

        paramMap = new ParamMap(List.of("Код наличия", "Наименование магазина", "Наименование товара", "Количество товара", "Минимальное количество"),
                List.of("id", "idMarket.name", "idGoods.name", "quantityGoods", "minQuantity"));

        setGoodsAllData();
        displayTable(model);
        return "contents";
    }

    @PostMapping(value = "/contents", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(contents, paramMap.getParamRef()));
        return response;
    }

    @PostMapping(value = "/contents", params = "action=getInfoSelectContents")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {
        Content storage = contentRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("name", storage.getIdGoods());
        response.put("quantityGoods", storage.getQuantityGoods());
        response.put("min_quantity", storage.getMinQuantity());
        return response;
    }

    @PostMapping(value = "/contents", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/contents";
    }

    @PostMapping(value = "/contents", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeContents, String dropdownChangeMarket, String dropdownChangeGoods, String changeQuantityGoods, String changeMinQuantity) {
        Content existRecord = contentRep.getById(Integer.valueOf(dropdownChangeContents));
        if (!dropdownChangeMarket.isEmpty())
            existRecord.setIdMarket(marketRep.getById(Integer.valueOf(dropdownChangeMarket)));
        if (!dropdownChangeGoods.isEmpty())
            existRecord.setIdGoods(goodsRep.getById(Integer.valueOf(dropdownChangeGoods)));
        if (!changeQuantityGoods.isEmpty())
            existRecord.setQuantityGoods(Integer.valueOf(changeQuantityGoods));
        if (!changeMinQuantity.isEmpty())
            existRecord.setMinQuantity(Integer.valueOf(changeMinQuantity));
        contentRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/contents", params = "action=add")
    public String addRecord(@RequestParam String dropdownAddMarket, @RequestParam String dropdownAddGoods, String addQuantityGoods, String addMinQuantity) {
        Content newRecord = new Content();
        if (!dropdownAddMarket.isEmpty())
            newRecord.setIdMarket(marketRep.getById(Integer.valueOf(dropdownAddMarket)));
        if (!dropdownAddGoods.isEmpty())
            newRecord.setIdGoods(goodsRep.getById(Integer.valueOf(dropdownAddGoods)));
        if (!addQuantityGoods.isEmpty())
            newRecord.setQuantityGoods(Integer.valueOf(addQuantityGoods));
        if (!addMinQuantity.isEmpty())
            newRecord.setMinQuantity(Integer.valueOf(addMinQuantity));
        contentRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/contents", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(contentRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(contents, paramMap.getParamRef()));
        model.addAttribute("allEntryThisModel", contents.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private void setGoodsAllData() {
        contents = contentRep.findAll(Sort.by("id").ascending());
    }
}
