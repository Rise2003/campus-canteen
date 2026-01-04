package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.UserContentMapper;
import com.xsq.content.model.po.Follow;
import com.xsq.content.model.po.User;
import com.xsq.content.model.po.UserContent;
import com.xsq.content.model.vo.DynamicVO;
import com.xsq.content.model.vo.UserContentVO;
import com.xsq.content.service.IDynamicService;
import com.xsq.content.service.IFollowService;
import com.xsq.content.service.IUserContentService;
import com.xsq.content.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserContentServiceImpl extends ServiceImpl<UserContentMapper, UserContent> implements IUserContentService {

    private final IUserService userService;
    private final IFollowService followService;
    private final IDynamicService dynamicService;

    public UserContentServiceImpl(IUserService userService, IFollowService followService, IDynamicService dynamicService) {
        this.userService = userService;
        this.followService = followService;
        this.dynamicService = dynamicService;
    }

    @Override
    public UserContentVO getUserContent(Long userId, Integer page, Integer size) {
        int p = (page == null || page < 1) ? 1 : page;
        int s = (size == null || size < 1) ? 10 : size;

        if (userId == null) {
            return UserContentVO.builder()
                    .userId(null)
                    .dynamicIds(Collections.emptyList())
                    .dynamics(Collections.emptyList())
                    .total(0L)
                    .page(p)
                    .size(s)
                    .hasNext(false)
                    .build();
        }

        User user = userService.getById(userId);
        String nickname = user == null ? null : user.getNickname();

        // 关注/粉丝数：从 follow 表统计 status=1
        int followingCount = (int) followService.count(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, userId)
                .eq(Follow::getStatus, 1));
        int followerCount = (int) followService.count(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowingId, userId)
                .eq(Follow::getStatus, 1));

        LambdaQueryWrapper<UserContent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserContent::getUserId, userId)
                .orderByDesc(UserContent::getCreatedAt);

        Page<UserContent> mpPage = page(new Page<>(p, s), wrapper);

        List<Long> dynamicIds = mpPage.getRecords() == null ? Collections.emptyList() : mpPage.getRecords().stream()
                .map(UserContent::getDynamicId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 批量组装 DynamicVO（保持与 dynamicIds 同序；不存在/异常状态的动态会被跳过）
        List<DynamicVO> dynamics = dynamicService.getDynamicDetails(dynamicIds, null);

        boolean hasNext = mpPage.getTotal() > (long) p * s;

        return UserContentVO.builder()
                .userId(userId)
                .nickname(nickname)
                .followingCount(followingCount)
                .followerCount(followerCount)
                .dynamicIds(dynamicIds)
                .dynamics(dynamics)
                .total(mpPage.getTotal())
                .page(p)
                .size(s)
                .hasNext(hasNext)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindDynamic(Long userId, Long dynamicId) {
        if (userId == null || dynamicId == null) return false;

        // 幂等：避免重复插入
        long exists = count(new LambdaQueryWrapper<UserContent>()
                .eq(UserContent::getUserId, userId)
                .eq(UserContent::getDynamicId, dynamicId));
        if (exists > 0) return true;

        User user = userService.getById(userId);
        String nickname = user == null ? null : user.getNickname();

        // 冗余统计字段：这里写入时顺手计算一次（展示时仍建议实时统计或定时同步）
        int followingCount = (int) followService.count(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, userId)
                .eq(Follow::getStatus, 1));
        int followerCount = (int) followService.count(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowingId, userId)
                .eq(Follow::getStatus, 1));

        UserContent uc = new UserContent();
        uc.setUserId(userId);
        uc.setNickname(nickname);
        uc.setFollowingCount(followingCount);
        uc.setFollowerCount(followerCount);
        uc.setDynamicId(dynamicId);
        uc.setCreatedAt(LocalDateTime.now());
        uc.setUpdatedAt(LocalDateTime.now());

        return save(uc);
    }
}
