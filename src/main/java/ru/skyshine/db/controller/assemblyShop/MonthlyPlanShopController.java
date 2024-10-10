package ru.skyshine.db.controller.assemblyShop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.skyshine.db.model.assemplyShop.MonthlyPlan;
import ru.skyshine.db.tools.converter.Converter;
import ru.skyshine.db.tools.storageClasses.ParamMap;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/assemblyShop")
public class MonthlyPlanShopController extends BaseShopController {
    ParamMap paramMapProductsPlan;
    Integer month;

    @GetMapping("/monthlyPlan")
    public String startPage(Model model) {
        model.addAttribute("title", "Месячные планы");
        model.addAttribute("mode", true);

        paramMapProductsPlan = new ParamMap(List.of("Код изделия", "Наименование изделия", "Текущее количество", "Необходимое количество"),
                List.of("codeProduct.code", "codeProduct.name", "availability", "need"));

        displayTable(model);
        return "monthly_plan";
    }

    @PostMapping(value = "/monthlyPlan", params = "action=infoForAddMonthlyPlan")
    @ResponseBody
    public Map<String, Object> infoForAddMonthlyPlan() {
        Map<String, Object> response = new HashMap<>();
        response.put("freeMonths", IntStream.rangeClosed(1, 12)
                .boxed()
                .filter(number -> !monthlyPlanRep.getMonthsSincePlans().contains(number))
                .map(number -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", number);
                    map.put("text", Month.of(Integer.parseInt(number.toString())).getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru")));
                    return map;
                })
                .collect(Collectors.toList()));
        response.put("products", productRep.findAll().stream()
                .map(detail -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", detail.getCode());
                    map.put("text", detail.getName());
                    return map;
                })
                .collect(Collectors.toList()));
        return response;
    }

    @PostMapping(value = "/monthlyPlan", params = "action=addMonthlyPlan")
    @ResponseBody
    public Map<String, Object> addMonthlyPlan(@RequestParam String month, @RequestParam String product,
                                              @RequestParam String availabilityAmount, @RequestParam String needAmount) {
        MonthlyPlan newRecord = new MonthlyPlan();
        newRecord.setMonth(Integer.parseInt(month));
        newRecord.setCodeProduct(productRep.getById(Integer.valueOf(product)));
        if (!availabilityAmount.isEmpty()) {
            newRecord.setAvailability(Integer.parseInt(availabilityAmount));
        }
        newRecord.setNeed(Integer.parseInt(needAmount));
        monthlyPlanRep.save(newRecord);
        return getAllMonthlyPlans();
    }

    @PostMapping(value = "/monthlyPlan", params = "action=infoForDelMonthlyPlan")
    @ResponseBody
    public Map<String, Object> infoForDelMonthlyPlan() {
        return getAllMonthlyPlans();
    }

    @PostMapping(value = "/monthlyPlan", params = "action=delMonthlyPlan")
    @ResponseBody
    public Map<String, Object> delMonthlyPlan(@RequestParam String month) {
        monthlyPlanRep.deletePlan(Integer.parseInt(month));
        return getAllMonthlyPlans();
    }

    private void displayTable(Model model) {
        model.addAttribute("allPlans", monthlyPlanRep.getMonthsSincePlans().stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component);
                    map.put("text", Month.of(Integer.parseInt(component.toString())).getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru")));
                    return map;
                })
                .collect(Collectors.toList()));
        model.addAttribute("nameColumnsProductsMPTable", paramMapProductsPlan.getParamName());
    }

    @PostMapping(value = "/monthlyPlan", params = "action=allMonthlyPlans")
    @ResponseBody
    private Map<String, Object> getAllMonthlyPlans() {
        Map<String, Object> response = new HashMap<>();
        response.put("allPlans", monthlyPlanRep.getMonthsSincePlans().stream()
                .map(component -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("value", component);
                    map.put("text", Month.of(Integer.parseInt(component.toString())).getDisplayName(TextStyle.FULL_STANDALONE, Locale.forLanguageTag("ru")));
                    return map;
                })
                .collect(Collectors.toList()));
        return response;
    }

    @PostMapping(value = "/monthlyPlan", params = "action=infoAboutMonthlyPlan")
    @ResponseBody
    private Map<String, Object> infoAboutMonthlyPlan(@RequestParam String month) {
        this.month = Integer.parseInt(month);
        Map<String, Object> response = new HashMap<>();
        response.put("productsForPlan", Converter.objToString(
                monthlyPlanRep.getProductsInPlan(this.month), paramMapProductsPlan.getParamRef()));
        response.put("newProductsForPlan", monthlyPlanRep.getNotProductsInPlan(this.month));
        return response;
    }

    @PostMapping(value = "/monthlyPlan", params = "action=infoAboutProductMonthlyPlan")
    @ResponseBody
    private Map<String, Object> getAllMonthlyPlans(@RequestParam String productId) {
        Map<String, Object> response = new HashMap<>();
        MonthlyPlan monthlyPlan = monthlyPlanRep.getProductInPlan(month, Integer.parseInt(productId));
        response.put("availabilityAmount", monthlyPlan.getAvailability());
        response.put("needAmount", monthlyPlan.getNeed());
        return response;
    }

    @PostMapping(value = "/monthlyPlan", params = "action=changeProductMP")
    @ResponseBody
    private Map<String, Object> changeProductForPlan(@RequestParam String productId, @RequestParam String availability, @RequestParam String need) {
        MonthlyPlan existsRecord = monthlyPlanRep.getProductInPlan(month, Integer.parseInt(productId));
        existsRecord.setAvailability(existsRecord.getAvailability() != Integer.parseInt(availability) ? Integer.parseInt(availability) : existsRecord.getAvailability());
        existsRecord.setNeed(existsRecord.getNeed() != Integer.parseInt(need) ? Integer.parseInt(need) : existsRecord.getNeed());
        monthlyPlanRep.save(existsRecord);
        return infoAboutMonthlyPlan(month.toString());
    }

    @PostMapping(value = "/monthlyPlan", params = "action=addProductMP")
    @ResponseBody
    private Map<String, Object> addProductForPlan(@RequestParam String productId, @RequestParam String availability, @RequestParam String need) {
        MonthlyPlan newRecord = new MonthlyPlan();
        newRecord.setMonth(this.month);
        newRecord.setCodeProduct(productRep.getById(Integer.valueOf(productId)));
        newRecord.setAvailability(Integer.parseInt(availability));
        newRecord.setNeed(Integer.parseInt(need));
        monthlyPlanRep.save(newRecord);
        return infoAboutMonthlyPlan(month.toString());
    }

    @PostMapping(value = "/monthlyPlan", params = "action=delProductMP")
    @ResponseBody
    private Map<String, Object> delProductForPlan(@RequestParam String productId) {
        monthlyPlanRep.deleteById(monthlyPlanRep.getProductInPlan(this.month, Integer.parseInt(productId)).getCode());
        return infoAboutMonthlyPlan(month.toString());
    }
}
