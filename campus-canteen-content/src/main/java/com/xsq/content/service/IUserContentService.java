package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.UserContent;
import com.xsq.content.model.vo.UserContentVO;

/**
 * 用户内容聚合服务
 */
public interface IUserContentService extends IService<UserContent> {

    /**
     * 获取指定用户的内容聚合信息（含关注/粉丝数 + 动态条目数/列表）
     */
    UserContentVO getUserContent(Long userId, Integer page, Integer size);

    /**
     * 将某条动态写入 user_content（幂等：同一 userId+dynamicId 只保留一条）
     */
    boolean bindDynamic(Long userId, Long dynamicId);
}

