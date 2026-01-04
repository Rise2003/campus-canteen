package com.xsq.base.utils;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.xsq.content.mapper.DishMapper;
import com.xsq.content.mapper.DynamicMapper;
import com.xsq.content.mapper.UserMapper;
import com.xsq.content.model.po.Dish;
import com.xsq.content.model.po.Dynamic;
import com.xsq.content.model.po.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ES 批量导入测试（不启动 SpringBootTest，避免 ES client Bean 版本冲突导致容器启动失败）：
 * - 用户 -> cc_user_v1（nickname 搜索）
 * - 菜品 -> cc_dish_v1（name/description 搜索）
 * - 动态 -> cc_dynamic_v1（title/content 搜索）
 */
@Slf4j
public class ElasticDocumentTest {

    private static final String ES_HOST = "http://192.168.19.130:9200";

    private static final String INDEX_USER = "cc_user_v1";
    private static final String INDEX_DISH = "cc_dish_v1";
    private static final String INDEX_DYNAMIC = "cc_dynamic_v1";

    private RestHighLevelClient client;
    private SqlSessionFactory sqlSessionFactory;

    @BeforeEach
    void setUp() throws Exception {
        client = new RestHighLevelClient(RestClient.builder(HttpHost.create(ES_HOST)));
        sqlSessionFactory = buildSqlSessionFactoryFromApplicationYaml();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (client != null) {
            client.close();
        }
    }

    /**
     * 一键批量导入三类数据
     */
    @Test
    void bulkAddAll() throws IOException {
        System.out.println("[bulkAddAll] start");
        bulkAddUsers();
        bulkAddDishes();
        bulkAddDynamics();
        System.out.println("[bulkAddAll] done");
    }

    @Test
    void bulkAddUsers() throws IOException {
        System.out.println("[bulkAddUsers] start");
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            bulkIndexByPage(
                    INDEX_USER,
                    500,
                    pageNo -> {
                        Page<User> page = Page.of(pageNo, 500);
                        return mapper.selectPage(page, new LambdaQueryWrapper<User>()
                                .eq(User::getStatus, 1));
                    },
                    user -> {
                        Map<String, Object> doc = toUserEsDoc(user);
                        return new IndexRequest(INDEX_USER)
                                .id(String.valueOf(user.getId()))
                                .source(doc);
                    }
            );
        }
        printIndexStats(INDEX_USER);
        System.out.println("[bulkAddUsers] done");
    }

    @Test
    void bulkAddDishes() throws IOException {
        System.out.println("[bulkAddDishes] start");
        try (SqlSession session = sqlSessionFactory.openSession()) {
            DishMapper mapper = session.getMapper(DishMapper.class);

            // 诊断：确认数据库里确实有包含关键字的数据
            long chickenCount = mapper.selectCount(new LambdaQueryWrapper<Dish>()
                    .like(Dish::getName, "鸡")
                    .eq(Dish::getStatus, 1));
            System.out.println("[bulkAddDishes] db count(name like '鸡' AND status=1) = " + chickenCount);

            bulkIndexByPage(
                    INDEX_DISH,
                    500,
                    pageNo -> {
                        Page<Dish> page = Page.of(pageNo, 500);
                        return mapper.selectPage(page, new LambdaQueryWrapper<Dish>()
                                .eq(Dish::getStatus, 1));
                    },
                    dish -> {
                        Map<String, Object> doc = toDishEsDoc(dish);
                        return new IndexRequest(INDEX_DISH)
                                .id(String.valueOf(dish.getId()))
                                // 关键：用 Map 直接作为 source，避免 JSONUtil 把字段名改成下划线风格
                                .source(doc);
                    }
            );
        }

        // 导入后打印索引文档数
        printIndexStats(INDEX_DISH);
        System.out.println("[bulkAddDishes] done");
    }

    @Test
    void bulkAddDynamics() throws IOException {
        System.out.println("[bulkAddDynamics] start");
        try (SqlSession session = sqlSessionFactory.openSession()) {
            DynamicMapper mapper = session.getMapper(DynamicMapper.class);
            bulkIndexByPage(
                    INDEX_DYNAMIC,
                    500,
                    pageNo -> {
                        Page<Dynamic> page = Page.of(pageNo, 500);
                        return mapper.selectPage(page, new LambdaQueryWrapper<Dynamic>()
                                .eq(Dynamic::getStatus, 1));
                    },
                    dynamic -> {
                        Map<String, Object> doc = toDynamicEsDoc(dynamic);
                        return new IndexRequest(INDEX_DYNAMIC)
                                .id(String.valueOf(dynamic.getId()))
                                .source(doc);
                    }
            );
        }
        printIndexStats(INDEX_DYNAMIC);
        System.out.println("[bulkAddDynamics] done");
    }

    /**
     * 可选：清空三类索引数据（按数据库 id 删除）。
     * 注意：这是删除 ES 文档，不会删除索引本身。
     */
    @Test
    void deleteAllDocs() throws IOException {
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper userMapper = session.getMapper(UserMapper.class);
            DishMapper dishMapper = session.getMapper(DishMapper.class);
            DynamicMapper dynamicMapper = session.getMapper(DynamicMapper.class);

            deleteDocsByDbIds(INDEX_USER, selectAllUserIds(userMapper));
            deleteDocsByDbIds(INDEX_DISH, selectAllDishIds(dishMapper));
            deleteDocsByDbIds(INDEX_DYNAMIC, selectAllDynamicIds(dynamicMapper));
        }
    }

    private List<Long> selectAllUserIds(UserMapper mapper) {
        List<User> list = mapper.selectList(new LambdaQueryWrapper<User>().select(User::getId));
        List<Long> ids = new ArrayList<>(list == null ? 0 : list.size());
        if (list != null) {
            for (User u : list) {
                if (u != null && u.getId() != null) ids.add(u.getId());
            }
        }
        return ids;
    }

    private List<Long> selectAllDishIds(DishMapper mapper) {
        List<Dish> list = mapper.selectList(new LambdaQueryWrapper<Dish>().select(Dish::getId));
        List<Long> ids = new ArrayList<>(list == null ? 0 : list.size());
        if (list != null) {
            for (Dish d : list) {
                if (d != null && d.getId() != null) ids.add(d.getId());
            }
        }
        return ids;
    }

    private List<Long> selectAllDynamicIds(DynamicMapper mapper) {
        List<Dynamic> list = mapper.selectList(new LambdaQueryWrapper<Dynamic>().select(Dynamic::getId));
        List<Long> ids = new ArrayList<>(list == null ? 0 : list.size());
        if (list != null) {
            for (Dynamic d : list) {
                if (d != null && d.getId() != null) ids.add(d.getId());
            }
        }
        return ids;
    }

    private void deleteDocsByDbIds(String index, List<Long> ids) throws IOException {
        if (ids == null || ids.isEmpty()) {
            log.info("Skip delete, index={} (empty ids)", index);
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (Long id : ids) {
            if (id == null) continue;
            bulkRequest.add(new DeleteRequest(index).id(String.valueOf(id)));
        }
        BulkResponse resp = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        if (resp.hasFailures()) {
            log.warn("Bulk delete has failures for index={}, msg={}", index, resp.buildFailureMessage());
        } else {
            log.info("Bulk delete OK, index={}, count={}", index, ids.size());
        }
    }

    /**
     * 读取 src/main/resources/application.yaml 的 datasource 配置来创建 SqlSessionFactory。
     *
     * 这里直接按项目中写死的配置初始化，避免启动 Spring 容器。
     */
    private SqlSessionFactory buildSqlSessionFactoryFromApplicationYaml() throws Exception {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost/campus_canteen_db?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true");
        ds.setUsername("root");
        ds.setPassword("123456");

        MybatisSqlSessionFactoryBean factoryBean = new MybatisSqlSessionFactoryBean();
        factoryBean.setDataSource(ds);

        // MyBatis-Plus 推荐配置类
        MybatisConfiguration configuration = new MybatisConfiguration();
        // 关键：注册 Mapper 接口（否则 getMapper 会报 Type ... is not known）
        configuration.addMappers("com.xsq.content.mapper");

        // 关键：启用分页拦截器，否则 selectPage 不会自动分页且 Page#getTotal 常为 0
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        configuration.addInterceptor(interceptor);

        factoryBean.setConfiguration(configuration);

        // 如果有 mapper.xml，设置位置（没有也没关系）
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:/mapper/**/*.xml"));

        return factoryBean.getObject();
    }

    /**
     * 通用分页批量写入。
     */
    private <T> void bulkIndexByPage(
            String index,
            int pageSize,
            PageFetcher<T> fetcher,
            IndexRequestMapper<T> mapper
    ) throws IOException {
        int pageNo = 1;
        while (true) {
            Page<T> page = fetcher.fetch(pageNo);
            List<T> records = page.getRecords();
            if (records == null || records.isEmpty()) {
                System.out.println("[bulkIndexByPage] index=" + index + ", pageNo=" + pageNo + " records=0 => stop");
                break;
            }

            System.out.println("[bulkIndexByPage] index=" + index + ", pageNo=" + pageNo + ", records=" + records.size() + ", total=" + page.getTotal());

            BulkRequest bulkRequest = new BulkRequest();
            for (T r : records) {
                bulkRequest.add(mapper.map(r));
            }

            BulkResponse resp = client.bulk(bulkRequest, RequestOptions.DEFAULT);

            // 强制刷新索引，保证后续立刻可查到
            client.indices().refresh(new RefreshRequest(index), RequestOptions.DEFAULT);

            if (resp.hasFailures()) {
                System.out.println("[bulkIndexByPage] index=" + index + ", pageNo=" + pageNo + " FAIL => " + resp.buildFailureMessage());
                log.warn("Bulk index has failures for index={}, pageNo={}, msg={}", index, pageNo, resp.buildFailureMessage());
            } else {
                int itemCount = resp.getItems() == null ? 0 : resp.getItems().length;
                System.out.println("[bulkIndexByPage] index=" + index + ", pageNo=" + pageNo + " OK, items=" + itemCount);
                log.info("Bulk index OK, index={}, pageNo={}, size={}", index, pageNo, records.size());
            }

            // 注意：有些情况下 total 可能为 0（例如分页拦截器未生效/统计失败）。
            // 为了更稳健，这里不再依赖 total 提前退出，而是以 records 为空作为终止条件。
            pageNo++;
        }
    }

    /**
     * 冒烟测试：搜索关键词“番茄”，分别查询 用户/菜品/动态 三个索引。
     *
     * 说明：
     * - 用户：nickname 包含“番茄”（如 小番茄）
     * - 菜品：name/description 命中（如 番茄炒蛋）
     * - 动态：title/content 命中（如 这家的番茄炒鸡蛋非常好吃）
     */
    @Test
    void searchChicken() throws IOException {
        String keyword = "鸡";
        System.out.println("==== keyword=" + keyword + " ====");

        printIndexStats(INDEX_USER);
        printIndexStats(INDEX_DISH);
        printIndexStats(INDEX_DYNAMIC);

        System.out.println("\n[Users] index=" + INDEX_USER + " (sample docs)");
        printUserSamples(20);

        System.out.println("\n[Dishes] index=" + INDEX_DISH + " (sample docs)");
        printDishSamples(20);

        System.out.println("\n[Dynamics] index=" + INDEX_DYNAMIC + " (sample docs)");
        printDynamicSamples(20);

        System.out.println("\n[Users] index=" + INDEX_USER + " (match)");
        searchAndPrintUsers(keyword, 10);

        System.out.println("\n[Dishes] index=" + INDEX_DISH + " (match)");
        searchAndPrintDishes(keyword, 10);

        System.out.println("\n[Dynamics] index=" + INDEX_DYNAMIC + " (match)");
        searchAndPrintDynamics(keyword, 10);

        System.out.println("\nTips: user/dynamic 如果总是 (no hits)，请先跑 bulkAddUsers / bulkAddDynamics，并确认索引 totalDocs > 0。\n");
    }

    private void printUserSamples(int size) throws IOException {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .fetchSource(new String[]{"id", "nickname", "status"}, null)
                .sort("id", SortOrder.ASC)
                .size(size);
        SearchResponse resp = client.search(new SearchRequest(INDEX_USER).source(source), RequestOptions.DEFAULT);
        SearchHit[] hits = resp.getHits().getHits();
        if (hits == null || hits.length == 0) {
            System.out.println("(no docs)");
            return;
        }
        for (SearchHit hit : hits) {
            Map<String, Object> m = hit.getSourceAsMap();
            System.out.println("- id=" + hit.getId() + ", nickname=" + m.get("nickname") + ", status=" + m.get("status"));
        }
    }

    private void printDynamicSamples(int size) throws IOException {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .fetchSource(new String[]{"id", "title", "content", "status"}, null)
                .sort("id", SortOrder.ASC)
                .size(size);
        SearchResponse resp = client.search(new SearchRequest(INDEX_DYNAMIC).source(source), RequestOptions.DEFAULT);
        SearchHit[] hits = resp.getHits().getHits();
        if (hits == null || hits.length == 0) {
            System.out.println("(no docs)");
            return;
        }
        for (SearchHit hit : hits) {
            Map<String, Object> m = hit.getSourceAsMap();
            String title = m.get("title") == null ? null : String.valueOf(m.get("title"));
            if (title != null && title.length() > 30) title = title.substring(0, 30) + "...";
            System.out.println("- id=" + hit.getId() + ", title=" + title);
        }
    }

    private void printDishSamples(int size) throws IOException {
        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .fetchSource(new String[]{"id", "name", "description"}, null)
                .sort("id", SortOrder.ASC)
                .size(size);

        SearchResponse resp = client.search(new SearchRequest(INDEX_DISH).source(source), RequestOptions.DEFAULT);
        SearchHit[] hits = resp.getHits().getHits();
        if (hits == null || hits.length == 0) {
            System.out.println("(no docs)");
            return;
        }
        for (SearchHit hit : hits) {
            Map<String, Object> m = hit.getSourceAsMap();
            System.out.println("- id=" + hit.getId() + ", name=" + m.get("name"));
        }
    }

    /**
     * cc_user_v1 strict mapping (camelCase):
     * id, nickname, avatarUrl, status, createdAt, updatedAt
     */
    private Map<String, Object> toUserEsDoc(User user) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        if (user == null) return m;
        m.put("id", user.getId());
        m.put("nickname", user.getNickname());
        m.put("avatarUrl", user.getAvatarUrl());
        m.put("status", user.getStatus());
        m.put("createdAt", user.getCreatedAt());
        m.put("updatedAt", user.getUpdatedAt());
        m.values().removeIf(java.util.Objects::isNull);
        return m;
    }

    /**
     * cc_dynamic_v1 strict mapping (camelCase):
     * id, userId, canteenId, title, content, images, totalRating, likeCount, commentCount,
     * viewCount, isRecommended, status, latitude, longitude, locationName, createdAt, updatedAt
     */
    private Map<String, Object> toDynamicEsDoc(Dynamic d) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        if (d == null) return m;
        m.put("id", d.getId());
        m.put("userId", d.getUserId());
        m.put("canteenId", d.getCanteenId());
        m.put("title", d.getTitle());
        m.put("content", d.getContent());
        m.put("images", d.getImages());
        m.put("totalRating", d.getTotalRating());
        m.put("likeCount", d.getLikeCount());
        m.put("commentCount", d.getCommentCount());
        m.put("viewCount", d.getViewCount());
        m.put("isRecommended", d.getIsRecommended());
        m.put("status", d.getStatus());
        m.put("latitude", d.getLatitude());
        m.put("longitude", d.getLongitude());
        m.put("locationName", d.getLocationName());
        m.put("createdAt", d.getCreatedAt());
        m.put("updatedAt", d.getUpdatedAt());
        m.values().removeIf(java.util.Objects::isNull);
        return m;
    }

    /**
     * cc_dish_v1 strict mapping (camelCase):
     * id, canteenId, stallId, name, description, coverImage, price,
     * totalRating, status, sortOrder, createdAt, updatedAt
     */
    private Map<String, Object> toDishEsDoc(Dish dish) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        if (dish == null) return m;

        m.put("id", dish.getId());
        m.put("canteenId", dish.getCanteenId());
        m.put("stallId", dish.getStallId());
        m.put("name", dish.getName());
        m.put("description", dish.getDescription());
        m.put("coverImage", dish.getCoverImage());
        m.put("price", dish.getPrice());
        m.put("totalRating", dish.getTotalRating());
        m.put("status", dish.getStatus());
        m.put("sortOrder", dish.getSortOrder());
        m.put("createdAt", dish.getCreatedAt());
        m.put("updatedAt", dish.getUpdatedAt());

        m.values().removeIf(java.util.Objects::isNull);
        return m;
    }

    /**
     * 打印索引状态：是否存在 + 当前文档总数。
     * 用于验证 bulkAddXxx 是否真的把数据写进 ES。
     */
    private void printIndexStats(String index) throws IOException {
        if (!indexExists(index)) {
            System.out.println("[IndexCheck] index=" + index + " NOT EXISTS");
            return;
        }

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(QueryBuilders.matchAllQuery())
                .size(0);

        SearchRequest req = new SearchRequest(index).source(source);
        SearchResponse resp = client.search(req, RequestOptions.DEFAULT);

        long total = resp.getHits() == null || resp.getHits().getTotalHits() == null
                ? -1L
                : resp.getHits().getTotalHits().value;
        System.out.println("[IndexCheck] index=" + index + " exists, totalDocs=" + total);
    }

    private boolean indexExists(String index) throws IOException {
        return client.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
    }

    private void searchAndPrintUsers(String keyword, int size) throws IOException {
        BoolQueryBuilder q = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("nickname", keyword))
                .should(QueryBuilders.wildcardQuery("nickname.keyword", "*" + keyword + "*"))
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder().query(q).size(size);
        SearchResponse resp = client.search(new SearchRequest(INDEX_USER).source(source), RequestOptions.DEFAULT);

        SearchHit[] hits = resp.getHits().getHits();
        if (hits == null || hits.length == 0) {
            System.out.println("(no hits)");
            return;
        }

        for (SearchHit hit : hits) {
            Map<String, Object> m = hit.getSourceAsMap();
            System.out.println("- id=" + hit.getId() + ", score=" + hit.getScore()
                    + ", nickname=" + m.get("nickname")
                    + ", avatarUrl=" + m.get("avatarUrl"));
        }
    }

    private void searchAndPrintDishes(String keyword, int size) throws IOException {
        // 说明：
        // - match 依赖分词，某些 analyzer 对“鸡”这种单字可能不产生 token，从而命中不足
        // - wildcard/name.keyword 能保证包含匹配（代价是性能较差，仅用于调试/小数据量）
        // - match_phrase 对短语更友好
        BoolQueryBuilder q = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("name", keyword))
                .should(QueryBuilders.matchQuery("description", keyword))
                .should(QueryBuilders.matchPhraseQuery("name", keyword))
                .should(QueryBuilders.wildcardQuery("name.keyword", "*" + keyword + "*"))
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder()
                .query(q)
                .size(size);

        SearchRequest req = new SearchRequest(INDEX_DISH).source(source);
        SearchResponse resp = client.search(req, RequestOptions.DEFAULT);

        SearchHit[] hits = resp.getHits().getHits();
        if (hits == null || hits.length == 0) {
            System.out.println("(no hits)");
            return;
        }

        for (SearchHit hit : hits) {
            Map<String, Object> m = hit.getSourceAsMap();
            Object name = m.get("name");
            Object desc = m.get("description");
            String descShort = desc == null ? null : String.valueOf(desc);
            if (descShort != null && descShort.length() > 40) {
                descShort = descShort.substring(0, 40) + "...";
            }
            System.out.println("- id=" + hit.getId()
                    + ", score=" + hit.getScore()
                    + ", name=" + name
                    + ", description=" + descShort);
        }
    }

    private void searchAndPrintDynamics(String keyword, int size) throws IOException {
        BoolQueryBuilder q = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("title", keyword))
                .should(QueryBuilders.matchQuery("content", keyword))
                .should(QueryBuilders.matchPhraseQuery("title", keyword))
                .should(QueryBuilders.wildcardQuery("title.keyword", "*" + keyword + "*"))
                .minimumShouldMatch(1);

        SearchSourceBuilder source = new SearchSourceBuilder().query(q).size(size);
        SearchResponse resp = client.search(new SearchRequest(INDEX_DYNAMIC).source(source), RequestOptions.DEFAULT);

        SearchHit[] hits = resp.getHits().getHits();
        if (hits == null || hits.length == 0) {
            System.out.println("(no hits)");
            return;
        }

        for (SearchHit hit : hits) {
            Map<String, Object> m = hit.getSourceAsMap();
            Object title = m.get("title");
            Object content = m.get("content");
            String contentShort = content == null ? null : String.valueOf(content);
            if (contentShort != null && contentShort.length() > 60) {
                contentShort = contentShort.substring(0, 60) + "...";
            }
            System.out.println("- id=" + hit.getId() + ", score=" + hit.getScore()
                    + ", title=" + title
                    + ", content=" + contentShort);
        }
    }


    @FunctionalInterface
    private interface PageFetcher<T> {
        Page<T> fetch(int pageNo);
    }

    @FunctionalInterface
    private interface IndexRequestMapper<T> {
        IndexRequest map(T record);
    }
}
