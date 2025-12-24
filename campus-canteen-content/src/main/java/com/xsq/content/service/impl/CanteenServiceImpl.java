package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.CanteenMapper;
import com.xsq.content.model.po.Canteen;
import com.xsq.content.model.vo.CanteenSimpleVO;
import com.xsq.content.service.ICanteenService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CanteenServiceImpl extends ServiceImpl<CanteenMapper, Canteen> implements ICanteenService {

    @Override
    public CanteenSimpleVO getSimpleCanteenInfo(Long canteenId) {
        if (canteenId == null) return null;
        Canteen c = getById(canteenId);
        if (c == null) return null;
        return CanteenSimpleVO.builder()
                .id(c.getId())
                .name(c.getName())
                .coverImage(c.getCoverImage())
                .build();
    }

    @Override
    public void incrementDynamicCount(Long canteenId) {
        // TODO: 若 canteen 表有 dynamic_count 字段，可在此实现增量更新
    }

    @Override
    public void decrementDynamicCount(Long canteenId) {
        // TODO: 若 canteen 表有 dynamic_count 字段，可在此实现增量更新
    }

    @Override
    public Map<Long, CanteenSimpleVO> batchGetSimpleCanteenInfo(Set<Long> canteenIds) {
        if (canteenIds == null || canteenIds.isEmpty()) return Collections.emptyMap();
        return listByIds(canteenIds).stream().collect(Collectors.toMap(Canteen::getId, c -> CanteenSimpleVO.builder()
                .id(c.getId())
                .name(c.getName())
                .coverImage(c.getCoverImage())
                .build(), (a, b) -> a));
    }
}

