package com.xsq.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品表
 * @TableName dish
 */
@TableName(value ="dish")
@Data
public class Dish {
    /**
     * 菜品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 档口ID
     */
    @TableField(value = "stall_id")
    private Long stallId;

    /**
     * 食堂ID
     */
    @TableField(value = "canteen_id")
    private Long canteenId;

    /**
     * 菜品名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 菜品描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 菜品图片
     */
    @TableField(value = "cover_image")
    private String coverImage;

    /**
     * 价格
     */
    @TableField(value = "price")
    private BigDecimal price;

    /**
     * 原价
     */
    @TableField(value = "original_price")
    private BigDecimal originalPrice;

    /**
     * 单位
     */
    @TableField(value = "unit")
    private String unit;

    /**
     * 卡路里
     */
    @TableField(value = "calories")
    private Integer calories;

    /**
     * 辣度 0:不辣 1:微辣 2:中辣 3:重辣
     */
    @TableField(value = "spicy_level")
    private Integer spicyLevel;

    /**
     * 标签(JSON数组)
     */
    @TableField(value = "tags")
    private String tags;

    /**
     * 综合评分
     */
    @TableField(value = "total_rating")
    private BigDecimal totalRating;

    /**
     * 口味评分
     */
    @TableField(value = "taste_rating")
    private BigDecimal tasteRating;

    /**
     * 卖相评分
     */
    @TableField(value = "appearance_rating")
    private BigDecimal appearanceRating;

    /**
     * 分量评分
     */
    @TableField(value = "portion_rating")
    private BigDecimal portionRating;

    /**
     * 点评数
     */
    @TableField(value = "review_count")
    private Integer reviewCount;

    /**
     * 收藏数
     */
    @TableField(value = "like_count")
    private Integer likeCount;

    /**
     * 点单次数
     */
    @TableField(value = "order_count")
    private Integer orderCount;

    /**
     * 是否推荐 1:是 0:否
     */
    @TableField(value = "is_recommended")
    private Integer isRecommended;

    /**
     * 状态 1:上架 0:下架
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 排序
     */
    @TableField(value = "sort_order")
    private Integer sortOrder;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Dish other = (Dish) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getStallId() == null ? other.getStallId() == null : this.getStallId().equals(other.getStallId()))
            && (this.getCanteenId() == null ? other.getCanteenId() == null : this.getCanteenId().equals(other.getCanteenId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getCoverImage() == null ? other.getCoverImage() == null : this.getCoverImage().equals(other.getCoverImage()))
            && (this.getPrice() == null ? other.getPrice() == null : this.getPrice().equals(other.getPrice()))
            && (this.getOriginalPrice() == null ? other.getOriginalPrice() == null : this.getOriginalPrice().equals(other.getOriginalPrice()))
            && (this.getUnit() == null ? other.getUnit() == null : this.getUnit().equals(other.getUnit()))
            && (this.getCalories() == null ? other.getCalories() == null : this.getCalories().equals(other.getCalories()))
            && (this.getSpicyLevel() == null ? other.getSpicyLevel() == null : this.getSpicyLevel().equals(other.getSpicyLevel()))
            && (this.getTags() == null ? other.getTags() == null : this.getTags().equals(other.getTags()))
            && (this.getTotalRating() == null ? other.getTotalRating() == null : this.getTotalRating().equals(other.getTotalRating()))
            && (this.getTasteRating() == null ? other.getTasteRating() == null : this.getTasteRating().equals(other.getTasteRating()))
            && (this.getAppearanceRating() == null ? other.getAppearanceRating() == null : this.getAppearanceRating().equals(other.getAppearanceRating()))
            && (this.getPortionRating() == null ? other.getPortionRating() == null : this.getPortionRating().equals(other.getPortionRating()))
            && (this.getReviewCount() == null ? other.getReviewCount() == null : this.getReviewCount().equals(other.getReviewCount()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getOrderCount() == null ? other.getOrderCount() == null : this.getOrderCount().equals(other.getOrderCount()))
            && (this.getIsRecommended() == null ? other.getIsRecommended() == null : this.getIsRecommended().equals(other.getIsRecommended()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getSortOrder() == null ? other.getSortOrder() == null : this.getSortOrder().equals(other.getSortOrder()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getStallId() == null) ? 0 : getStallId().hashCode());
        result = prime * result + ((getCanteenId() == null) ? 0 : getCanteenId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getCoverImage() == null) ? 0 : getCoverImage().hashCode());
        result = prime * result + ((getPrice() == null) ? 0 : getPrice().hashCode());
        result = prime * result + ((getOriginalPrice() == null) ? 0 : getOriginalPrice().hashCode());
        result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
        result = prime * result + ((getCalories() == null) ? 0 : getCalories().hashCode());
        result = prime * result + ((getSpicyLevel() == null) ? 0 : getSpicyLevel().hashCode());
        result = prime * result + ((getTags() == null) ? 0 : getTags().hashCode());
        result = prime * result + ((getTotalRating() == null) ? 0 : getTotalRating().hashCode());
        result = prime * result + ((getTasteRating() == null) ? 0 : getTasteRating().hashCode());
        result = prime * result + ((getAppearanceRating() == null) ? 0 : getAppearanceRating().hashCode());
        result = prime * result + ((getPortionRating() == null) ? 0 : getPortionRating().hashCode());
        result = prime * result + ((getReviewCount() == null) ? 0 : getReviewCount().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getOrderCount() == null) ? 0 : getOrderCount().hashCode());
        result = prime * result + ((getIsRecommended() == null) ? 0 : getIsRecommended().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getSortOrder() == null) ? 0 : getSortOrder().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", stallId=").append(stallId);
        sb.append(", canteenId=").append(canteenId);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", coverImage=").append(coverImage);
        sb.append(", price=").append(price);
        sb.append(", originalPrice=").append(originalPrice);
        sb.append(", unit=").append(unit);
        sb.append(", calories=").append(calories);
        sb.append(", spicyLevel=").append(spicyLevel);
        sb.append(", tags=").append(tags);
        sb.append(", totalRating=").append(totalRating);
        sb.append(", tasteRating=").append(tasteRating);
        sb.append(", appearanceRating=").append(appearanceRating);
        sb.append(", portionRating=").append(portionRating);
        sb.append(", reviewCount=").append(reviewCount);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", orderCount=").append(orderCount);
        sb.append(", isRecommended=").append(isRecommended);
        sb.append(", status=").append(status);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}