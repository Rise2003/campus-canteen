package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.UserMapper;
import com.xsq.content.model.po.User;
import com.xsq.content.model.vo.UserSimpleVO;
import com.xsq.content.service.IUserService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public UserSimpleVO getSimpleUserInfo(Long userId) {
        if (userId == null) return null;
        User u = getById(userId);
        if (u == null) return null;
        return UserSimpleVO.builder()
                .id(u.getId())
                .nickname(u.getNickname())
                .avatarUrl(u.getAvatarUrl())
                .build();
    }

    @Override
    public boolean isAdmin(Long userId) {
        // TODO: 根据实际业务补充角色/权限逻辑
        return false;
    }

    @Override
    public void incrementDynamicCount(Long userId) {
        // TODO: 若 user 表有 dynamic_count 字段，可在此实现增量更新
    }

    @Override
    public void decrementDynamicCount(Long userId) {
        // TODO: 若 user 表有 dynamic_count 字段，可在此实现增量更新
    }

    @Override
    public Map<Long, UserSimpleVO> batchGetSimpleUserInfo(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();
        return listByIds(userIds).stream().collect(Collectors.toMap(User::getId, u -> UserSimpleVO.builder()
                .id(u.getId())
                .nickname(u.getNickname())
                .avatarUrl(u.getAvatarUrl())
                .build(), (a, b) -> a));
    }
}

