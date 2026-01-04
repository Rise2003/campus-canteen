package com.xsq.content;

import com.xsq.content.mapper.DynamicMapper;
import com.xsq.content.model.po.Dynamic;
import com.xsq.content.model.vo.DynamicVO;
import com.xsq.content.service.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class DynamicServiceImplBatchDetailsTest {

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

    @MockBean
    private DynamicMapper dynamicMapper;

    @Test
    void getDynamicDetails_shouldKeepInputOrder_andSkipMissingOrNotNormal() {
        Dynamic d1 = new Dynamic();
        d1.setId(1L);
        d1.setStatus(1);
        d1.setTitle("t1");

        Dynamic d2 = new Dynamic();
        d2.setId(2L);
        d2.setStatus(3); // deleted

        Dynamic d3 = new Dynamic();
        d3.setId(3L);
        d3.setStatus(1);
        d3.setTitle("t3");

        when(dynamicMapper.selectBatchIds(ArgumentMatchers.anyCollection()))
                .thenReturn(Arrays.asList(d1, d2, d3));

        when(userService.listByIds(anyCollection())).thenReturn(Collections.emptyList());
        when(canteenService.listByIds(anyCollection())).thenReturn(Collections.emptyList());

        List<DynamicVO> vos = dynamicService.getDynamicDetails(Arrays.asList(3L, 1L, 2L, 4L), null);
        assertNotNull(vos);
        assertEquals(2, vos.size());
        assertEquals(3L, vos.get(0).getId());
        assertEquals(1L, vos.get(1).getId());
    }
}
