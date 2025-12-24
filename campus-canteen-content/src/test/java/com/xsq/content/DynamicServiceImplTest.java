package com.xsq.content;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xsq.content.mapper.DynamicMapper;
import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.po.Dynamic;
import com.xsq.content.model.vo.DynamicListVO;
import com.xsq.content.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DynamicServiceImplTest {

    @Autowired
    private IDynamicService dynamicService;

    @MockBean
    private IUserService userService;
    @MockBean
    private ICanteenService canteenService;
    @MockBean
    private IDishService dishService;
    @MockBean
    private ICollectionService collectionService;
    @MockBean
    private IDynamicDishService dynamicDishService;
    @MockBean
    private ILikeRecordService likeRecordService;
    @MockBean
    private IFollowService followService;

    // ServiceImpl 依赖的 baseMapper
    @MockBean
    private DynamicMapper dynamicMapper;

    @Test
    void getDynamicList_nullQuery_returnsEmptySuccess() {
        DynamicListVO vo = dynamicService.getDynamicList(null);
        assertNotNull(vo);
        assertEquals(0L, vo.getTotal());
        assertNotNull(vo.getList());
        assertTrue(vo.getList().isEmpty());
    }

    @Test
    void getDynamicList_basicQuery_doesNotThrow() {
        // Stub ServiceImpl.page(...) -> baseMapper.selectPage(...) 走 MyBatis-Plus
        when(dynamicMapper.selectPage(ArgumentMatchers.any(Page.class), ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    Page<Dynamic> page = invocation.getArgument(0);
                    page.setRecords(java.util.Collections.emptyList());
                    page.setTotal(0);
                    return page;
                });

        DynamicQueryDTO dto = new DynamicQueryDTO();
        dto.setPage(1);
        dto.setSize(10);

        DynamicListVO vo = dynamicService.getDynamicList(dto);
        assertNotNull(vo);
        assertEquals(0L, vo.getTotal());
        assertNotNull(vo.getList());
    }

    @Test
    void getDynamicList_hasNext_trueWhenMoreThanOnePage() {
        when(dynamicMapper.selectPage(ArgumentMatchers.any(Page.class), ArgumentMatchers.any()))
                .thenAnswer(invocation -> {
                    Page<Dynamic> page = invocation.getArgument(0);
                    page.setRecords(java.util.Collections.emptyList());
                    page.setTotal(11);
                    return page;
                });

        DynamicQueryDTO dto = new DynamicQueryDTO();
        dto.setPage(1);
        dto.setSize(10);

        DynamicListVO vo = dynamicService.getDynamicList(dto);
        assertNotNull(vo);
        assertEquals(11L, vo.getTotal());
        assertEquals(1, vo.getPage());
        assertEquals(10, vo.getSize());
        assertTrue(Boolean.TRUE.equals(vo.getHasNext()));
    }
}
