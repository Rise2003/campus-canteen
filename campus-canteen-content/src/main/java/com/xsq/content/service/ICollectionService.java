package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.xsq.content.model.po.Collection;

import java.util.List;
import java.util.Map;

public interface ICollectionService extends IService<Collection> {

    boolean collect(Long userId, Integer targetType, Long targetId);

    boolean cancelCollect(Long userId, Integer targetType, Long targetId);

    boolean isCollected(Long targetId, Long userId, Integer targetType);

    Map<Long, Boolean> getCollectStatusBatch(List<Long> targetIds, Long userId, Integer targetType);
}