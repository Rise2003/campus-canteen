package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.LikeRecordMapper;
import com.xsq.content.model.po.LikeRecord;
import com.xsq.content.service.ILikeRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LikeRecordServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements ILikeRecordService {

    @Override
    public boolean like(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) return false;
        if (isLiked(targetId, userId, targetType)) return true;

        LikeRecord lr = new LikeRecord();
        lr.setUserId(userId);
        lr.setTargetType(targetType);
        lr.setTargetId(targetId);
        lr.setStatus(1);
        lr.setCreatedAt(LocalDateTime.now());
        lr.setUpdatedAt(LocalDateTime.now());
        return save(lr);
    }

    @Override
    public boolean cancelLike(Long userId, Integer targetType, Long targetId) {
        if (userId == null || targetType == null || targetId == null) return false;
        return update(new LambdaUpdateWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId)
                .set(LikeRecord::getStatus, 0)
                .set(LikeRecord::getUpdatedAt, LocalDateTime.now()));
    }

    @Override
    public boolean isLiked(Long targetId, Long userId, Integer targetType) {
        if (targetId == null || userId == null || targetType == null) return false;
        return count(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getTargetId, targetId)
                .eq(LikeRecord::getStatus, 1)) > 0;
    }

    @Override
    public Map<Long, Boolean> getLikeStatusBatch(List<Long> targetIds, Long userId, Integer targetType) {
        if (targetIds == null || targetIds.isEmpty() || userId == null || targetType == null) return Collections.emptyMap();

        List<LikeRecord> likes = list(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, targetType)
                .eq(LikeRecord::getStatus, 1)
                .in(LikeRecord::getTargetId, targetIds));

        Map<Long, Boolean> map = new HashMap<>();
        for (Long id : targetIds) map.put(id, false);
        for (LikeRecord lr : likes) {
            map.put(lr.getTargetId(), true);
        }
        return map;
    }
}

