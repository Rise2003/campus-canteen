package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicVO implements Serializable {

    private Long id;

    private String title;
    private String content;

    private List<String> images;

    private UserSimpleVO user;
    private CanteenSimpleVO canteen;

    private RatingVO ratings;
    private StatsVO stats;

    private String locationName;
    private BigDecimal latitude;
    private BigDecimal longitude;

    private LocalDateTime createdAt;

    private String timeAgo;
    private Integer readTime;

    private List<DishRatingVO> dishRatings;

    private Boolean editAllowed;
    private Boolean deleteAllowed;
    private String shareUrl;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RatingVO implements Serializable {
        private BigDecimal total;
        private BigDecimal taste;
        private BigDecimal environment;
        private BigDecimal service;
        private BigDecimal price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatsVO implements Serializable {
        private Integer likeCount;
        private Integer commentCount;
        private Integer shareCount;
        private Integer viewCount;
        private Integer collectCount;

        private Boolean isLiked;
        private Boolean isCollected;
    }
}
