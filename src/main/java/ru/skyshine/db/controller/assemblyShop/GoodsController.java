package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.tradingCompany.Goods;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class GoodsController extends BaseShopController{
    private ParamMap paramMap;
    private List<Goods> goods;

    @GetMapping("/goods")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Товары");
        model.addAttribute("mode", true);

        paramMap = new ParamMap(List.of("Id", "Наименование", "Стоимость", "Описание"),
                List.of("id", "name", "cost", "description"));

        setGoodsAllData();
        displayTable(model);
        return "goods";
    }

    @PostMapping(value = "/goods", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(goods, paramMap.getParamRef()));
        return response;
    }

    @PostMapping(value = "/goods", params = "action=getInfoSelectGoods")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {
        Goods good = goodsRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("name", good.getName());
        response.put("cost", good.getCost());
        response.put("description", good.getDescription());
        return response;
    }

    @PostMapping(value = "/goods", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/goods";
    }

    @PostMapping(value = "/goods", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeGoods, String changeName, String changeCost, String changeDescription) {
        Goods existRecord = goodsRep.getById(Integer.valueOf(dropdownChangeGoods));
        if (!changeName.isEmpty())
            existRecord.setName(changeName);
        if (!changeCost.isEmpty())
            existRecord.setCost(Double.valueOf(changeCost));
        if (!changeDescription.isEmpty())
            existRecord.setDescription(changeDescription);
        goodsRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/goods", params = "action=add")
    public String addRecord(@RequestParam String addName, String addCost, String addDescription) {
        Goods newRecord = new Goods();
        if (!addName.isEmpty())
            newRecord.setName(addName);
        if (!addCost.isEmpty())
            newRecord.setCost(Double.valueOf(addCost));
        if (!addDescription.isEmpty())
            newRecord.setDescription(addDescription);
        goodsRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/goods", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(goodsRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(goods, paramMap.getParamRef()));
        model.addAttribute("allEntryThisModel", goods.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private void setGoodsAllData() {
        goods = goodsRep.findAll(Sort.by("id").ascending());
    }
}
