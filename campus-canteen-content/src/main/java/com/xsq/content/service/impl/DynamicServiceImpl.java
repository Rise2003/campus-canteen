package com.xsq.content.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.base.exception.BusinessException;
import com.xsq.content.mapper.DynamicMapper;
import com.xsq.content.model.dto.DynamicDishDTO;
import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.po.Canteen;
import com.xsq.content.model.po.Collection;
import com.xsq.content.model.po.Dish;
import com.xsq.content.model.po.Dynamic;
import com.xsq.content.model.po.DynamicDish;
import com.xsq.content.model.po.Follow;
import com.xsq.content.model.po.LikeRecord;
import com.xsq.content.model.po.User;
import com.xsq.content.model.vo.*;
import com.xsq.content.service.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@Slf4j
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements IDynamicService {

    @Autowired
    private IUserService userService;
    @Autowired
    private ICanteenService canteenService;
    @Autowired
    private IDishService dishService;
    @Autowired
    private ICollectionService collectionService;
    @Autowired
    private IDynamicDishService dynamicDishService;
    @Autowired
    private ILikeRecordService likeRecordService;
    @Autowired
    private IFollowService followService;
    private static final int STATUS_NORMAL = 1;
    private static final int STATUS_DELETED = 3;
    private static final int TARGET_TYPE_DYNAMIC = 1;

    @Override
    public DynamicListVO getDynamicList(DynamicQueryDTO queryDTO) {
        if (queryDTO == null) {
            return DynamicListVO.success(Collections.emptyList(), 0L, 1, 10);
        }

        String sort = StrUtil.blankToDefault(queryDTO.getSort(), "createdAt");
        if ("distance".equalsIgnoreCase(sort)) {
            return getNearbyDynamicList(queryDTO);
        }

        LambdaQueryWrapper<Dynamic> wrapper = buildQueryWrapper(queryDTO);
        Page<Dynamic> page = new Page<>(safePage(queryDTO.getPage()), safeSize(queryDTO.getSize()));
        Page<Dynamic> dynamicPage = this.page(page, wrapper);

        List<DynamicVO> vos = toVOList(dynamicPage.getRecords(), queryDTO.getCurrentUserId());
        return DynamicListVO.success(vos, dynamicPage.getTotal(), safePage(queryDTO.getPage()), safeSize(queryDTO.getSize()));
    }

    @Override
    public DynamicVO getDynamicDetail(Long dynamicId, Long currentUserId) {
        Dynamic dynamic = this.getById(dynamicId);
        if (dynamic == null || !Objects.equals(dynamic.getStatus(), STATUS_NORMAL)) {
            return null;
        }

        incrementViewCount(dynamicId);

        DynamicVO vo = toVO(dynamic, currentUserId);
        vo.setDishRatings(getDishRatingsByDynamic(dynamicId));
        vo.setEditAllowed(checkDynamicPermission(dynamicId, currentUserId));
        vo.setDeleteAllowed(checkDynamicPermission(dynamicId, currentUserId));
        vo.setShareUrl(generateShareUrl(dynamicId));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean incrementViewCount(Long dynamicId) {
        return this.update(new LambdaUpdateWrapper<Dynamic>()
                .eq(Dynamic::getId, dynamicId)
                .setSql("view_count = view_count + 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean likeDynamic(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        if (isLiked(dynamicId, userId)) return true;

        LikeRecord record = new LikeRecord();
        record.setUserId(userId);
        record.setTargetType(TARGET_TYPE_DYNAMIC);
        record.setTargetId(dynamicId);
        record.setStatus(1);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        boolean saved = likeRecordService.save(record);
        if (saved) {
            this.update(new LambdaUpdateWrapper<Dynamic>()
                    .eq(Dynamic::getId, dynamicId)
                    .setSql("like_count = like_count + 1"));
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelLikeDynamic(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        if (!isLiked(dynamicId, userId)) return true;

        boolean updated = likeRecordService.update(new UpdateWrapper<LikeRecord>()
                .eq("user_id", userId)
                .eq("target_type", TARGET_TYPE_DYNAMIC)
                .eq("target_id", dynamicId)
                .set("status", 0)
                .set("updated_at", LocalDateTime.now()));

        if (updated) {
            this.update(new LambdaUpdateWrapper<Dynamic>()
                    .eq(Dynamic::getId, dynamicId)
                    .setSql("like_count = GREATEST(like_count - 1, 0)"));
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean collectDynamic(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        if (isCollected(dynamicId, userId)) return true;

        Collection record = new Collection();
        record.setUserId(userId);
        record.setTargetType(TARGET_TYPE_DYNAMIC);
        record.setTargetId(dynamicId);
        record.setStatus(1);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        boolean saved = collectionService.save(record);
        if (saved) {
            this.update(new LambdaUpdateWrapper<Dynamic>()
                    .eq(Dynamic::getId, dynamicId)
                    .setSql("collect_count = collect_count + 1"));
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelCollectDynamic(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        if (!isCollected(dynamicId, userId)) return true;

        boolean updated = collectionService.update(new UpdateWrapper<Collection>()
                .eq("user_id", userId)
                .eq("target_type", TARGET_TYPE_DYNAMIC)
                .eq("target_id", dynamicId)
                .set("status", 0)
                .set("updated_at", LocalDateTime.now()));

        if (updated) {
            this.update(new LambdaUpdateWrapper<Dynamic>()
                    .eq(Dynamic::getId, dynamicId)
                    .setSql("collect_count = GREATEST(collect_count - 1, 0)"));
        }
        return updated;
    }

    @Override
    public Long countUserDynamics(Long userId) {
        if (userId == null) return 0L;
        return this.count(new LambdaQueryWrapper<Dynamic>()
                .eq(Dynamic::getUserId, userId)
                .eq(Dynamic::getStatus, STATUS_NORMAL));
    }

    @Override
    public boolean checkDynamicPermission(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        Dynamic dynamic = this.getById(dynamicId);
        return dynamic != null && Objects.equals(dynamic.getUserId(), userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long publishDynamic(Dynamic dynamic, List<Long> dishIds, List<DynamicDishDTO> dishRatings) {
        if (dynamic == null) return null;

        if (dynamic.getStatus() == null) {
            dynamic.setStatus(STATUS_NORMAL);
        }
        if (dynamic.getLikeCount() == null) dynamic.setLikeCount(0);
        if (dynamic.getCommentCount() == null) dynamic.setCommentCount(0);
        if (dynamic.getShareCount() == null) dynamic.setShareCount(0);
        if (dynamic.getViewCount() == null) dynamic.setViewCount(0);
        if (dynamic.getCollectCount() == null) dynamic.setCollectCount(0);
        if (dynamic.getCreatedAt() == null) dynamic.setCreatedAt(LocalDateTime.now());
        if (dynamic.getUpdatedAt() == null) dynamic.setUpdatedAt(LocalDateTime.now());

        boolean saved = this.save(dynamic);
        if (!saved) return null;

        Long dynamicId = dynamic.getId();
        if (dynamicId == null) return null;

        List<DynamicDish> relations = new ArrayList<>();

        if (dishRatings != null && !dishRatings.isEmpty()) {
            for (DynamicDishDTO dto : dishRatings) {
                if (dto == null || dto.getDishId() == null) continue;
                DynamicDish dd = new DynamicDish();
                dd.setDynamicId(dynamicId);
                dd.setDishId(dto.getDishId());
                dd.setRating(dto.getRating());
                dd.setComment(dto.getComment());
                dd.setCreatedAt(LocalDateTime.now());
                relations.add(dd);
            }
        } else if (dishIds != null && !dishIds.isEmpty()) {
            for (Long dishId : dishIds) {
                if (dishId == null) continue;
                DynamicDish dd = new DynamicDish();
                dd.setDynamicId(dynamicId);
                dd.setDishId(dishId);
                dd.setCreatedAt(LocalDateTime.now());
                relations.add(dd);
            }
        }

        if (!relations.isEmpty()) {
            dynamicDishService.saveBatch(relations);
        }

        return dynamicId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDynamic(Long dynamicId, Long userId) {
        if (!checkDynamicPermission(dynamicId, userId)) {
            return false;
        }
        return this.update(new LambdaUpdateWrapper<Dynamic>()
                .eq(Dynamic::getId, dynamicId)
                .set(Dynamic::getStatus, STATUS_DELETED)
                .set(Dynamic::getUpdatedAt, LocalDateTime.now()));
    }

    private DynamicListVO getNearbyDynamicList(DynamicQueryDTO queryDTO) {
        if (queryDTO.getLatitude() == null || queryDTO.getLongitude() == null) {
            return DynamicListVO.success(Collections.emptyList(), 0L, safePage(queryDTO.getPage()), safeSize(queryDTO.getSize()));
        }

        double lat = queryDTO.getLatitude();
        double lon = queryDTO.getLongitude();
        int radius = queryDTO.getRadius() == null ? 1000 : Math.max(1, queryDTO.getRadius());

        double deltaLat = radius / 111320.0;
        double deltaLon = radius / (111320.0 * Math.cos(Math.toRadians(lat)));

        BigDecimal minLat = BigDecimal.valueOf(lat - deltaLat);
        BigDecimal maxLat = BigDecimal.valueOf(lat + deltaLat);
        BigDecimal minLon = BigDecimal.valueOf(lon - deltaLon);
        BigDecimal maxLon = BigDecimal.valueOf(lon + deltaLon);

        LambdaQueryWrapper<Dynamic> wrapper = buildQueryWrapper(queryDTO);
        wrapper.isNotNull(Dynamic::getLatitude)
                .isNotNull(Dynamic::getLongitude)
                .between(Dynamic::getLatitude, minLat, maxLat)
                .between(Dynamic::getLongitude, minLon, maxLon);

        List<Dynamic> candidates = this.list(wrapper);
        if (candidates == null || candidates.isEmpty()) {
            return DynamicListVO.success(Collections.emptyList(), 0L, safePage(queryDTO.getPage()), safeSize(queryDTO.getSize()));
        }

        Comparator<Dynamic> cmp = Comparator.comparingDouble(d -> distanceMeters(lat, lon, d.getLatitude(), d.getLongitude()));
        if ("desc".equalsIgnoreCase(queryDTO.getOrder())) {
            cmp = cmp.reversed();
        }
        candidates.sort(cmp);

        int page = safePage(queryDTO.getPage());
        int size = safeSize(queryDTO.getSize());
        int from = Math.max(0, (page - 1) * size);
        int to = Math.min(candidates.size(), from + size);
        if (from >= to) {
            return DynamicListVO.success(Collections.emptyList(), (long) candidates.size(), page, size);
        }

        List<Dynamic> slice = candidates.subList(from, to);
        List<DynamicVO> vos = toVOList(slice, queryDTO.getCurrentUserId());
        return DynamicListVO.success(vos, (long) candidates.size(), page, size);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Dynamic> buildQueryWrapper(DynamicQueryDTO queryDTO) {
        LambdaQueryWrapper<Dynamic> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Dynamic::getStatus, STATUS_NORMAL);

        if (queryDTO.getCanteenId() != null) {
            wrapper.eq(Dynamic::getCanteenId, queryDTO.getCanteenId());
        }
        if (queryDTO.getUserId() != null) {
            wrapper.eq(Dynamic::getUserId, queryDTO.getUserId());
        }
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Dynamic::getTitle, queryDTO.getKeyword())
                    .or().like(Dynamic::getContent, queryDTO.getKeyword()));
        }
        if (queryDTO.getMinRating() != null) {
            wrapper.ge(Dynamic::getTotalRating, queryDTO.getMinRating());
        }
        Boolean recommended = queryDTO.getRecommended();
        if (recommended == null) {
            recommended = queryDTO.getRecommendedOnly();
        }
        if (Boolean.TRUE.equals(recommended)) {
            wrapper.eq(Dynamic::getIsRecommended, 1);
        }

        if (StrUtil.isNotBlank(queryDTO.getTimeRange())) {
            LocalDateTime start = getTimeRangeStart(queryDTO.getTimeRange());
            if (start != null) {
                wrapper.ge(Dynamic::getCreatedAt, start);
            }
        }

        if (Boolean.TRUE.equals(queryDTO.getFollowingOnly()) && queryDTO.getCurrentUserId() != null) {
            List<Long> followingIds = followService.list(new LambdaQueryWrapper<Follow>()
                            .eq(Follow::getFollowerId, queryDTO.getCurrentUserId())
                            .eq(Follow::getStatus, 1))
                    .stream()
                    .map(Follow::getFollowingId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            if (followingIds.isEmpty()) {
                wrapper.eq(Dynamic::getId, -1L);
            } else {
                wrapper.in(Dynamic::getUserId, followingIds);
            }
        }

        applySort(wrapper, queryDTO.getSort(), queryDTO.getOrder());
        return wrapper;
    }

    void applySort(LambdaQueryWrapper<Dynamic> wrapper, String sort, String order) {
        boolean asc = "asc".equalsIgnoreCase(order);
        String s = StrUtil.blankToDefault(sort, "createdAt");

        switch (s) {
            case "created":
            case "createdAt":
                wrapper.orderBy(true, asc, Dynamic::getCreatedAt);
                break;
            case "like":
            case "likeCount":
                wrapper.orderBy(true, asc, Dynamic::getLikeCount).orderByDesc(Dynamic::getCreatedAt);
                break;
            case "comment":
            case "commentCount":
                wrapper.orderBy(true, asc, Dynamic::getCommentCount).orderByDesc(Dynamic::getCreatedAt);
                break;
            case "hot":
                wrapper.orderBy(true, asc, Dynamic::getLikeCount)
                        .orderBy(true, asc, Dynamic::getCommentCount)
                        .orderByDesc(Dynamic::getCreatedAt);
                break;
            case "recommend":
                wrapper.orderByDesc(Dynamic::getIsRecommended).orderByDesc(Dynamic::getCreatedAt);
                break;
            default:
                wrapper.orderByDesc(Dynamic::getCreatedAt);
        }
    }

    private List<DynamicVO> toVOList(List<Dynamic> dynamics, Long currentUserId) {
        if (dynamics == null || dynamics.isEmpty()) return Collections.emptyList();

        Set<Long> userIds = dynamics.stream().map(Dynamic::getUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<Long> canteenIds = dynamics.stream().map(Dynamic::getCanteenId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, User> userMap = userIds.isEmpty()
                ? Collections.emptyMap()
                : userService.listByIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u, (a, b) -> a));

        Map<Long, Canteen> canteenMap = canteenIds.isEmpty()
                ? Collections.emptyMap()
                : canteenService.listByIds(canteenIds).stream().collect(Collectors.toMap(Canteen::getId, c -> c, (a, b) -> a));

        Map<Long, Boolean> likeMap = Collections.emptyMap();
        Map<Long, Boolean> collectMap = Collections.emptyMap();
        Map<Long, Boolean> followingMap = Collections.emptyMap();
        if (currentUserId != null) {
            List<Long> dynamicIds = dynamics.stream().map(Dynamic::getId).filter(Objects::nonNull).collect(Collectors.toList());
            likeMap = batchLiked(dynamicIds, currentUserId);
            collectMap = batchCollected(dynamicIds, currentUserId);
            followingMap = batchFollowing(userIds, currentUserId);
        }

        List<DynamicVO> result = new ArrayList<>(dynamics.size());
        for (Dynamic dynamic : dynamics) {
            DynamicVO vo = toVO(dynamic, userMap.get(dynamic.getUserId()), canteenMap.get(dynamic.getCanteenId()));
            if (vo.getStats() != null) {
                vo.getStats().setIsLiked(likeMap.getOrDefault(dynamic.getId(), false));
                vo.getStats().setIsCollected(collectMap.getOrDefault(dynamic.getId(), false));
            }
            if (vo.getUser() != null) {
                vo.getUser().setIsFollowing(followingMap.getOrDefault(dynamic.getUserId(), false));
            }
            result.add(vo);
        }
        return result;
    }

    private DynamicVO toVO(Dynamic dynamic, Long currentUserId) {
        User user = dynamic.getUserId() == null ? null : userService.getById(dynamic.getUserId());
        Canteen canteen = dynamic.getCanteenId() == null ? null : canteenService.getById(dynamic.getCanteenId());
        DynamicVO vo = toVO(dynamic, user, canteen);

        if (currentUserId != null && vo.getStats() != null) {
            vo.getStats().setIsLiked(isLiked(dynamic.getId(), currentUserId));
            vo.getStats().setIsCollected(isCollected(dynamic.getId(), currentUserId));
        }
        return vo;
    }

    private DynamicVO toVO(Dynamic dynamic, User user, Canteen canteen) {
        if (dynamic == null) return null;

        List<String> images = parseImages(dynamic.getImages());

        DynamicVO.RatingVO rating = DynamicVO.RatingVO.builder()
                .total(dynamic.getTotalRating())
                .taste(dynamic.getTasteRating())
                .environment(dynamic.getEnvironmentRating())
                .service(dynamic.getServiceRating())
                .price(dynamic.getPriceRating())
                .build();

        DynamicVO.StatsVO stats = DynamicVO.StatsVO.builder()
                .likeCount(nvl(dynamic.getLikeCount()))
                .commentCount(nvl(dynamic.getCommentCount()))
                .shareCount(nvl(dynamic.getShareCount()))
                .viewCount(nvl(dynamic.getViewCount()))
                .collectCount(nvl(dynamic.getCollectCount()))
                .isLiked(false)
                .isCollected(false)
                .build();

        UserSimpleVO userVO = user == null ? null : UserSimpleVO.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();

        CanteenSimpleVO canteenVO = canteen == null ? null : CanteenSimpleVO.builder()
                .id(canteen.getId())
                .name(canteen.getName())
                .coverImage(canteen.getCoverImage())
                .build();

        return DynamicVO.builder()
                .id(dynamic.getId())
                .title(dynamic.getTitle())
                .content(dynamic.getContent())
                .images(images)
                .user(userVO)
                .canteen(canteenVO)
                .ratings(rating)
                .stats(stats)
                .locationName(dynamic.getLocationName())
                .latitude(dynamic.getLatitude())
                .longitude(dynamic.getLongitude())
                .createdAt(dynamic.getCreatedAt())
                .timeAgo(calculateTimeAgo(dynamic.getCreatedAt()))
                .readTime(calculateReadTime(dynamic.getContent()))
                .build();
    }

    private List<DishRatingVO> getDishRatingsByDynamic(Long dynamicId) {
        if (dynamicId == null) return Collections.emptyList();
        List<DynamicDish> relations = dynamicDishService.list(new LambdaQueryWrapper<DynamicDish>()
                .eq(DynamicDish::getDynamicId, dynamicId));
        if (relations == null || relations.isEmpty()) return Collections.emptyList();

        List<Long> dishIds = relations.stream().map(DynamicDish::getDishId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        Map<Long, Dish> dishMap = dishIds.isEmpty()
                ? Collections.emptyMap()
                : dishService.listByIds(dishIds).stream().collect(Collectors.toMap(Dish::getId, d -> d, (a, b) -> a));

        List<DishRatingVO> result = new ArrayList<>();
        for (DynamicDish rel : relations) {
            Dish dish = dishMap.get(rel.getDishId());
            if (dish == null) continue;
            result.add(DishRatingVO.builder()
                    .dishId(dish.getId())
                    .dishName(dish.getName())
                    .coverImage(dish.getCoverImage())
                    .price(dish.getPrice())
                    .rating(rel.getRating())
                    .comment(rel.getComment())
                    .avgRating(dish.getTotalRating())
                    .reviewCount(dish.getReviewCount())
                    .tastes(Collections.emptyList())
                    .build());
        }
        return result;
    }

    private Map<Long, Boolean> batchLiked(List<Long> dynamicIds, Long userId) {
        if (dynamicIds == null || dynamicIds.isEmpty() || userId == null) return Collections.emptyMap();
        List<LikeRecord> likes = likeRecordService.list(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, TARGET_TYPE_DYNAMIC)
                .eq(LikeRecord::getStatus, 1)
                .in(LikeRecord::getTargetId, dynamicIds));
        Map<Long, Boolean> map = new HashMap<>();
        for (Long id : dynamicIds) {
            map.put(id, false);
        }
        for (LikeRecord lr : likes) {
            map.put(lr.getTargetId(), true);
        }
        return map;
    }

    private Map<Long, Boolean> batchCollected(List<Long> dynamicIds, Long userId) {
        if (dynamicIds == null || dynamicIds.isEmpty() || userId == null) return Collections.emptyMap();
        List<Collection> collects = collectionService.list(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getTargetType, TARGET_TYPE_DYNAMIC)
                .eq(Collection::getStatus, 1)
                .in(Collection::getTargetId, dynamicIds));
        Map<Long, Boolean> map = new HashMap<>();
        for (Long id : dynamicIds) {
            map.put(id, false);
        }
        for (Collection c : collects) {
            map.put(c.getTargetId(), true);
        }
        return map;
    }

    private Map<Long, Boolean> batchFollowing(Set<Long> authorIds, Long currentUserId) {
        if (authorIds == null || authorIds.isEmpty() || currentUserId == null) return Collections.emptyMap();

        List<Follow> follows = followService.list(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, currentUserId)
                .eq(Follow::getStatus, 1)
                .in(Follow::getFollowingId, authorIds));

        Map<Long, Boolean> map = new HashMap<>();
        for (Long id : authorIds) {
            map.put(id, false);
        }
        for (Follow f : follows) {
            if (f.getFollowingId() != null) {
                map.put(f.getFollowingId(), true);
            }
        }
        return map;
    }

    private boolean isLiked(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        return likeRecordService.count(new LambdaQueryWrapper<LikeRecord>()
                .eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetType, TARGET_TYPE_DYNAMIC)
                .eq(LikeRecord::getTargetId, dynamicId)
                .eq(LikeRecord::getStatus, 1)) > 0;
    }

    private boolean isCollected(Long dynamicId, Long userId) {
        if (dynamicId == null || userId == null) return false;
        return collectionService.count(new LambdaQueryWrapper<Collection>()
                .eq(Collection::getUserId, userId)
                .eq(Collection::getTargetType, TARGET_TYPE_DYNAMIC)
                .eq(Collection::getTargetId, dynamicId)
                .eq(Collection::getStatus, 1)) > 0;
    }

    private List<String> parseImages(String imagesJson) {
        if (StrUtil.isBlank(imagesJson)) return Collections.emptyList();
        try {
            return JSONUtil.toList(JSONUtil.parseArray(imagesJson), String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String calculateTimeAgo(LocalDateTime createTime) {
        if (createTime == null) return "";
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(createTime, now);
        long hours = ChronoUnit.HOURS.between(createTime, now);
        long days = ChronoUnit.DAYS.between(createTime, now);
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        if (hours < 24) return hours + "小时前";
        if (days < 30) return days + "天前";
        return createTime.toLocalDate().toString();
    }

    private Integer calculateReadTime(String content) {
        if (StrUtil.isBlank(content)) return 1;
        int wordCount = content.length();
        return Math.max(1, wordCount / 200);
    }

    private LocalDateTime getTimeRangeStart(String timeRange) {
        if (StrUtil.isBlank(timeRange)) return null;
        LocalDateTime now = LocalDateTime.now();
        switch (timeRange.toLowerCase()) {
            case "today":
                return now.withHour(0).withMinute(0).withSecond(0).withNano(0);
            case "week":
                return now.minusDays(7);
            case "month":
                return now.minusDays(30);
            default:
                return null;
        }
    }

    private String generateShareUrl(Long dynamicId) {
        return "https://campus-canteen.com/dynamic/" + dynamicId;
    }

    private int safePage(Integer page) {
        return page == null || page < 1 ? 1 : page;
    }

    private int safeSize(Integer size) {
        if (size == null) return 10;
        if (size < 1) return 1;
        return Math.min(size, 50);
    }

    private Integer nvl(Integer v) {
        return v == null ? 0 : v;
    }

    private double distanceMeters(double lat1, double lon1, BigDecimal lat2, BigDecimal lon2) {
        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;
        return distanceMeters(lat1, lon1, lat2.doubleValue(), lon2.doubleValue());
    }

    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {
        double r = 6371000.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return r * c;
    }


}
