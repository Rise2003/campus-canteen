package com.xsq.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜品-口味关联表
 * @TableName dish_taste
 */
@TableName(value ="dish_taste")
@Data
public class DishTaste {
    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 菜品ID
     */
    @TableField(value = "dish_id")
    private Long dishId;

    /**
     * 口味标签ID
     */
    @TableField(value = "taste_id")
    private Integer tasteId;

    /**
     * 强度 1:轻微 2:适中 3:浓郁
     */
    @TableField(value = "intensity")
    private Integer intensity;

    /**
     * 创建时间
     */
    @TableField(value = "created_at")
    private LocalDateTime createdAt;

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
        DishTaste other = (DishTaste) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDishId() == null ? other.getDishId() == null : this.getDishId().equals(other.getDishId()))
            && (this.getTasteId() == null ? other.getTasteId() == null : this.getTasteId().equals(other.getTasteId()))
            && (this.getIntensity() == null ? other.getIntensity() == null : this.getIntensity().equals(other.getIntensity()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDishId() == null) ? 0 : getDishId().hashCode());
        result = prime * result + ((getTasteId() == null) ? 0 : getTasteId().hashCode());
        result = prime * result + ((getIntensity() == null) ? 0 : getIntensity().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dishId=").append(dishId);
        sb.append(", tasteId=").append(tasteId);
        sb.append(", intensity=").append(intensity);
        sb.append(", createdAt=").append(createdAt);
        sb.append("]");
        return sb.toString();
    }
}