package com.xsq.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 档口表
 * @TableName stall
 */
@TableName(value ="stall")
@Data
public class Stall {
    /**
     * 档口ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 食堂ID
     */
    @TableField(value = "canteen_id")
    private Long canteenId;

    /**
     * 档口名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 档口描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 档口Logo
     */
    @TableField(value = "logo_image")
    private String logoImage;

    /**
     * 菜系类型
     */
    @TableField(value = "cuisine_type")
    private String cuisineType;

    /**
     * 人均价格
     */
    @TableField(value = "avg_price")
    private BigDecimal avgPrice;

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
     * 服务评分
     */
    @TableField(value = "service_rating")
    private BigDecimal serviceRating;

    /**
     * 出餐速度评分
     */
    @TableField(value = "speed_rating")
    private BigDecimal speedRating;

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
     * 状态 1:营业中 0:已关闭
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
        Stall other = (Stall) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getCanteenId() == null ? other.getCanteenId() == null : this.getCanteenId().equals(other.getCanteenId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getLogoImage() == null ? other.getLogoImage() == null : this.getLogoImage().equals(other.getLogoImage()))
            && (this.getCuisineType() == null ? other.getCuisineType() == null : this.getCuisineType().equals(other.getCuisineType()))
            && (this.getAvgPrice() == null ? other.getAvgPrice() == null : this.getAvgPrice().equals(other.getAvgPrice()))
            && (this.getTotalRating() == null ? other.getTotalRating() == null : this.getTotalRating().equals(other.getTotalRating()))
            && (this.getTasteRating() == null ? other.getTasteRating() == null : this.getTasteRating().equals(other.getTasteRating()))
            && (this.getServiceRating() == null ? other.getServiceRating() == null : this.getServiceRating().equals(other.getServiceRating()))
            && (this.getSpeedRating() == null ? other.getSpeedRating() == null : this.getSpeedRating().equals(other.getSpeedRating()))
            && (this.getReviewCount() == null ? other.getReviewCount() == null : this.getReviewCount().equals(other.getReviewCount()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
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
        result = prime * result + ((getCanteenId() == null) ? 0 : getCanteenId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getLogoImage() == null) ? 0 : getLogoImage().hashCode());
        result = prime * result + ((getCuisineType() == null) ? 0 : getCuisineType().hashCode());
        result = prime * result + ((getAvgPrice() == null) ? 0 : getAvgPrice().hashCode());
        result = prime * result + ((getTotalRating() == null) ? 0 : getTotalRating().hashCode());
        result = prime * result + ((getTasteRating() == null) ? 0 : getTasteRating().hashCode());
        result = prime * result + ((getServiceRating() == null) ? 0 : getServiceRating().hashCode());
        result = prime * result + ((getSpeedRating() == null) ? 0 : getSpeedRating().hashCode());
        result = prime * result + ((getReviewCount() == null) ? 0 : getReviewCount().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
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
        sb.append(", canteenId=").append(canteenId);
        sb.append(", name=").append(name);
        sb.append(", description=").append(description);
        sb.append(", logoImage=").append(logoImage);
        sb.append(", cuisineType=").append(cuisineType);
        sb.append(", avgPrice=").append(avgPrice);
        sb.append(", totalRating=").append(totalRating);
        sb.append(", tasteRating=").append(tasteRating);
        sb.append(", serviceRating=").append(serviceRating);
        sb.append(", speedRating=").append(speedRating);
        sb.append(", reviewCount=").append(reviewCount);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", status=").append(status);
        sb.append(", sortOrder=").append(sortOrder);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}