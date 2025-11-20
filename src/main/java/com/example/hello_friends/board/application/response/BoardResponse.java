package com.example.hello_friends.board.application.response;

import com.example.hello_friends.board.domain.Board;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
// 값이 null인 필드는 아예 JSON에서 빼버리는 설정 (목록 조회할 때 files 필드가 아예 안 보이게 설정)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardResponse {
    private Long id;
    private String title;
    private String content;
    private Long view;
    private String authorNickname;
    private int likeCount;
    private LocalDateTime createdAt;
    private String address;

    // 상세 조회 때만 들어갈 파일 리스트
    private List<BoardFileResponse> files;

    // 목록 조회용
    public static BoardResponse from(Board board) {
        BoardResponse response = new BoardResponse();
        response.setId(board.getId());
        response.setTitle(board.getTitle());
        response.setContent(board.getContent());
        response.setView(board.getView());
        response.setAuthorNickname(board.getUser().getNickname());
        response.setCreatedAt(board.getCreatedAt());
        response.setLikeCount(board.getLikes().size());
        response.setAddress(board.getAddress());
        return response;
    }

    // 상세 조회용 - 파일까지 포함
    public static BoardResponse fromDetail(Board board) {
        BoardResponse response = from(board);

        // 파일이 있다면 변환하고 같이 담기
        if (board.getFiles() != null && !board.getFiles().isEmpty()) {
            response.setFiles(board.getFiles().stream()
                    .map(BoardFileResponse::from)
                    .collect(Collectors.toList()));
        }
        return response;
    }
}