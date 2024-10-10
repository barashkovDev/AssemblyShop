package ru.skyshine.db.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MainController {

    @GetMapping()
    public String menu(Model model) {
        model.addAttribute("title", "Доступные работы");
        model.addAttribute("mode",true);
        return "home";
    }

    @PostMapping()
    public String redirect(@RequestParam String chosenId, Model model) {
        switch (chosenId){
            case "1" -> {
                return "redirect:/labs";
            }
            case "2" -> {
                return "redirect:/sportSchool";
            }
            case "3" -> {
                return "redirect:/assemblyShop";
            }
        }
        return "home";
    }
}
