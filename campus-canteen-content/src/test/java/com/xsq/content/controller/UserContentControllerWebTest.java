package com.xsq.content.controller;

import com.xsq.content.model.vo.UserContentVO;
import com.xsq.content.service.IUserContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = UserContentController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.class,
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration.class
})
class UserContentControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserContentService userContentService;

    @Test
    void getUserContent_shouldReturn200() throws Exception {
        when(userContentService.getUserContent(eq(1L), eq(2), eq(5)))
                .thenReturn(UserContentVO.builder()
                        .userId(1L)
                        .nickname("x")
                        .followingCount(0)
                        .followerCount(0)
                        .dynamicIds(Collections.emptyList())
                        .dynamics(Collections.emptyList())
                        .total(0L)
                        .page(2)
                        .size(5)
                        .hasNext(false)
                        .build());

        mockMvc.perform(get("/api/user-content/1")
                        .param("page", "2")
                        .param("size", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(userContentService).getUserContent(1L, 2, 5);
    }

    @Test
    void bind_shouldCallService() throws Exception {
        when(userContentService.bindDynamic(1L, 99L)).thenReturn(true);

        mockMvc.perform(post("/api/user-content/bind")
                        .param("userId", "1")
                        .param("dynamicId", "99")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(userContentService).bindDynamic(1L, 99L);
    }
}
