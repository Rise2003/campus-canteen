package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.TasteTagMapper;
import com.xsq.content.model.po.TasteTag;
import com.xsq.content.service.ITasteTagService;
import org.springframework.stereotype.Service;

@Service
public class TasteTagServiceImpl extends ServiceImpl<TasteTagMapper, TasteTag> implements ITasteTagService {
}

