package com.Kaer.flagtoflower.color;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ColorService {

    @Value("${color.api.url}")
    private String apiUrl;

    @Value("${trefle.api.url}")
    private String trefleApiUrl;

    @Value("${trefle.api.token}")
    private String trefleApiToken;

    private final RestTemplate restTemplate;

    public ColorService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;

    }


    public List<String> createColorUrls(List<String> colors) {
        List<String> colorUrls = new ArrayList<>();

        for (String color : colors) {
            String colorUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .queryParam("rgb", color)
                    .build()
                    .toUriString();
            colorUrls.add(colorUrl);
            System.out.println("colorUrl: " + colorUrl);
            System.out.println("colorUrls: " + colorUrls);
        }
        return colorUrls;
    }

    public List<String> getColorNames(List<String> colorUrls) throws IOException {
        List<String> colorNames = new ArrayList<>();

        for (String oneColorUrl : colorUrls) {
            try {
                // Wywołujemy zewnętrzne API i pobieramy odpowiedź
                String response = restTemplate.getForObject(oneColorUrl, String.class);
                String colorName = extractColorNameFromJson(response);
                if (colorName != null) {
                    colorNames.add(colorName);
                    System.out.println("colorNames from getColorNames if: " + colorNames);
                }
            } catch (RestClientException e) {
                e.printStackTrace();
            }
        }
        System.out.println("colorNames from @Service getColorNames: " + colorNames);
        return colorNames;
    }
    private String extractColorNameFromJson(String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            // Pobieramy nazwę koloru z odpowiedzi JSON
            JsonNode nameNode = jsonNode.path("name").path("value");
            String colorName = nameNode.asText();
            System.out.println("colorName from extractColorNameFromJson: " + colorName);
            return colorName;
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(e);
        }
    }
    public List<String> buildFlowerUrl(List<String> colorNames) {
        ArrayList<String> flowerUrls = new ArrayList<>();
        System.out.println("colorNames from buildFlowerUrl: " + colorNames);

        for (String colorName : colorNames) {
            try {
                String formattedColor = colorName.replaceAll("\\s", "").toLowerCase();
                String flowerUrl = UriComponentsBuilder.fromHttpUrl(trefleApiUrl)
                        .queryParam("filter[flower_color]=" + formattedColor)
                        .queryParam("token", trefleApiToken)
                        .build()
                        .toUriString();
                flowerUrls.add(flowerUrl);
                System.out.println("Flower Urls form buildFlowerUrl: " + flowerUrls);
            } catch (RestClientException e) {
                e.printStackTrace();
            }
        }
        return flowerUrls;
    }

    public List<Flower> getFlowersByColorNames(List<String> colorNames) throws IOException {
        List<String> flowerUrls = buildFlowerUrl(colorNames);
        List<Flower> flowers = new ArrayList<>();

        for (String oneFlowerUrl : flowerUrls) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                InputStream urlStream = new URL(oneFlowerUrl).openStream();
                JsonNode jsonNode = objectMapper.readTree(urlStream);

                if (jsonNode != null && jsonNode.has("data")) {
                    JsonNode dataNode = jsonNode.get("data");

                    List<JsonNode> flowersWithImages = new ArrayList<>();

                    for (JsonNode flowerNode : dataNode) {
                        if (flowerNode.has("image_url") && !flowerNode.get("image_url").asText().isEmpty()) {
                            flowersWithImages.add(flowerNode);
                        }
                    }

                    int totalFlowersWithImages = flowersWithImages.size();

                    if (totalFlowersWithImages > 0) {
                        // Display up to 3 random flowers with images
                        Random random = new Random();
                        int limit = Math.min(3, totalFlowersWithImages);

                        for (int i = 0; i < limit; i++) {
                            int randomIndex = random.nextInt(totalFlowersWithImages);
                            JsonNode selectedFlowerNode = flowersWithImages.get(randomIndex);

                            Flower flower = Flower.builder()
                                    .commonName(selectedFlowerNode.get("common_name").asText())
                                    .imageUrl(selectedFlowerNode.get("image_url").asText())
                                    .build();
                            flowers.add(flower);

                            // Remove the selected flower to avoid duplicates
                            flowersWithImages.remove(randomIndex);
                            totalFlowersWithImages--;
                        }
                    }
                }
            } catch (IOException e) {
                // Obsługa błędów, np. logowanie, rzuć wyjątek, itp.
                e.printStackTrace();
            }
        }

        return flowers;
    }
}
