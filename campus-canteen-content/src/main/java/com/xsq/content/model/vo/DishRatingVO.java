package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishRatingVO implements Serializable {

    private Long dishId;
    private String dishName;
    private String coverImage;
    private BigDecimal price;

    private BigDecimal rating;
    private String comment;

    private BigDecimal avgRating;
    private Integer reviewCount;

    private List<DishTasteVO> tastes;
}
