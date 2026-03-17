package com.chetraseng.sunrise_task_flow_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelResponse {
    private String name;
    private long id;
    private String color;
}
