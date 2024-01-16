package com.Kaer.flagtoflower.flower;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Service
public class FlowerService {

    @Value("${trefle.api.url}")
    private String apiUrl;

    @Value("${trefle.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate;

    public FlowerService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String getFlowersByColor(String color) {
        try {
            // Use UriComponentsBuilder to safely create the URL
            String url = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("filter[flower_color]", color)
                    .queryParam("token", apiToken)
                    .build().toString();

            // Send HTTP request and get the response as ResponseEntity<String>
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);

            // Check if the response is successful (HTTP Status 2xx)
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                return responseEntity.getBody();
            } else {
                // Handle unsuccessful response, e.g., throw an exception or return an appropriate message
                return "Error: " + responseEntity.getStatusCodeValue() + " - " + responseEntity.getBody();
            }
        } catch (Exception e) {
            // Handle errors related to the HTTP request
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}