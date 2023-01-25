package Project.SangCom.post.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class PostResponse {

    private Long id; // PK
    private String author; // 익명 or 닉네임
    private String title; // 게시글 제목
    private String content; // 게시글 내용
}
