package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.DishMapper;
import com.xsq.content.model.po.Dish;
import com.xsq.content.model.vo.DishTasteVO;
import com.xsq.content.service.IDishService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Override
    public List<DishTasteVO> getDishTastes(Long dishId) {
        // TODO: 后续可结合 DishTasteService/TasteTag 等实现
        return Collections.emptyList();
    }

    @Override
    public void incrementReviewCount(Long dishId) {
        // TODO: 若 dish 表有 review_count 可在此实现 setSql 增量
    }
}

