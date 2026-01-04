package com.xsq.content.controller;

import com.xsq.content.model.dto.DynamicQueryDTO;
import com.xsq.content.model.vo.*;
import com.xsq.content.service.IDishService;
import com.xsq.content.service.IDynamicService;
import com.xsq.content.service.IUserService;
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

@WebMvcTest(value = SearchController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class
})
class SearchControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDynamicService dynamicService;

    @MockBean
    private IDishService dishService;

    @MockBean
    private IUserService userService;

    @Test
    void searchDynamics_shouldBindKeywordPageSize() throws Exception {
        when(dynamicService.getDynamicList(any(DynamicQueryDTO.class)))
                .thenReturn(DynamicListVO.success(Collections.emptyList(), 0L, 1, 10));

        mockMvc.perform(get("/api/search/dynamics")
                        .param("keyword", "burger")
                        .param("page", "2")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ArgumentCaptor<DynamicQueryDTO> captor = ArgumentCaptor.forClass(DynamicQueryDTO.class);
        verify(dynamicService, times(1)).getDynamicList(captor.capture());
        DynamicQueryDTO dto = captor.getValue();
        assertEquals("burger", dto.getKeyword());
        assertEquals(2, dto.getPage());
        assertEquals(5, dto.getSize());
    }

    @Test
    void searchAll_shouldCallDishAndUserSearchAndDynamic() throws Exception {
        when(dynamicService.getDynamicList(any(DynamicQueryDTO.class)))
                .thenReturn(DynamicListVO.success(Collections.emptyList(), 0L, 1, 10));
        when(dishService.searchDishes(eq("noodle"), eq(1), eq(10)))
                .thenReturn(Collections.singletonList(DishSimpleVO.builder().id(1L).name("noodle").build()));
        when(dishService.countSearchDishes(eq("noodle"))).
                thenReturn(1L);

        when(userService.searchUsersByUsername(eq("noodle"), eq(1), eq(10)))
                .thenReturn(Collections.singletonList(UserSimpleVO.builder().id(2L).nickname("tom").build()));
        when(userService.countSearchUsersByUsername(eq("noodle")))
                .thenReturn(1L);

        mockMvc.perform(get("/api/search")
                        .param("keyword", "noodle")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(dynamicService, times(1)).getDynamicList(any(DynamicQueryDTO.class));
        verify(dishService, times(1)).searchDishes("noodle", 1, 10);
        verify(dishService, times(1)).countSearchDishes("noodle");
        verify(userService, times(1)).searchUsersByUsername("noodle", 1, 10);
        verify(userService, times(1)).countSearchUsersByUsername("noodle");
    }
}
