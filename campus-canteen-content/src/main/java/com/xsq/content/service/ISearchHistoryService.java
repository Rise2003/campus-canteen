package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.SearchHistory;
import org.springframework.stereotype.Service;

@Service
/**
* @author qq187
* @description 针对表【search_history(搜索记录表)】的数据库操作Service
* @createDate 2025-12-24 15:19:58
*/
public interface ISearchHistoryService extends IService<SearchHistory> {

}
