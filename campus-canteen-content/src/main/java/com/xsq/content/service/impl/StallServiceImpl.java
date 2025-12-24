package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.StallMapper;
import com.xsq.content.model.po.Stall;
import com.xsq.content.service.IStallService;
import org.springframework.stereotype.Service;

@Service
public class StallServiceImpl extends ServiceImpl<StallMapper, Stall> implements IStallService {
}

