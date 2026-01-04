package com.xsq.content.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicQueryDTO {

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer size = 10;

    private String sort;

    private String order;

    private Long dishId;

    private Long canteenId;

    private Long userId;

    private String keyword;

    /**
     * 兼容字段：仅推荐内容（旧参数名）
     */
    private Boolean recommendedOnly;

    /**
     * api.txt 契约字段：recommended
     */
    private Boolean recommended;

    private Boolean followingOnly;

    private Long currentUserId;

    private String timeRange;

    private Double latitude;

    private Double longitude;

    @Builder.Default
    private Integer radius = 1000;

    private BigDecimal minRating;
}
