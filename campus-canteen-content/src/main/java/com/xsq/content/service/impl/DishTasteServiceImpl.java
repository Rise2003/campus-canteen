package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.DishTasteMapper;
import com.xsq.content.model.po.DishTaste;
import com.xsq.content.service.IDishTasteService;
import org.springframework.stereotype.Service;

@Service
public class DishTasteServiceImpl extends ServiceImpl<DishTasteMapper, DishTaste> implements IDishTasteService {
}

