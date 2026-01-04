package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.User;
import com.xsq.content.model.vo.UserSimpleVO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IUserService extends IService<User> {

    UserSimpleVO getSimpleUserInfo(Long userId);

    boolean isAdmin(Long userId);

    void incrementDynamicCount(Long userId);

    void decrementDynamicCount(Long userId);

    Map<Long, UserSimpleVO> batchGetSimpleUserInfo(Set<Long> userIds);

    /**
     * 关键词搜索用户（username like）
     */
    List<UserSimpleVO> searchUsersByUsername(String keyword, Integer page, Integer size);

    /**
     * 返回关键词搜索用户的总数（用于分页）
     */
    Long countSearchUsersByUsername(String keyword);
}