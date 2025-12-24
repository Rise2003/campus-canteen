package com.xsq.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xsq.content.mapper.CommentReactionMapper;
import com.xsq.content.model.po.CommentReaction;
import com.xsq.content.service.ICommentReactionService;
import org.springframework.stereotype.Service;

@Service
public class CommentReactionServiceImpl extends ServiceImpl<CommentReactionMapper, CommentReaction> implements ICommentReactionService {
}

