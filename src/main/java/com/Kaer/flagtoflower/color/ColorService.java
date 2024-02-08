package com.Kaer.flagtoflower.color;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColorService {

    @Value("${color.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public ColorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }


    public List<String> createColorUrls(List<String> colors) {
        List<String> colorUrls = new ArrayList<>();

        for (String color : colors) {
            String colorUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("rgb", color)  // Poprawka: Usunięcie niepoprawnej konstrukcji zapytania
                    .build()
                    .toUriString();
            colorUrls.add(colorUrl);
            System.out.println("colorUrl: " + colorUrl);
        }
        return colorUrls;
    }

    public List<String> getColorNames(List<String> flowerColors) throws IOException {
        List<String> colorNames = new ArrayList<>();

        for (String oneRgbColor : flowerColors) {
            try {
                // Wywołujemy zewnętrzne API i pobieramy odpowiedź
                String response = restTemplate.getForObject(oneRgbColor, String.class);
                String colorName = extractColorNameFromJson(response);
                if (colorName != null) {
                    colorNames.add(colorName);
                }
            } catch (RestClientException e) {
                // Obsłuż błędy związane z komunikacją z API
                e.printStackTrace();
                // Możesz dodać logikę obsługi błędu, np. pominąć dany kolor lub zwrócić informację o błędzie
            }
        }

        System.out.println("colorNames: " + colorNames);
        return colorNames;
    }


    private String extractColorNameFromJson(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            // Pobieramy nazwę koloru z odpowiedzi JSON
            JsonNode nameNode = jsonNode.path("name").path("value");
            String colorName = nameNode.asText();

            return colorName;
        } catch (Exception e) {
            e.printStackTrace(); // Obsłuż błędy parsowania JSON
            return null;
        }
    }
}