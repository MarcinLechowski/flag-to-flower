package com.Kaer.flagtoflower.controller;

import com.Kaer.flagtoflower.model.CountryCodeForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping("/")
public class FlagController {


    @PostMapping("/flag")
    public String getFlag(CountryCodeForm countryCodeForm, Model model) {
        String flagUrl = "https://flagcdn.com/w160/";

        // Sprawdź, czy countryCodeForm.getCountryCode() nie jest puste
        if (countryCodeForm.getCountryCode() != null && !countryCodeForm.getCountryCode().isEmpty()) {
            flagUrl += countryCodeForm.getCountryCode().toLowerCase() + ".png";
        } else {
            // Tutaj możesz ustawić domyślną wartość lub obsłużyć inaczej, gdy countryCode jest puste
            // Na przykład:
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

            // Redukcja odcieni kolorów - zmniejszenie liczby iteracji o połowę
            int step = 25; // Krok, który pozwoli na zaokrąglenie składowych kolorów
            for (int x = 0; x < width; x += width / 10) {  // Zmniejszenie liczby iteracji o połowę
                for (int y = 0; y < height; y += height / 10) {  // Zmniejszenie liczby iteracji o połowę
                    int rgb = image.getRGB(x, y);
                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // Zaokrąglanie składowych kolorów do najbliższej wartości kroku
                    red = Math.round(red / step) * step;
                    green = Math.round(green / step) * step;
                    blue = Math.round(blue / step) * step;

                    String colorString = "rgb(" + red + "," + green + "," + blue + ")";

                    // Dodaj tylko unikalne kolory do listy
                    if (!colors.contains(colorString)) {
                        colors.add(colorString);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return colors;
    }

}