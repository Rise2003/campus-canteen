package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.xsq.content.model.po.LikeRecord;

import java.util.List;
import java.util.Map;

public interface ILikeRecordService extends IService<LikeRecord> {

    boolean like(Long userId, Integer targetType, Long targetId);

    boolean cancelLike(Long userId, Integer targetType, Long targetId);

    boolean isLiked(Long targetId, Long userId, Integer targetType);

    Map<Long, Boolean> getLikeStatusBatch(List<Long> targetIds, Long userId, Integer targetType);
}