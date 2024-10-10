package ru.skyshine.db.controller.sportSchool;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/sportSchool")
public class HomeSportController {

    @GetMapping()
    public String menu(Model model) {
        model.addAttribute("title", "Жопа");
        model.addAttribute("mode",true);
        return "start_school";
    }
}
