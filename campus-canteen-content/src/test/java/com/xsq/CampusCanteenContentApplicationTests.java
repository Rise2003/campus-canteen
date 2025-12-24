package com.xsq;

import com.xsq.content.mapper.DynamicMapper;
import com.xsq.content.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

//@SpringBootTest
class CampusCanteenContentApplicationTests {

    @MockBean private IUserService userService;
    @MockBean private ICanteenService canteenService;
    @MockBean private IDishService dishService;
    @MockBean private ICollectionService collectionService;
    @MockBean private IDynamicDishService dynamicDishService;
    @MockBean private ILikeRecordService likeRecordService;
    @MockBean private IFollowService followService;

    @MockBean private DynamicMapper dynamicMapper;

    @Test
    void contextLoads() {
    }

}
