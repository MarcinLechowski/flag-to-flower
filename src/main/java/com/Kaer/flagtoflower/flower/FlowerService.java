package com.Kaer.flagtoflower.flower;


import com.Kaer.flagtoflower.color.ColorService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class FlowerService {

    @Value("${trefle.api.url}")
    private String trefleApiUrl;

    @Value("${trefle.api.token}")
    private String trefleApiToken;

    private final ColorService colorService;
    private final RestTemplate restTemplate;

    public FlowerService(ColorService colorService, RestTemplate restTemplate) {
        this.colorService = colorService;
        this.restTemplate = restTemplate;
    }

    public List<String> buildFlowerUrl(List<String> colorNames) {
        ArrayList<String> flowerUrls = new ArrayList<>();


            for (String colorName : colorNames) {

                String flowerUrl = UriComponentsBuilder.fromHttpUrl(trefleApiUrl)
                        .queryParam(colorName)
                        .queryParam(trefleApiToken)
                        .build()
                        .toUriString();
                flowerUrls.add(flowerUrl);
                System.out.println("Flower Urls: " + flowerUrls);
            }

            return flowerUrls;
        }


    public List<Flower> parseFlowerResponse(String responseBody) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        List<Flower> flowers = new ArrayList<>();

        if (jsonNode != null && jsonNode.has("data")) {
            JsonNode dataNode = jsonNode.get("data");

            List<JsonNode> flowersWithImages = new ArrayList<>();

            // Filter flowers with images
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

        return flowers;
    }

    public String sendGetRequest(String flowerUrl) {
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(flowerUrl, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                // Handle non-successful response, e.g., log or throw an exception
                System.err.println("Failed to fetch data from Trefle API. Status code: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            // Handle exceptions, e.g., log or throw a custom exception
            e.printStackTrace();
        }

        return null; // Return null in case of errors
    }
}