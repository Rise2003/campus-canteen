package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.UserMapper;
import com.xsq.content.model.po.User;
import com.xsq.content.model.vo.UserSimpleVO;
import com.xsq.content.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
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
        List<User> users = listByIds(userIds);
        if (users == null || users.isEmpty()) return Collections.emptyMap();
        return users.stream().collect(Collectors.toMap(User::getId, u -> UserSimpleVO.builder()
                .id(u.getId())
                .nickname(u.getNickname())
                .avatarUrl(u.getAvatarUrl())
                .build(), (a, b) -> a));
    }

    @Override
    public List<UserSimpleVO> searchUsersByUsername(String keyword, Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;

        if (StringUtils.isBlank(keyword)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1)
                .like(User::getUsername, keyword)
                .orderByDesc(User::getCreatedAt);

        Page<User> mpPage = page(new Page<>(p, s), wrapper);
        List<User> records = mpPage.getRecords();
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream()
                .map(u -> UserSimpleVO.builder()
                        .id(u.getId())
                        // 搜索是按 username，但页面展示往往用 nickname；这里沿用 existing VO
                        .nickname(u.getNickname())
                        .avatarUrl(u.getAvatarUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public Long countSearchUsersByUsername(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return 0L;
        }
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getStatus, 1)
                .like(User::getUsername, keyword);
        return count(wrapper);
    }
}
