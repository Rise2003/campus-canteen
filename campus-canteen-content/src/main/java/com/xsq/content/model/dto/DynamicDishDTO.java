package com.xsq.content.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "动态菜品评价DTO")
public class DynamicDishDTO {

    @Schema(description = "菜品ID", required = true)
    private Long dishId;

    @Schema(description = "评分")
    private BigDecimal rating;

    @Schema(description = "评价内容")
    private String comment;

    @Schema(description = "口味标签")
    private List<DishTasteDTO> tastes;
}

@Data
@Schema(description = "菜品口味DTO")
class DishTasteDTO {

    @Schema(description = "口味ID", required = true)
    private Integer tasteId;

    @Schema(description = "强度: 1-轻微, 2-适中, 3-浓郁")
    private Integer intensity = 2;
}