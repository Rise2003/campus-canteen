package com.xsq.content.controller;

import com.xsq.base.common.Result;
import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.vo.DynamicListVO;
import com.xsq.content.model.vo.DynamicVO;
import com.xsq.content.service.IDynamicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
 

/**
 * 动态相关接口
 */
@Slf4j
@RestController
@RequestMapping("/api/dynamic")
@RequiredArgsConstructor
@Tag(name = "动态模块", description = "动态相关接口")
@Validated
public class DynamicController {

    private final IDynamicService dynamicService;

    /**
     * 获取首页动态列表（瀑布流）
     */
    @Operation(summary = "获取动态列表", description = "首页瀑布流动态列表，支持多种排序和筛选")
    @GetMapping("/list")
    public Result<DynamicListVO> getDynamicList(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量，默认10") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序字段: createdAt, likeCount, commentCount")
            @RequestParam(defaultValue = "createdAt") String sort,
            @Parameter(description = "排序方向: desc, asc")
            @RequestParam(defaultValue = "desc") String order,
            @Parameter(description = "菜品ID筛选") @RequestParam(required = false) Long dishId,
            @Parameter(description = "食堂ID筛选") @RequestParam(required = false) Long canteenId,
            @Parameter(description = "用户ID筛选") @RequestParam(required = false) Long userId,
            @Parameter(description = "关键词搜索") @RequestParam(required = false) String keyword,
            @Parameter(description = "最低评分") @RequestParam(required = false) java.math.BigDecimal minRating,
            @Parameter(description = "仅关注用户动态") @RequestParam(defaultValue = "false") Boolean followingOnly,
            @Parameter(description = "仅推荐动态(契约参数 recommended)") @RequestParam(required = false) Boolean recommended,
            @Parameter(description = "兼容参数: recommendedOnly") @RequestParam(required = false) Boolean recommendedOnly,
            HttpServletRequest request) {

        try {
            Long currentUserId = getCurrentUserId(request);

            // 兼容：若 recommended 未传，则回退到 recommendedOnly
            Boolean rec = (recommended != null) ? recommended : recommendedOnly;

            DynamicQueryDTO queryDTO = DynamicQueryDTO.builder()
                    .page(page)
                    .size(size)
                    .sort(sort)
                    .order(order)
                    .dishId(dishId)
                    .canteenId(canteenId)
                    .userId(userId)
                    .keyword(keyword)
                    .minRating(minRating)
                    .followingOnly(followingOnly)
                    .recommended(rec)
                    .recommendedOnly(recommendedOnly)
                    .currentUserId(currentUserId)
                    .build();

            DynamicListVO result = dynamicService.getDynamicList(queryDTO);

            log.info("获取动态列表成功，page={}, size={}, total={}", page, size, result.getTotal());
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取动态列表失败", e);
            return Result.error("获取动态列表失败");
        }
    }

    /**
     * 获取动态详情
     */
    @Operation(summary = "获取动态详情", description = "获取动态的完整信息，包括用户、食堂、菜品、评论等")
    @GetMapping("/{id}")
    public Result<DynamicVO> getDynamicDetail(
            @Parameter(description = "动态ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {

        try {
            Long currentUserId = getCurrentUserId(request);
            DynamicVO dynamicVO = dynamicService.getDynamicDetail(id, currentUserId);

            log.info("获取动态详情成功，dynamicId={}", id);
            return Result.success(dynamicVO);

        } catch (Exception e) {
            log.error("获取动态详情失败，dynamicId={}", id, e);
            return Result.error("获取动态详情失败");
        }
    }

    /**
     * 推荐动态（个性化推荐）
     */
    @Operation(summary = "获取推荐动态", description = "基于用户行为和偏好的个性化推荐")
    @GetMapping("/recommend")
    public Result<DynamicListVO> getRecommendedDynamics(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            Long currentUserId = getCurrentUserId(request);

            DynamicQueryDTO queryDTO = DynamicQueryDTO.builder()
                    .page(page)
                    .size(size)
                    .sort("recommend")
                    .currentUserId(currentUserId)
                    .recommendedOnly(true)
                    .build();

            DynamicListVO result = dynamicService.getDynamicList(queryDTO);

            log.info("获取推荐动态成功，userId={}, count={}", currentUserId, result.getList().size());
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取推荐动态失败", e);
            return Result.error("获取推荐动态失败");
        }
    }

    /**
     * 热门动态（按浏览量排序 + 时间范围可降级）
     */
    @Operation(
            summary = "获取热门动态",
            description = "热门动态：按浏览量(view_count)倒序；timeRange 支持 today/week/month；体验语义：若所选范围无数据则自动降级 week->month->all"
    )
    @GetMapping("/hot")
    public Result<DynamicListVO> getHotDynamics(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "时间范围：today-本日, week-本周, month-本月（体验语义会自动降级）")
            @RequestParam(defaultValue = "week") String timeRange) {

        try {
            DynamicQueryDTO queryDTO = DynamicQueryDTO.builder()
                    .page(page)
                    .size(size)
                    .sort("hot")
                    .order("desc")
                    .timeRange(timeRange)
                    .build();

            DynamicListVO result = dynamicService.getDynamicList(queryDTO);

            log.info("获取热门动态成功，timeRange={}, count={}", timeRange, result.getList().size());
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取热门动态失败", e);
            return Result.error("获取热门动态失败");
        }
    }

    /**
     * 附近动态（基于地理位置）
     */
    @Operation(summary = "获取附近动态", description = "根据地理位置获取附近的动态")
    @GetMapping("/nearby")
    public Result<DynamicListVO> getNearbyDynamics(
            @Parameter(description = "纬度", required = true) @RequestParam Double latitude,
            @Parameter(description = "经度", required = true) @RequestParam Double longitude,
            @Parameter(description = "半径(米)，默认1000") @RequestParam(defaultValue = "1000") Integer radius,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            Long currentUserId = getCurrentUserId(request);

            DynamicQueryDTO queryDTO = DynamicQueryDTO.builder()
                    .page(page)
                    .size(size)
                    .sort("distance")
                    .order("asc")
                    .latitude(latitude)
                    .longitude(longitude)
                    .radius(radius)
                    .currentUserId(currentUserId)
                    .build();

            DynamicListVO result = dynamicService.getDynamicList(queryDTO);

            log.info("获取附近动态成功，location=({},{})", latitude, longitude);
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取附近动态失败", e);
            return Result.error("获取附近动态失败");
        }
    }

    /**
     * 获取用户发布的动态
     */
    @Operation(summary = "获取用户动态", description = "获取指定用户发布的动态列表")
    @GetMapping("/user/{userId}")
    public Result<DynamicListVO> getUserDynamics(
            @Parameter(description = "用户ID", required = true) @PathVariable Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        try {
            Long currentUserId = getCurrentUserId(request);

            DynamicQueryDTO queryDTO = DynamicQueryDTO.builder()
                    .page(page)
                    .size(size)
                    .userId(userId)
                    .currentUserId(currentUserId)
                    .build();

            DynamicListVO result = dynamicService.getDynamicList(queryDTO);

            log.info("获取用户动态成功，userId={}, count={}", userId, result.getList().size());
            return Result.success(result);

        } catch (Exception e) {
            log.error("获取用户动态失败，userId={}", userId, e);
            return Result.error("获取用户动态失败");
        }
    }

    /**
     * 搜索动态
     *
     * 已迁移至 SearchController：GET /api/search/dynamics
     */
    @Deprecated
    @Operation(summary = "搜索动态(已迁移)", description = "请使用 /api/search/dynamics")
    @GetMapping("/search")
    public Result<DynamicListVO> searchDynamics(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer size,
            HttpServletRequest request) {

        // 为兼容旧前端，暂时保留一段时间：内部转调新逻辑
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

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // 这里从JWT Token中解析用户ID
        // 示例实现，实际项目中需要根据你的认证框架调整
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            // 解析token获取userId
            // return jwtUtil.parseUserId(token);
        }
        return null; // 未登录用户返回null
    }
}

