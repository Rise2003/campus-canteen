package com.xsq.base.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import com.xsq.content.model.vo.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class EsUtil {
    private volatile RestHighLevelClient client;

    /**
     * 懒加载 client：避免 Spring 容器启动时就触发 ES 客户端静态初始化。
     *
     * 说明：当前项目的 ES 依赖可能存在版本冲突，RestHighLevelClient 在类初始化阶段
     * 可能抛出 NoSuchFieldError（例如 IGNORE_DEPRECATIONS），导致 ApplicationContext 直接启动失败。
     */
    public RestHighLevelClient getClient() {
        if (client != null) {
            return client;
        }
        synchronized (this) {
            if (client != null) {
                return client;
            }
            try {
                client = new RestHighLevelClient(
                        RestClient.builder(
                                HttpHost.create("http://192.168.19.130:9200")
                        ));
                return client;
            } catch (NoSuchFieldError e) {
                throw new IllegalStateException("Failed to initialize RestHighLevelClient. " +
                        "This usually means Elasticsearch client dependency version conflict.", e);
            }
        }
    }

    public void close() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    private static final String INDEX_USER = "cc_user_v1";
    private static final String INDEX_DISH = "cc_dish_v1";
    private static final String INDEX_DYNAMIC = "cc_dynamic_v1";

    /**
     * 统一 ES 搜索：一次请求返回用户/菜品/动态，方便前端分栏展示。
     */
    public SearchAllVO searchAll(String keyword, int page, int size) throws IOException {
        if (StringUtils.isBlank(keyword)) {
            return SearchAllVO.builder()
                    .keyword(keyword)
                    .dynamics(DynamicListVO.success(Collections.emptyList(), 0L, page, size))
                    .dishes(Collections.emptyList())
                    .dishTotal(0L)
                    .users(Collections.emptyList())
                    .userTotal(0L)
                    .build();
        }

        SearchAllVO.SearchAllVOBuilder builder = SearchAllVO.builder().keyword(keyword);

        // dishes
        SearchResult<DishSimpleVO> dishRes = searchDishes(keyword, page, size);
        builder.dishes(dishRes.list).dishTotal(dishRes.total);

        // users
        SearchResult<UserSimpleVO> userRes = searchUsers(keyword, page, size);
        builder.users(userRes.list).userTotal(userRes.total);

        // dynamics
        SearchResult<DynamicVO> dynamicRes = searchDynamics(keyword, page, size);
        builder.dynamics(DynamicListVO.success(dynamicRes.list, dynamicRes.total, page, size));

        return builder.build();
    }

    public SearchResult<DishSimpleVO> searchDishes(String keyword, int page, int size) throws IOException {
        BoolQueryBuilder q = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.matchQuery("description", keyword))
                .should(QueryBuilders.matchPhraseQuery("name", keyword))
                .should(QueryBuilders.wildcardQuery("name.keyword", "*" + keyword + "*"))
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(q)
                .from(Math.max(0, (page - 1) * size))
                .size(size)
                .fetchSource(new String[]{"id", "canteenId", "name", "coverImage", "price", "totalRating"}, null);

        SearchResponse resp = getClient().search(new SearchRequest(INDEX_DISH).source(source), RequestOptions.DEFAULT);
        long total = resp.getHits().getTotalHits() == null ? 0L : resp.getHits().getTotalHits().value;

        List<DishSimpleVO> list = new ArrayList<>();
        for (SearchHit hit : resp.getHits().getHits()) {
            Map<String, Object> m = hit.getSourceAsMap();
            DishSimpleVO vo = DishSimpleVO.builder()
                    .id(toLong(m.get("id"), hit.getId()))
                    .canteenId(toLong(m.get("canteenId"), null))
                    .name((String) m.get("name"))
                    .coverImage((String) m.get("coverImage"))
                    .price(toBigDecimal(m.get("price")))
                    .totalRating(toBigDecimal(m.get("totalRating")))
                    .build();
            list.add(vo);
        }
        return new SearchResult<>(list, total);
    }

    public SearchResult<UserSimpleVO> searchUsers(String keyword, int page, int size) throws IOException {
        BoolQueryBuilder q = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("nickname", keyword))
                .should(QueryBuilders.wildcardQuery("nickname.keyword", "*" + keyword + "*"))
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(q)
                .from(Math.max(0, (page - 1) * size))
                .size(size)
                .fetchSource(new String[]{"id", "nickname", "avatarUrl"}, null);

        SearchResponse resp = getClient().search(new SearchRequest(INDEX_USER).source(source), RequestOptions.DEFAULT);
        long total = resp.getHits().getTotalHits() == null ? 0L : resp.getHits().getTotalHits().value;

        List<UserSimpleVO> list = new ArrayList<>();
        for (SearchHit hit : resp.getHits().getHits()) {
            Map<String, Object> m = hit.getSourceAsMap();
            UserSimpleVO vo = UserSimpleVO.builder()
                    .id(toLong(m.get("id"), hit.getId()))
                    .nickname((String) m.get("nickname"))
                    .avatarUrl((String) m.get("avatarUrl"))
                    .build();
            list.add(vo);
        }
        return new SearchResult<>(list, total);
    }

    public SearchResult<DynamicVO> searchDynamics(String keyword, int page, int size) throws IOException {
        BoolQueryBuilder q = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", keyword))
                .should(QueryBuilders.matchQuery("content", keyword))
                .should(QueryBuilders.matchPhraseQuery("title", keyword))
                .should(QueryBuilders.wildcardQuery("title.keyword", "*" + keyword + "*"))
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(q)
                .from(Math.max(0, (page - 1) * size))
                .size(size)
                .fetchSource(new String[]{"id", "title", "content", "locationName", "latitude", "longitude", "likeCount", "commentCount", "shareCount", "viewCount", "collectCount", "totalRating", "createdAt"}, null);

        SearchResponse resp = getClient().search(new SearchRequest(INDEX_DYNAMIC).source(source), RequestOptions.DEFAULT);
        long total = resp.getHits().getTotalHits() == null ? 0L : resp.getHits().getTotalHits().value;

        List<DynamicVO> list = new ArrayList<>();
        for (SearchHit hit : resp.getHits().getHits()) {
            Map<String, Object> m = hit.getSourceAsMap();

            DynamicVO.StatsVO stats = DynamicVO.StatsVO.builder()
                    .likeCount(toInt(m.get("likeCount")))
                    .commentCount(toInt(m.get("commentCount")))
                    .shareCount(toInt(m.get("shareCount")))
                    .viewCount(toInt(m.get("viewCount")))
                    .collectCount(toInt(m.get("collectCount")))
                    .build();

            DynamicVO.RatingVO ratings = DynamicVO.RatingVO.builder()
                    .total(toBigDecimal(m.get("totalRating")))
                    .build();

            DynamicVO vo = DynamicVO.builder()
                    .id(toLong(m.get("id"), hit.getId()))
                    .title((String) m.get("title"))
                    .content((String) m.get("content"))
                    .locationName((String) m.get("locationName"))
                    .latitude(toBigDecimal(m.get("latitude")))
                    .longitude(toBigDecimal(m.get("longitude")))
                    .createdAt(toLocalDateTime(m.get("createdAt")))
                    .stats(stats)
                    .ratings(ratings)
                    .build();

            list.add(vo);
        }
        return new SearchResult<>(list, total);
    }

    public static class SearchResult<T> {
        public final List<T> list;
        public final long total;

        private SearchResult(List<T> list, long total) {
            this.list = list;
            this.total = total;
        }
    }

    private Long toLong(Object v, String fallbackId) {
        if (v == null) {
            try {
                return fallbackId == null ? null : Long.parseLong(fallbackId);
            } catch (Exception e) {
                return null;
            }
        }
        if (v instanceof Number) return ((Number) v).longValue();
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number) return ((Number) v).intValue();
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal toBigDecimal(Object v) {
        if (v == null) return null;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        if (v instanceof Number) return BigDecimal.valueOf(((Number) v).doubleValue());
        try {
            return new BigDecimal(String.valueOf(v));
        } catch (Exception e) {
            return null;
        }
    }

    private java.time.LocalDateTime toLocalDateTime(Object v) {
        if (v == null) return null;
        // ES date 可能是字符串（ISO-8601）或 epoch millis
        try {
            if (v instanceof Number) {
                long ms = ((Number) v).longValue();
                return java.time.Instant.ofEpochMilli(ms).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
            }
            String s = String.valueOf(v);
            // 兼容带时区/不带时区
            try {
                return java.time.OffsetDateTime.parse(s).toLocalDateTime();
            } catch (Exception ignore) {
                return java.time.LocalDateTime.parse(s);
            }
        } catch (Exception e) {
            return null;
        }
    }
}
