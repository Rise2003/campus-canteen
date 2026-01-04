package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.DynamicDish;

import java.util.List;

public interface IDynamicDishService extends IService<DynamicDish> {

    List<DynamicDish> getByDynamicId(Long dynamicId);

    boolean saveBatch(Long dynamicId, List<Long> dishIds);
}