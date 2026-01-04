package com.xsq.content.controller;

import com.xsq.base.common.Result;
import com.xsq.content.model.vo.UserContentVO;
import com.xsq.content.service.IUserContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户内容聚合相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/user-content")
@RequiredArgsConstructor
@Tag(name = "用户内容模块", description = "用户内容聚合（关注/粉丝/动态关联）")
@Validated
public class UserContentController {

    private final IUserContentService userContentService;

    @Operation(summary = "获取用户内容聚合", description = "返回用户昵称、关注/粉丝数、以及关联的动态ID列表")
    @GetMapping("/{userId}")
    public Result<UserContentVO> getUserContent(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {
        try {
            return Result.success(userContentService.getUserContent(userId, page, size));
        } catch (Exception e) {
            log.error("获取用户内容聚合失败，userId={}", userId, e);
            return Result.error("获取用户内容失败");
        }
    }

    @Operation(summary = "绑定用户-动态", description = "写入 user_content(user_id, dynamic_id) 关联（幂等）")
    @PostMapping("/bind")
    public Result<Boolean> bindDynamic(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "动态ID", required = true) @RequestParam Long dynamicId) {
        try {
            return Result.success(userContentService.bindDynamic(userId, dynamicId));
        } catch (Exception e) {
            log.error("绑定用户动态失败 userId={}, dynamicId={}", userId, dynamicId, e);
            return Result.error("绑定失败");
        }
    }
}

