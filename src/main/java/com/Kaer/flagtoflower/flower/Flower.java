package com.Kaer.flagtoflower.flower;

import lombok.Builder;
import lombok.Data;//


@Data
@Builder
public class Flower {
    private Long id;
    private String commonName;
    private String imageUrl;
    private String colorName;
}
