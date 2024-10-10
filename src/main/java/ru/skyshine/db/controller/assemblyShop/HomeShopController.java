package ru.skyshine.db.controller.assemblyShop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/assemblyShop")
public class HomeShopController {

    @GetMapping()
    public String menu(Model model) {
        model.addAttribute("title", "Сборочный цех");
        model.addAttribute("mode", true);
        return "start_shop";
    }
}
