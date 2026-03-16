package com.chetraseng.sunrise_task_flow_api.dto;

import com.chetraseng.sunrise_task_flow_api.model.CommentModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequest extends CommentModel {
    private String content, author;

}
