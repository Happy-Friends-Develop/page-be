package com.example.hello_friends.board.application.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "게시글 요청")
public class BoardRequest {
    @Schema(description = "게시글 제목", example = "제목입니다")
    private String title;

    @Schema(description = "게시글 내용", example = "내용입니다")
    private String content;
}

