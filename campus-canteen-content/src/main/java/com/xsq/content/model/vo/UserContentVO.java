package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 用户内容聚合返回
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserContentVO implements Serializable {

    private Long userId;

    private String nickname;

    private Integer followingCount;

    private Integer followerCount;

    /**
     * 该用户关联的动态ID列表（从 user_content 表读取）
     *
     * 说明：为兼容旧字段/轻量返回保留。
     */
    private List<Long> dynamicIds;

    /**
     * 动态详情列表（按 user_content 记录顺序返回）
     */
    private List<DynamicVO> dynamics;

    private Long total;

    private Integer page;

    private Integer size;

    private Boolean hasNext;
}
