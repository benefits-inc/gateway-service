package com.benefits.gatewayservice.common.spec;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Pagination{
    private Integer page;
    private Integer size;
    private Integer currentElements;
    private Long totalElements;
    private Integer totalPage;

}
