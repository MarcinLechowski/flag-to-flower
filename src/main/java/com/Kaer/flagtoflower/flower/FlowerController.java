package com.Kaer.flagtoflower.flower;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FlowerController {

    private final FlowerService flowerService;

    @Autowired
    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping("/flowers-form")
    public String getFlowers(Model model, @RequestParam("colors") List<String> colors) {
        System.out.println(colors);
        try {
            System.out.println(colors);
            // Build Trefle API URLs based on colors
            List<String> flowerUrls = flowerService.buildFlowerUrl(colors);

            List<Flower> allFlowers = new ArrayList<>();
            // Send GET requests to the FlowerService and parse the responses
            for (String flowerUrl : flowerUrls) {
                String responseBody = flowerService.sendGetRequest(flowerUrl);
                List<Flower> flowers = flowerService.parseFlowerResponse(responseBody);

                // Add the flowers to the model
                allFlowers.addAll(flowers);  // Use addAll to accumulate all flowers
            }

            // Add the final list of flowers to the model
            model.addAttribute("flowers", allFlowers);

        } catch (IOException e) {
            // Handle exception appropriately (e.g., log or show an error message)
            e.printStackTrace();
        }

        return "flowers-form";
    }
}