package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.DynamicDishMapper;
import com.xsq.content.model.po.DynamicDish;
import com.xsq.content.service.IDynamicDishService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DynamicDishServiceImpl extends ServiceImpl<DynamicDishMapper, DynamicDish> implements IDynamicDishService {

    @Override
    public List<DynamicDish> getByDynamicId(Long dynamicId) {
        if (dynamicId == null) return Collections.emptyList();
        return list(new LambdaQueryWrapper<DynamicDish>().eq(DynamicDish::getDynamicId, dynamicId));
    }

    @Override
    public boolean saveBatch(Long dynamicId, List<Long> dishIds) {
        if (dynamicId == null || dishIds == null || dishIds.isEmpty()) return false;
        List<DynamicDish> list = dishIds.stream().filter(id -> id != null).distinct().map(dishId -> {
            DynamicDish dd = new DynamicDish();
            dd.setDynamicId(dynamicId);
            dd.setDishId(dishId);
            dd.setCreatedAt(LocalDateTime.now());
            return dd;
        }).collect(Collectors.toList());
        return saveBatch(list);
    }
}

