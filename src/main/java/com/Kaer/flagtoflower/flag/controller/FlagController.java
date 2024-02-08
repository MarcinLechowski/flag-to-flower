package com.Kaer.flagtoflower.flag.controller;

import com.Kaer.flagtoflower.color.ColorService;
import com.Kaer.flagtoflower.flag.model.CountryCodeForm;
import com.Kaer.flagtoflower.flower.FlowerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class FlagController {

    private  final int NUM_REGIONS = 20;
    private final FlowerService flowerService;
    private final ColorService colorService;
    private final RestTemplate restTemplate;

    public FlagController(FlowerService flowerService, ColorService colorService, RestTemplate restTemplate) {
        this.flowerService = flowerService;
        this.colorService = colorService;
        this.restTemplate = restTemplate;
    }


    @GetMapping("/flag")
    public String getFlag(CountryCodeForm countryCodeForm, Model model) {
        String flagUrl = "https://flagcdn.com/w160/";

        if (countryCodeForm.getCountryCode() != null && !countryCodeForm.getCountryCode().isEmpty()) {
            flagUrl += countryCodeForm.getCountryCode().toLowerCase() + ".png";
        }

        model.addAttribute("flagImageUrl", flagUrl);

        List<String> flagColors = getUniqueColorInfo(flagUrl, model);
        model.addAttribute("flagColors", flagColors);

        // Przekazuj kolory RGB do ColorService
        List<String> colorUrls = colorService.createColorUrls(flagColors);
        model.addAttribute("colorUrls", colorUrls);

        return "flag-form";
    }

    private List<String> getUniqueColorInfo(String flagUrl, Model model) {
        List<String> colors = new ArrayList<>();

        try {
            URL url = new URL(flagUrl);
            BufferedImage image = ImageIO.read(url);

            int width = image.getWidth();
            int height = image.getHeight();

            int regionWidth = width / NUM_REGIONS;
            int regionHeight = height / NUM_REGIONS;

            for (int i = 0; i < NUM_REGIONS; i++) {
                for (int j = 0; j < NUM_REGIONS; j++) {
                    int startX = i * regionWidth;
                    int startY = j * regionHeight;

                    BufferedImage region = image.getSubimage(startX, startY, regionWidth, regionHeight);
                    String dominantColor = getDominantColor(region);
                    colors.add(dominantColor);
                }
            }

            colors = colors
                    .stream()
                    .distinct()
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return colors;
    }

    private String getDominantColor(BufferedImage image) {
        Map<String, Integer> colorCounts = new HashMap<>();

        int regionWidth = image.getWidth();
        int regionHeight = image.getHeight();

        for (int x = 0; x < regionWidth; x++) {
            for (int y = 0; y < regionHeight; y++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                String colorString = red + "," + green + "," + blue;
                colorCounts.put(colorString, colorCounts.getOrDefault(colorString, 0) + 1);
            }
        }

        Optional<Map.Entry<String, Integer>> dominantColorEntry = colorCounts
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        return dominantColorEntry
                .map(Map.Entry::getKey)
                .orElse("rgb(0,0,0)");
    }
}