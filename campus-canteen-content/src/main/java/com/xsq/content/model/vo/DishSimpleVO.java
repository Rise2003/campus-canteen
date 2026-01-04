package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 菜品搜索/列表用的精简 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DishSimpleVO implements Serializable {

    private Long id;

    private Long canteenId;

    private String name;

    private String coverImage;

    private BigDecimal price;

    private BigDecimal totalRating;
}

