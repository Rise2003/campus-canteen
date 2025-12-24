package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.DishTaste;
import org.springframework.stereotype.Service;

@Service
/**
* @author qq187
* @description 针对表【dish_taste(菜品-口味关联表)】的数据库操作Service
* @createDate 2025-12-24 15:19:58
*/
public interface IDishTasteService extends IService<DishTaste> {

}
