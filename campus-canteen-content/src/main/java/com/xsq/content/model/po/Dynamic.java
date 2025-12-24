package com.xsq.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 动态表
 * @TableName dynamic
 */
@TableName(value ="dynamic")
@Data
public class Dynamic {
    /**
     * 动态ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 食堂ID
     */
    @TableField(value = "canteen_id")
    private Long canteenId;

    /**
     * 动态标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 动态内容
     */
    @TableField(value = "content")
    private String content;

    /**
     * 图片URL(JSON数组)
     */
    @TableField(value = "images")
    private String images;

    /**
     * 总体评分
     */
    @TableField(value = "total_rating")
    private BigDecimal totalRating;

    /**
     * 口味评分
     */
    @TableField(value = "taste_rating")
    private BigDecimal tasteRating;

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
     * 价格评分
     */
    @TableField(value = "price_rating")
    private BigDecimal priceRating;

    /**
     * 点赞数
     */
    @TableField(value = "like_count")
    private Integer likeCount;

    /**
     * 评论数
     */
    @TableField(value = "comment_count")
    private Integer commentCount;

    /**
     * 分享数
     */
    @TableField(value = "share_count")
    private Integer shareCount;

    /**
     * 浏览数
     */
    @TableField(value = "view_count")
    private Integer viewCount;

    /**
     * 收藏数
     */
    @TableField(value = "collect_count")
    private Integer collectCount;

    /**
     * 是否推荐 1:是 0:否
     */
    @TableField(value = "is_recommended")
    private Integer isRecommended;

    /**
     * 允许评论 1:是 0:否
     */
    @TableField(value = "allow_comment")
    private Integer allowComment;

    /**
     * 显示位置 1:是 0:否
     */
    @TableField(value = "show_location")
    private Integer showLocation;

    /**
     * 状态 1:正常 2:隐藏 3:删除
     */
    @TableField(value = "status")
    private Integer status;

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
     * 位置名称
     */
    @TableField(value = "location_name")
    private String locationName;

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
        Dynamic other = (Dynamic) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getCanteenId() == null ? other.getCanteenId() == null : this.getCanteenId().equals(other.getCanteenId()))
            && (this.getTitle() == null ? other.getTitle() == null : this.getTitle().equals(other.getTitle()))
            && (this.getContent() == null ? other.getContent() == null : this.getContent().equals(other.getContent()))
            && (this.getImages() == null ? other.getImages() == null : this.getImages().equals(other.getImages()))
            && (this.getTotalRating() == null ? other.getTotalRating() == null : this.getTotalRating().equals(other.getTotalRating()))
            && (this.getTasteRating() == null ? other.getTasteRating() == null : this.getTasteRating().equals(other.getTasteRating()))
            && (this.getEnvironmentRating() == null ? other.getEnvironmentRating() == null : this.getEnvironmentRating().equals(other.getEnvironmentRating()))
            && (this.getServiceRating() == null ? other.getServiceRating() == null : this.getServiceRating().equals(other.getServiceRating()))
            && (this.getPriceRating() == null ? other.getPriceRating() == null : this.getPriceRating().equals(other.getPriceRating()))
            && (this.getLikeCount() == null ? other.getLikeCount() == null : this.getLikeCount().equals(other.getLikeCount()))
            && (this.getCommentCount() == null ? other.getCommentCount() == null : this.getCommentCount().equals(other.getCommentCount()))
            && (this.getShareCount() == null ? other.getShareCount() == null : this.getShareCount().equals(other.getShareCount()))
            && (this.getViewCount() == null ? other.getViewCount() == null : this.getViewCount().equals(other.getViewCount()))
            && (this.getCollectCount() == null ? other.getCollectCount() == null : this.getCollectCount().equals(other.getCollectCount()))
            && (this.getIsRecommended() == null ? other.getIsRecommended() == null : this.getIsRecommended().equals(other.getIsRecommended()))
            && (this.getAllowComment() == null ? other.getAllowComment() == null : this.getAllowComment().equals(other.getAllowComment()))
            && (this.getShowLocation() == null ? other.getShowLocation() == null : this.getShowLocation().equals(other.getShowLocation()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getLatitude() == null ? other.getLatitude() == null : this.getLatitude().equals(other.getLatitude()))
            && (this.getLongitude() == null ? other.getLongitude() == null : this.getLongitude().equals(other.getLongitude()))
            && (this.getLocationName() == null ? other.getLocationName() == null : this.getLocationName().equals(other.getLocationName()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getCanteenId() == null) ? 0 : getCanteenId().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((getImages() == null) ? 0 : getImages().hashCode());
        result = prime * result + ((getTotalRating() == null) ? 0 : getTotalRating().hashCode());
        result = prime * result + ((getTasteRating() == null) ? 0 : getTasteRating().hashCode());
        result = prime * result + ((getEnvironmentRating() == null) ? 0 : getEnvironmentRating().hashCode());
        result = prime * result + ((getServiceRating() == null) ? 0 : getServiceRating().hashCode());
        result = prime * result + ((getPriceRating() == null) ? 0 : getPriceRating().hashCode());
        result = prime * result + ((getLikeCount() == null) ? 0 : getLikeCount().hashCode());
        result = prime * result + ((getCommentCount() == null) ? 0 : getCommentCount().hashCode());
        result = prime * result + ((getShareCount() == null) ? 0 : getShareCount().hashCode());
        result = prime * result + ((getViewCount() == null) ? 0 : getViewCount().hashCode());
        result = prime * result + ((getCollectCount() == null) ? 0 : getCollectCount().hashCode());
        result = prime * result + ((getIsRecommended() == null) ? 0 : getIsRecommended().hashCode());
        result = prime * result + ((getAllowComment() == null) ? 0 : getAllowComment().hashCode());
        result = prime * result + ((getShowLocation() == null) ? 0 : getShowLocation().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getLatitude() == null) ? 0 : getLatitude().hashCode());
        result = prime * result + ((getLongitude() == null) ? 0 : getLongitude().hashCode());
        result = prime * result + ((getLocationName() == null) ? 0 : getLocationName().hashCode());
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
        sb.append(", userId=").append(userId);
        sb.append(", canteenId=").append(canteenId);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", images=").append(images);
        sb.append(", totalRating=").append(totalRating);
        sb.append(", tasteRating=").append(tasteRating);
        sb.append(", environmentRating=").append(environmentRating);
        sb.append(", serviceRating=").append(serviceRating);
        sb.append(", priceRating=").append(priceRating);
        sb.append(", likeCount=").append(likeCount);
        sb.append(", commentCount=").append(commentCount);
        sb.append(", shareCount=").append(shareCount);
        sb.append(", viewCount=").append(viewCount);
        sb.append(", collectCount=").append(collectCount);
        sb.append(", isRecommended=").append(isRecommended);
        sb.append(", allowComment=").append(allowComment);
        sb.append(", showLocation=").append(showLocation);
        sb.append(", status=").append(status);
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append(", locationName=").append(locationName);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}