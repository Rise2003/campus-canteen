package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.Dish;
import com.xsq.content.model.vo.DishSimpleVO;
import com.xsq.content.model.vo.DishTasteVO;

import java.util.List;

public interface IDishService extends IService<Dish> {

    List<DishTasteVO> getDishTastes(Long dishId);

    void incrementReviewCount(Long dishId);

    /**
     * 关键词搜索菜品（name/description）
     */
    List<DishSimpleVO> searchDishes(String keyword, Integer page, Integer size);

    /**
     * 返回关键词搜索菜品的总数（用于分页）
     */
    Long countSearchDishes(String keyword);
}