package com.Kaer.flagtoflower.color;

import lombok.Builder;
import lombok.Data;

@Data
@Builder//Dzięki temu, można łatwo tworzyć obiekty z różnymi zestawami pól, a kod staje się bardziej czytelny.
public class Flower {
    private Long id;
    private String commonName;
    private String imageUrl;
    private String colorName;
}