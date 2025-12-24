package com.xsq.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 食堂表
 * @TableName canteen
 */
@TableName(value ="canteen")
@Data
public class Canteen {
    /**
     * 食堂ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 食堂名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 食堂编码
     */
    @TableField(value = "code")
    private String code;

    /**
     * 封面图片
     */
    @TableField(value = "cover_image")
    private String coverImage;

    /**
     * 食堂描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 地址
     */
    @TableField(value = "address")
    private String address;

    /**
     * 纬度
     */
    @TableField(value = "latitude")
    private BigDecimal latitude;

    /**
     * 经度
     */
    @TableField(value = "longitude")
    private BigDecimal longitude;

    /**
     * 所在楼层
     */
    @TableField(value = "floor")
    private String floor;

    /**
     * 开放时间
     */
    @TableField(value = "open_time")
    private String openTime;

    /**
     * 联系电话
     */
    @TableField(value = "contact_phone")
    private String contactPhone;

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
     * 环境评分
     */
    @TableField(value = "environment_rating")
    private BigDecimal environmentRating;

    /**
     * 服务评分
     */
    @TableField(value = "service_rating")
    private BigDecimal serviceRating;

    /**
     * 卫生评分
     */
    @TableField(value = "hygiene_rating")
    private BigDecimal hygieneRating;

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
     * 浏览数
     */
    @TableField(value = "view_count")
    private Integer viewCount;

    /**
     * 状态 1:营业中 0:已关闭
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 管理员ID
     */
    @TableField(value = "manager_id")
    private Long managerId;

    /**
     * 创建人
     */
    @TableField(value = "created_by")
    private Long createdBy;

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
        Canteen other = (Canteen) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getName() == null ? other.getName() == null : this.getName().equals(other.getName()))
            && (this.getCode() == null ? other.getCode() == null : this.getCode().equals(other.getCode()))
            && (this.getCoverImage() == null ? other.getCoverImage() == null : this.getCoverImage().equals(other.getCoverImage()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
            && (this.getLatitude() == null ? other.getLatitude() == null : this.getLatitude().equals(other.getLatitude()))
            && (this.getLongitude() == null ? other.getLongitude() == null : this.getLongitude().equals(other.getLongitude()))
            && (this.getFloor() == null ? other.getFloor() == null : this.getFloor().equals(other.getFloor()))
            && (this.getOpenTime() == null ? other.getOpenTime() == null : this.getOpenTime().equals(other.getOpenTime()))
            && (this.getContactPhone() == null ? other.getContactPhone() == null : this.getContactPhone().equals(other.getContactPhone()))
            && (this.getAvgPrice() == null ? other.getAvgPrice() == null : this.getAvgPrice().equals(other.getAvgPrice()))
            && (this.getTotalRating() == null ? other.getTotalRating() == null : this.getTotalRating().equals(other.getTotalRating()))
            && (this.getEnvironmentRating() == null ? other.getEnvironmentRating() == null : this.getEnvironmentRating().equals(other.getEnvironmentRating()))
            && (this.getServiceRating() == null ? other.getServiceRating() == null : this.getServiceRating().equals(other.getServiceRating()))
            && (this.getHygieneRating() == null ? other.getHygieneRating() == null : this.getHygieneRating().equals(other.getHygieneRating()))
            && (this.getReviewCount() == null ? other.getReviewCount() == null : this.getReviewCount().equals(other.getReviewCount()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getManagerId() == null ? other.getManagerId() == null : this.getManagerId().equals(other.getManagerId()))
            && (this.getCreatedBy() == null ? other.getCreatedBy() == null : this.getCreatedBy().equals(other.getCreatedBy()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
        result = prime * result + ((getCode() == null) ? 0 : getCode().hashCode());
        result = prime * result + ((getCoverImage() == null) ? 0 : getCoverImage().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getLatitude() == null) ? 0 : getLatitude().hashCode());
        result = prime * result + ((getLongitude() == null) ? 0 : getLongitude().hashCode());
        result = prime * result + ((getFloor() == null) ? 0 : getFloor().hashCode());
        result = prime * result + ((getOpenTime() == null) ? 0 : getOpenTime().hashCode());
        result = prime * result + ((getContactPhone() == null) ? 0 : getContactPhone().hashCode());
        result = prime * result + ((getAvgPrice() == null) ? 0 : getAvgPrice().hashCode());
        result = prime * result + ((getTotalRating() == null) ? 0 : getTotalRating().hashCode());
        result = prime * result + ((getEnvironmentRating() == null) ? 0 : getEnvironmentRating().hashCode());
        result = prime * result + ((getServiceRating() == null) ? 0 : getServiceRating().hashCode());
        result = prime * result + ((getHygieneRating() == null) ? 0 : getHygieneRating().hashCode());
        result = prime * result + ((getReviewCount() == null) ? 0 : getReviewCount().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getManagerId() == null) ? 0 : getManagerId().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
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
        sb.append(", name=").append(name);
        sb.append(", code=").append(code);
        sb.append(", coverImage=").append(coverImage);
        sb.append(", description=").append(description);
        sb.append(", address=").append(address);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", floor=").append(floor);
        sb.append(", openTime=").append(openTime);
        sb.append(", contactPhone=").append(contactPhone);
        sb.append(", avgPrice=").append(avgPrice);
        sb.append(", totalRating=").append(totalRating);
        sb.append(", environmentRating=").append(environmentRating);
        sb.append(", serviceRating=").append(serviceRating);
        sb.append(", hygieneRating=").append(hygieneRating);
        sb.append(", reviewCount=").append(reviewCount);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", status=").append(status);
        sb.append(", managerId=").append(managerId);
        sb.append(", createdBy=").append(createdBy);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}