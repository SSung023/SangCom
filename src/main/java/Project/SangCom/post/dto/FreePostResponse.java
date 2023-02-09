package Project.SangCom.post.dto;

import Project.SangCom.post.domain.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Slice;

@Getter
@NoArgsConstructor
@ToString
public class FreePostResponse {
    private PostResponse mostLikedPost; // 실시간 인기글에 대한 정보

    private Slice<PostResponse> recentlyPost; // 최근 작성된


    @Builder
    public FreePostResponse(PostResponse mostLikedPost, Slice<PostResponse> recentlyPost) {
        this.mostLikedPost = mostLikedPost;
        this.recentlyPost = recentlyPost;
    }
}
