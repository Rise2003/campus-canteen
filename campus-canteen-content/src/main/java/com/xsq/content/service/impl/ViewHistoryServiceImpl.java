package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.ViewHistoryMapper;
import com.xsq.content.model.po.ViewHistory;
import com.xsq.content.service.IViewHistoryService;
import org.springframework.stereotype.Service;

@Service
public class ViewHistoryServiceImpl extends ServiceImpl<ViewHistoryMapper, ViewHistory> implements IViewHistoryService {
}

