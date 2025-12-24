package com.xsq.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xsq.content.model.po.CommentReaction;
import org.springframework.stereotype.Service;

@Service
/**
* @author qq187
* @description 针对表【comment_reaction(评论表态表)】的数据库操作Service
* @createDate 2025-12-24 15:19:58
*/
public interface ICommentReactionService extends IService<CommentReaction> {

}
