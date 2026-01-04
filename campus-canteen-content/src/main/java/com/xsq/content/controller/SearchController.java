package com.xsq.content.controller;

import com.xsq.base.common.Result;
import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.vo.*;
import com.xsq.content.service.IDishService;
import com.xsq.content.service.IDynamicService;
import com.xsq.content.service.IUserService;
import com.xsq.base.utils.EsUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 搜索相关接口（供前端搜索栏使用）
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Tag(name = "搜索模块", description = "关键词搜索（动态/菜品/用户）")
@Validated
public class SearchController {

    private final IDynamicService dynamicService;
    private final IDishService dishService;
    private final IUserService userService;
    private final EsUtil esUtil;

    @Operation(summary = "搜索动态", description = "根据关键词搜索动态")
    @GetMapping("/dynamics")
    public Result<DynamicListVO> searchDynamics(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            Long currentUserId = getCurrentUserId(request);

            DynamicQueryDTO queryDTO = DynamicQueryDTO.builder()
                    .page(page)
                    .size(size)
                    .keyword(keyword)
                    .currentUserId(currentUserId)
                    .build();

            DynamicListVO result = dynamicService.getDynamicList(queryDTO);
            return Result.success(result);
        } catch (Exception e) {
            log.error("搜索动态失败，keyword={}", keyword, e);
            return Result.error("搜索动态失败");
        }
    }

    @Operation(summary = "搜索菜品", description = "根据关键词搜索菜品（name/description like）")
    @GetMapping("/dishes")
    public Result<SearchAllVO> searchDishes(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        try {
            List<DishSimpleVO> list = dishService.searchDishes(keyword, page, size);
            Long total = dishService.countSearchDishes(keyword);
            return Result.success(SearchAllVO.builder()
                    .keyword(keyword)
                    .dishes(list)
                    .dishTotal(total)
                    .build());
        } catch (Exception e) {
            log.error("搜索菜品失败，keyword={}", keyword, e);
            return Result.error("搜索菜品失败");
        }
    }

    @Operation(summary = "搜索用户", description = "根据关键词搜索用户（username like）")
    @GetMapping("/users")
    public Result<SearchAllVO> searchUsers(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size) {

        try {
            List<UserSimpleVO> list = userService.searchUsersByUsername(keyword, page, size);
            Long total = userService.countSearchUsersByUsername(keyword);
            return Result.success(SearchAllVO.builder()
                    .keyword(keyword)
                    .users(list)
                    .userTotal(total)
                    .build());
        } catch (Exception e) {
            log.error("搜索用户失败，keyword={}", keyword, e);
            return Result.error("搜索用户失败");
        }
    }

    @Operation(summary = "统一搜索", description = "一次请求搜索动态/菜品/用户（ES）")
    @GetMapping("")
    public Result<SearchAllVO> searchAll(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            // 目前保留 currentUserId 获取逻辑（后续若要注入 isLiked/isCollected 等个性化字段，会用到）
            Long currentUserId = getCurrentUserId(request);

            SearchAllVO vo = esUtil.searchAll(keyword, page, size);
            // TODO: 若后续需要把动态结果补齐 user/canteen/是否点赞收藏等，可在这里二次 enrich

            return Result.success(vo);

        } catch (Exception e) {
            log.error("统一搜索失败(ES)，keyword={}", keyword, e);
            return Result.error("搜索失败");
        }
    }

    private Long getCurrentUserId(HttpServletRequest request) {
        // 与 DynamicController 保持一致的占位实现
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // TODO: parse token
        }
        return null;
    }
}
