package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.CollectionMapper;
import com.xsq.content.model.po.Collection;
import com.xsq.content.service.ICollectionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection> implements ICollectionService {

    @Override
    public boolean collect(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) return false;
        if (isCollected(targetId, userId, targetType)) return true;

        Collection c = new Collection();
        c.setUserId(userId);
        c.setTargetType(targetType);
        c.setTargetId(targetId);
        c.setStatus(1);
        c.setCreatedAt(LocalDateTime.now());
        c.setUpdatedAt(LocalDateTime.now());
        return save(c);
    }

    @Override
    public boolean cancelCollect(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) return false;
        return update(new LambdaUpdateWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getTargetType, targetType)
                .eq(Collection::getTargetId, targetId)
                .set(Collection::getStatus, 0)
                .set(Collection::getUpdatedAt, LocalDateTime.now()));
    }

    @Override
    public boolean isCollected(Long targetId, Long userId, Integer targetType) {
        if (targetId == null || userId == null || targetType == null) return false;
        return count(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getTargetType, targetType)
                .eq(Collection::getTargetId, targetId)
                .eq(Collection::getStatus, 1)) > 0;
    }

    @Override
    public Map<Long, Boolean> getCollectStatusBatch(List<Long> targetIds, Long userId, Integer targetType) {
        if (targetIds == null || targetIds.isEmpty() || userId == null || targetType == null) return Collections.emptyMap();

        List<Collection> collects = list(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getTargetType, targetType)
                .eq(Collection::getStatus, 1)
                .in(Collection::getTargetId, targetIds));

        Map<Long, Boolean> map = new HashMap<>();
        for (Long id : targetIds) map.put(id, false);
        for (Collection c : collects) {
            map.put(c.getTargetId(), true);
        }
        return map;
    }
}

