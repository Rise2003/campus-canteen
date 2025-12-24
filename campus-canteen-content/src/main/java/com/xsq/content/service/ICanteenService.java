package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.Canteen;
import com.xsq.content.model.vo.CanteenSimpleVO;

import java.util.Map;
import java.util.Set;

public interface ICanteenService extends IService<Canteen> {

    CanteenSimpleVO getSimpleCanteenInfo(Long canteenId);

    void incrementDynamicCount(Long canteenId);

    void decrementDynamicCount(Long canteenId);

    Map<Long, CanteenSimpleVO> batchGetSimpleCanteenInfo(Set<Long> canteenIds);
}