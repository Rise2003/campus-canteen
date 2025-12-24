package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleVO implements Serializable {
    private Long id;
    private String nickname;
    private String avatarUrl;

    /**
     * 当前用户是否关注该作者（未登录时可为 null 或 false，由服务层决定）
     */
    private Boolean isFollowing;
}
