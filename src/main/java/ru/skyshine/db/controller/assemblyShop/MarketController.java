package ru.skyshine.db.controller.assemblyShop;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.tradingCompany.Market;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/assemblyShop")
public class MarketController extends BaseShopController {
    private ParamMap paramMap;
    private List<Market> markets;

    @GetMapping("/markets")
    public String allStartPage(Model model) {
        model.addAttribute("title", "Магазины");
        model.addAttribute("mode", true);

        paramMap = new ParamMap(List.of("Id", "Наименование", "Владелец", "Адрес", "Почта", "Телефон"),
                List.of("id", "name", "owner", "adress", "email", "phoneNumber"));

        setMarketsAllData();
        displayTable(model);
        return "markets";
    }

    @PostMapping(value = "/markets", params = "action=filter")
    @ResponseBody
    public Map<String, Object> filterAndDisplayTable(@RequestParam String selectTypeFilter, @RequestParam String selectColumnFilter, @RequestParam String valueFilter) {
        Map<String, Object> response = new HashMap<>();
        response.put("dataTable", Converter.objToString(markets, paramMap.getParamRef()));
        return response;
    }

    @PostMapping(value = "/markets", params = "action=getInfoSelectMarket")
    @ResponseBody
    public Map<String, Object> getInfoSelectCW(@RequestParam String id) {
        Market market = marketRep.getById(Integer.valueOf(id));
        Map<String, Object> response = new HashMap<>();
        response.put("name", market.getName());
        response.put("owner", market.getOwner());
        response.put("adress", market.getAdress());
        response.put("email", market.getEmail());
        response.put("phone_number", market.getPhoneNumber());
        return response;
    }

    @PostMapping(value = "/markets", params = "action=reset")
    public String reset() {
        return "redirect:/assemblyShop/markets";
    }

    @PostMapping(value = "/markets", params = "action=change")
    public String changeRecord(@RequestParam String dropdownChangeMarket, String changeName, String changeOwner, String changeAdress,
                               String changeEmail, String changePhoneNumber) {
        Market existRecord = marketRep.getById(Integer.valueOf(dropdownChangeMarket));
        if (!changeName.isEmpty())
            existRecord.setName(changeName);
        if (!changeOwner.isEmpty())
            existRecord.setOwner(changeOwner);
        if (!changeAdress.isEmpty())
            existRecord.setAdress(changeAdress);
        if (!changeEmail.isEmpty())
            existRecord.setEmail(changeEmail);
        if (!changePhoneNumber.isEmpty())
            existRecord.setPhoneNumber(changePhoneNumber);
        marketRep.save(existRecord);
        return reset();
    }

    @PostMapping(value = "/markets", params = "action=add")
    public String addRecord(@RequestParam String addName, String addOwner, String addAdress,
                            String addEmail, String addPhoneNumber) {
        Market newRecord = new Market();
        if (!addName.isEmpty())
            newRecord.setName(addName);
        if (!addOwner.isEmpty())
            newRecord.setOwner(addOwner);
        if (!addAdress.isEmpty())
            newRecord.setAdress(addAdress);
        if (!addEmail.isEmpty())
            newRecord.setEmail(addEmail);
        if (!addPhoneNumber.isEmpty())
            newRecord.setPhoneNumber(addPhoneNumber);
        marketRep.save(newRecord);
        return reset();
    }

    @PostMapping(value = "/markets", params = "action=delete")
    @ResponseBody
    public Map<String, Object> deleteRecord(@RequestParam String delIdEntry) {
        return deleteRecord(marketRep, delIdEntry);
    }

    private void displayTable(Model model) {
        model.addAttribute("nameColumnsTable", paramMap.getParamName());
        model.addAttribute("dataTable", Converter.objToString(markets, paramMap.getParamRef()));
        model.addAttribute("allEntryThisModel", markets.stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component.getId());
                    map.put("text", component.getId());
                    return map;
                })
                .collect(Collectors.toList()));
    }

    private void setMarketsAllData() {
        markets = marketRep.findAll(Sort.by("id").ascending());
    }
}
