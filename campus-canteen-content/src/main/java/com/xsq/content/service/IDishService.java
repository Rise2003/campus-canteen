package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.Dish;
import com.xsq.content.model.vo.DishTasteVO;

import java.util.List;

public interface IDishService extends IService<Dish> {

    List<DishTasteVO> getDishTastes(Long dishId);

    void incrementReviewCount(Long dishId);
}