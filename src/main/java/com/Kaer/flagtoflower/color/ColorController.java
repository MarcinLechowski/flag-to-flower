package com.Kaer.flagtoflower.color;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

@Controller
public class ColorController {

    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @GetMapping("/flag/getColorNames")
    public String getColorNames(@RequestParam("colors") List<String> colors, Model model) throws IOException {
        List<String> colorUrls = colorService.createColorUrls(colors);
        List<String> colorNames = colorService.getColorNames(colorUrls);
        model.addAttribute("colorNames", colorNames);


        return "flag-form :: colorNamesFragment";
    }
}