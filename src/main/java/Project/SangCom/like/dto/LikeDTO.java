package Project.SangCom.like.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LikeDTO {
    private Long postId;
    private Long commentId;
    private Long parentId; //대댓글 좋아요 테스트
}
