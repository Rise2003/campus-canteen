package com.xsq.content.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicListVO implements Serializable {

    private List<DynamicVO> list;
    private Long total;
    private Integer page;
    private Integer size;

    /**
     * 是否还有下一页
     */
    private Boolean hasNext;

    public static DynamicListVO success(List<DynamicVO> list, Long total, Integer page, Integer size) {
        boolean hasNext = total != null && page != null && size != null && total > (long) page * size;
        return DynamicListVO.builder()
                .list(list)
                .total(total)
                .page(page)
                .size(size)
                .hasNext(hasNext)
                .build();
    }
}
