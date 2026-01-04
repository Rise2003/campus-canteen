package com.xsq.content.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户内容聚合表（用户-动态映射 + 冗余统计字段）
 * 表：user_content
 */
@TableName(value = "user_content")
@Data
public class UserContent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "nickname")
    private String nickname;

    @TableField(value = "following_count")
    private Integer followingCount;

    @TableField(value = "follower_count")
    private Integer followerCount;

    @TableField(value = "dynamic_id")
    private Long dynamicId;

    @TableField(value = "created_at")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at")
    private LocalDateTime updatedAt;
}
