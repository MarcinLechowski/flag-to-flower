package com.Kaer.flagtoflower.flag.controller;


import com.Kaer.flagtoflower.flag.model.CountryCodeForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class FlagController {

    private static final int NUM_REGIONS = 20;


    @GetMapping("/flag")
    public String getFlag(CountryCodeForm countryCodeForm, Model model) {
        String flagUrl = "https://flagcdn.com/w160/";

        // Sprawdź, czy countryCodeForm.getCountryCode() nie jest puste
        if (countryCodeForm.getCountryCode() != null && !countryCodeForm.getCountryCode().isEmpty()) {
            flagUrl += countryCodeForm.getCountryCode().toLowerCase() + ".png";
        } else {
            // Tutaj możesz ustawić domyślną wartość lub obsłużyć inaczej, gdy countryCode jest puste
            // Na przykład:https://lh6.googleusercontent.com/JwJwuf9__rW-wQE7YpmEPo9Yj9e3q69igGNN7ChFsjLuFJeEwIwTFvwCUetotyRprHIDufK2ashemq3wLumO-ClS25Mr439yrydahfSVKgQh76RYsq-zESXDLIb_PESOrQ=w740
            // flagUrl = "https://flagcdn.com/default.png";
            // lub
            // return "error-page"; // i stworzyć widok dla strony błędu
        }

        model.addAttribute("flagImageUrl", flagUrl);

        // Dodaj logikę do pobrania unikalnych kolorów flagi i przekazania ich do widoku
        List<String> flagColors = getUniqueColorInfo(flagUrl);
        model.addAttribute("flagColors", flagColors);

        return "flag-form";
    }

    private List<String> getUniqueColorInfo(String flagUrl) {
        List<String> colors = new ArrayList<>();

        try {
            URL url = new URL(flagUrl);
            BufferedImage image = ImageIO.read(url);

            int width = image.getWidth();
            int height = image.getHeight();

            int regionWidth = width / NUM_REGIONS;
            int regionHeight = height / NUM_REGIONS;

            // Iteruj przez regiony
            for (int i = 0; i < NUM_REGIONS; i++) {
                for (int j = 0; j < NUM_REGIONS; j++) {
                    int startX = i * regionWidth;
                    int startY = j * regionHeight;

                    // Obszar regionu
                    BufferedImage region = image.getSubimage(startX, startY, regionWidth, regionHeight);

                    // Znajdź dominujący kolor w regionie
                    String dominantColor = getDominantColor(region);

                    // Dodaj dominujący kolor do listy
                    colors.add(dominantColor);
                }
            }

            // Usuń duplikaty
            colors = colors.stream().distinct().collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return colors;
    }

    private String getDominantColor(BufferedImage image) {
        // Utwórz mapę kolorów i zlicz wystąpienia każdego koloru w regionie
        Map<String, Integer> colorCounts = new HashMap<>();

        int regionWidth = image.getWidth();
        int regionHeight = image.getHeight();

        // Iteruj przez piksele w regionie
        for (int x = 0; x < regionWidth; x++) {
            for (int y = 0; y < regionHeight; y++) {
                int rgb = image.getRGB(x, y);
                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                String colorString = "rgb(" + red + "," + green + "," + blue + ")";

                // Zwiększ liczbę wystąpień koloru w mapie
                colorCounts.put(colorString, colorCounts.getOrDefault(colorString, 0) + 1);
            }
        }

        // Znajdź dominujący kolor w regionie
        Optional<Map.Entry<String, Integer>> dominantColorEntry = colorCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        // Zwróć dominujący kolor lub czarny, jeśli nie znaleziono
        return dominantColorEntry.map(Map.Entry::getKey).orElse("rgb(0,0,0)");
    }
}

