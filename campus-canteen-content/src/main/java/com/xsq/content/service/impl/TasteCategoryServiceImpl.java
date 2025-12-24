package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.TasteCategoryMapper;
import com.xsq.content.model.po.TasteCategory;
import com.xsq.content.service.ITasteCategoryService;
import org.springframework.stereotype.Service;

@Service
public class TasteCategoryServiceImpl extends ServiceImpl<TasteCategoryMapper, TasteCategory> implements ITasteCategoryService {
}

