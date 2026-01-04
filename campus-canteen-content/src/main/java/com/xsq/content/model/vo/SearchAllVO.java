package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 统一关键词搜索返回
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchAllVO implements Serializable {

    /** 搜索关键词（回显） */
    private String keyword;

    /** 动态（直接复用现有分页结构） */
    private DynamicListVO dynamics;

    /** 菜品 */
    private List<DishSimpleVO> dishes;
    private Long dishTotal;

    /** 用户 */
    private List<UserSimpleVO> users;
    private Long userTotal;
}

