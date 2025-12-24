package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.dto.DynamicDishDTO;
import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.po.Dynamic;
import com.xsq.content.model.vo.DynamicListVO;
import com.xsq.content.model.vo.DynamicVO;

import java.util.List;

/**
 * 动态服务接口
 */
public interface IDynamicService extends IService<Dynamic> {

    /**
     * 获取动态列表
     */
    DynamicListVO getDynamicList(DynamicQueryDTO queryDTO);

    /**
     * 获取动态详情
     */
    DynamicVO getDynamicDetail(Long dynamicId, Long currentUserId);

    /**
     * 增加动态浏览量
     */
    boolean incrementViewCount(Long dynamicId);

    /**
     * 点赞动态
     */
    boolean likeDynamic(Long dynamicId, Long userId);

    /**
     * 取消点赞动态
     */
    boolean cancelLikeDynamic(Long dynamicId, Long userId);

    /**
     * 收藏动态
     */
    boolean collectDynamic(Long dynamicId, Long userId);

    /**
     * 取消收藏动态
     */
    boolean cancelCollectDynamic(Long dynamicId, Long userId);

    /**
     * 获取用户动态数量
     */
    Long countUserDynamics(Long userId);

    /**
     * 检查用户是否有权限操作动态
     */
    boolean checkDynamicPermission(Long dynamicId, Long userId);

    /**
     * 发布动态
     */
    Long publishDynamic(Dynamic dynamic, List<Long> dishIds, List<DynamicDishDTO> dishRatings);

    /**
     * 删除动态（软删除）
     */
    boolean deleteDynamic(Long dynamicId, Long userId);
}