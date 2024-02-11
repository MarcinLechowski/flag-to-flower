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

    @GetMapping("/flag/getColorNamesAndFlowers")
    public String getColorNamesAndFlowers(@RequestParam("colors") List<String> colors, Model model) throws IOException {
        List<String> colorUrls = colorService.createColorUrls(colors);
        List<String> colorNames = colorService.getColorNames(colorUrls);
        List<String> flowerUrls = colorService.buildFlowerUrl(colorNames);

        System.out.println("colorNames from buildFlowerUrl: " + colorNames);
        System.out.println("flowerUrls from ColorController /getFlowers" + flowerUrls);

        model.addAttribute("colorNames", colorNames);
        model.addAttribute("flowerUrls", flowerUrls);

        return "flag-form :: colorNamesAndFlowersFragment";
    }

    @GetMapping("/getFlowers")
    public String getFlowers(@RequestParam("colorNames") List<String> colorNames, Model model) {
        List<String> flowerUrls = colorService.buildFlowerUrl(colorNames);

        System.out.println("flowerUrls from ColorController /getFlowers" + flowerUrls);
        try {
            List<Flower> flowers = colorService.getFlowersByColorNames(colorNames);

            model.addAttribute("flowers", flowers);
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
        return "flag-form :: colorNamesAndFlowersFragment";
    }
}