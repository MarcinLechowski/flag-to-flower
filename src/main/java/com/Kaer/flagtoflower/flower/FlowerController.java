package com.Kaer.flagtoflower.flower;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Controller
public class FlowerController {

    private final FlowerService flowerService;

    public FlowerController(FlowerService flowerService) {
        this.flowerService = flowerService;
    }

    @GetMapping("/")
    public String showForm() {
        return "flowers";
    }

    @GetMapping("/getFlowers")
    public String getFlowers(@RequestParam String color, Model model) {
        try {
            String response = flowerService.getFlowersByColor(color);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

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

                        Flower flower = new Flower();
                        flower.setCommonName(selectedFlowerNode.get("common_name").asText());
                        flower.setImageUrl(selectedFlowerNode.get("image_url").asText());
                        flowers.add(flower);

                        // Remove the selected flower to avoid duplicates
                        flowersWithImages.remove(randomIndex);
                        totalFlowersWithImages--;
                    }
                }
            }

            model.addAttribute("flowers", flowers);

        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }

        return "result";
    }
}