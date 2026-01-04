package com.xsq.content.controller;

import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.vo.DynamicListVO;
import com.xsq.content.service.IDynamicService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web 层契约测试：验证 /api/dynamic/list 的参数绑定与 DTO 映射。
 *
 * 说明：这里不依赖数据库，只 mock 掉 IDynamicService。
 */
@WebMvcTest(value = DynamicController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class
})
class DynamicControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDynamicService dynamicService;

    @Test
    void list_shouldBindDishCanteenUserAndSortOrder() throws Exception {
        when(dynamicService.getDynamicList(any(DynamicQueryDTO.class)))
                .thenReturn(DynamicListVO.success(Collections.emptyList(), 0L, 1, 10));

        mockMvc.perform(get("/api/dynamic/list")
                        .param("page", "2")
                        .param("size", "5")
                        .param("dishId", "11")
                        .param("canteenId", "22")
                        .param("userId", "33")
                        .param("sort", "likeCount")
                        .param("order", "asc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Result.success(...) 的结构不确定，这里仅做最宽松断言
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ArgumentCaptor<DynamicQueryDTO> captor = ArgumentCaptor.forClass(DynamicQueryDTO.class);
        verify(dynamicService, times(1)).getDynamicList(captor.capture());

        DynamicQueryDTO dto = captor.getValue();
        assertNotNull(dto);
        assertEquals(2, dto.getPage());
        assertEquals(5, dto.getSize());
        assertEquals(11L, dto.getDishId());
        assertEquals(22L, dto.getCanteenId());
        assertEquals(33L, dto.getUserId());
        assertEquals("likeCount", dto.getSort());
        assertEquals("asc", dto.getOrder());
    }

    @Test
    void list_shouldFallbackRecommendedToRecommendedOnly_whenRecommendedIsNull() throws Exception {
        when(dynamicService.getDynamicList(any(DynamicQueryDTO.class)))
                .thenReturn(DynamicListVO.success(Collections.emptyList(), 0L, 1, 10));

        mockMvc.perform(get("/api/dynamic/list")
                        .param("recommendedOnly", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        ArgumentCaptor<DynamicQueryDTO> captor = ArgumentCaptor.forClass(DynamicQueryDTO.class);
        verify(dynamicService).getDynamicList(captor.capture());
        DynamicQueryDTO dto = captor.getValue();

        // Controller 内 rec = recommended != null ? recommended : recommendedOnly
        assertNull(dto.getRecommended());
        assertEquals(Boolean.TRUE, dto.getRecommendedOnly());
    }
}
