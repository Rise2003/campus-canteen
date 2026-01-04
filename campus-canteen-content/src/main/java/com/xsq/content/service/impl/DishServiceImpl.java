package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.DishMapper;
import com.xsq.content.model.po.Dish;
import com.xsq.content.model.vo.DishSimpleVO;
import com.xsq.content.model.vo.DishTasteVO;
import com.xsq.content.service.IDishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<DishSimpleVO> searchDishes(String keyword, Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;

        if (StringUtils.isBlank(keyword)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1)
                .and(w -> w.like(Dish::getName, keyword).or().like(Dish::getDescription, keyword))
                .orderByAsc(Dish::getSortOrder)
                .orderByDesc(Dish::getCreatedAt);

        Page<Dish> mpPage = page(new Page<>(p, s), wrapper);
        List<Dish> records = mpPage.getRecords();
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream()
                .map(d -> DishSimpleVO.builder()
                        .id(d.getId())
                        .canteenId(d.getCanteenId())
                        .name(d.getName())
                        .coverImage(d.getCoverImage())
                        .price(d.getPrice())
                        .totalRating(d.getTotalRating())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long countSearchDishes(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return 0L;
        }
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Dish::getStatus, 1)
                .and(w -> w.like(Dish::getName, keyword).or().like(Dish::getDescription, keyword));
        return count(wrapper);
    }
}
