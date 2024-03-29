package Project.SangCom.comment.service;

import Project.SangCom.comment.domain.Comment;
import Project.SangCom.comment.dto.CommentRequest;
import Project.SangCom.comment.dto.CommentResponse;
import Project.SangCom.comment.repository.CommentRepository;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.user.domain.Role;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.repository.UserRepository;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import Project.SangCom.utils.WithMockCustomUser;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@WebAppConfiguration
@Slf4j
public class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentService commentService;



    @Test
    @DisplayName("CommentRequest 객체를 Comment로 변환할 수 있다.")
    public void convertToComment(){
        //given
        CommentRequest commentRequest = getCommentRequest(0L, "test content");

        //when
        Comment receivedComment = commentRequest.toEntity();
        Comment savedComment = commentRepository.save(receivedComment);

        //then
        assertThat(receivedComment).isEqualTo(savedComment);
    }

    @Test
    @DisplayName("Comment 객체를 CommentResponse 객체로 변환할 수 있다.")
    @WithMockCustomUser
    public void convertToCommentResponse_1(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = getPost();
        CommentRequest request = getCommentRequest("comment");
        Long savedCommentId = commentService.saveComment(user.getId(), post.getId(), request);

        //when
        Comment savedComment = commentService.findCommentById(savedCommentId);
        CommentResponse commentResponse = commentService.convertToResponse(user, savedComment);

        //then
        assertThat(commentResponse.getId()).isEqualTo(savedComment.getId());
        assertThat(commentResponse.getAuthorName()).isEqualTo("익명");
        assertThat(commentResponse.getContent()).isEqualTo(savedComment.getContent());
        assertThat(commentResponse.getIsAnonymous()).isEqualTo(1);
        assertThat(commentResponse.getIsLikePressed()).isEqualTo(0);
        assertThat(commentResponse.getIsOwner()).isEqualTo(1);
        assertThat(commentResponse.getLikeCount()).isEqualTo(0);

        assertThat(commentResponse.getChildComment().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("Comment 대댓글 객체들까지 CommentResponse 객체로 변환할 수 있다.")
    @WithMockCustomUser
    public void convertToCommentResponse_2(){
        //given
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        Long reCommentId1 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommentId2 = saveReComment(user.getId(), post.getId(), commentId);

        //when
        Comment savedComment = commentService.findCommentById(commentId);
        CommentResponse commentResponse = commentService.convertToResponse(user, savedComment);

        //then
        assertThat(commentResponse.getId()).isEqualTo(savedComment.getId());
        assertThat(commentResponse.getAuthorName()).isEqualTo("익명");
        assertThat(commentResponse.getContent()).isEqualTo(savedComment.getContent());
        assertThat(commentResponse.getIsAnonymous()).isEqualTo(1);
        assertThat(commentResponse.getIsLikePressed()).isEqualTo(0);
        assertThat(commentResponse.getIsOwner()).isEqualTo(1);
        assertThat(commentResponse.getLikeCount()).isEqualTo(0);

        assertThat(commentResponse.getChildComment().size()).isEqualTo(2);

        assertThat(commentResponse.getChildComment().get(0).getIsOwner()).isEqualTo(1);
        assertThat(commentResponse.getChildComment().get(1).getIsOwner()).isEqualTo(1);
    }

    @Test
    @DisplayName("CommentRequest 객체를 전달받아 service를 통해 댓글을 DB에 저장할 수 있다.")
    public void canSaveComment(){
        //given
        User user = getUser();
        Post post = getPost();
        CommentRequest request = getCommentRequest(0L, "comment");

        //when
        User saveUser = userRepository.save(user);
        Long registeredId = commentService.saveComment(saveUser.getId(), post.getId(), request);
        Comment savedComment = commentRepository.findById(registeredId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        //then
        assertThat(request.getAuthorName()).isEqualTo(savedComment.getAuthor());
        assertThat(request.getContent()).isEqualTo(savedComment.getContent());
        assertThat(request.getIsAnonymous()).isEqualTo(savedComment.getIsAnonymous());

        assertThat(post.getComments().size()).isEqualTo(1);
        assertThat(post.getComments()).contains(savedComment);
    }

    @Test
    @DisplayName("댓글을 저장하면 Post의 commentCount가 증가한다.")
    public void increaseCommentCnt_whenSaveComment(){
        //given
        User user = getUser();
        Post post = getPost();

        //when
        saveComment(user.getId(), post);
        saveComment(user.getId(), post);

        //then
        assertThat(post.getCommentCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("대댓글을 저장하면 Post의 commentCount가 증가한다.")
    public void increaseCommentCnt_whenSaveReComment(){
        //given
        User user = getUser();
        Post post = getPost();

        //when
        Long commentId = saveComment(user.getId(), post);
        saveReComment(user.getId(), post.getId(), commentId);

        //then
        assertThat(post.getCommentCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("댓글을 삭제하면 Post의 commentCount가 감소한다.")
    public void decreaseCommentCnt_whenDeleteComment(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        saveComment(user.getId(), post);

        //when
        commentService.deleteComment(commentId, post.getId());

        //then
        assertThat(post.getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("대댓글을 삭제하면 Post의 commentCount가 감소한다.")
    public void decreaseCommentCnt_whenDeleteReComment(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        Long reCommentId = saveReComment(user.getId(), post.getId(), commentId);

        //when
        commentService.deleteComment(reCommentId, post.getId());

        //then
        assertThat(post.getCommentCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("CommentRequest 객체를 전달받아 service를 통해 대댓글을 DB에 저장할 수 있다.")
    public void canSaveReComment() {
        //given
        User user = getUser();
        Post post = getPost();

        Long savedCommentId = saveComment(post);
        Comment saveComment = commentService.findCommentById(savedCommentId);
        CommentRequest request = getCommentRequest(savedCommentId, "Re-comment");

        //when
        User saveUser = userRepository.save(user);

        Long registeredId = commentService.saveReComment(saveUser.getId(), post.getId(), savedCommentId, request);
        Comment savedReComment = commentRepository.findById(registeredId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        //then
        assertThat(request.getParentId()).isEqualTo(savedReComment.getParent().getId());
        assertThat(request.getAuthorName()).isEqualTo(savedReComment.getAuthor());
        assertThat(request.getContent()).isEqualTo(savedReComment.getContent());
        assertThat(request.getIsAnonymous()).isEqualTo(savedReComment.getIsAnonymous());

        assertThat(post.getComments().size()).isEqualTo(1); // 부모 댓글은 하나
        assertThat(post.getComments()).contains(saveComment);
        assertThat(post.getComments().get(0).getChildList()).contains(savedReComment);
    }

    /** 예외처리 -> 실패할 경우들 테스트
     * 댓글 저장 실패 - 게시물 없음
     * 대댓글 저장 실패 - 게시물 없음, 댓글 없음
     *
     * 댓글 삭제 실패 - 권한 없음
    */

    @Test
    @DisplayName("댓글 저장 실패 - 게시물 없음")
    public void failSaveComment(){
        //given
        User user = getUser();
        Post post = getPost();

        User savedUser = userRepository.save(user);
        Post savedPost = postRepository.save(post);

        Long userId = savedUser.getId();
        Long postId = savedPost.getId();

        CommentRequest request = getCommentRequest("Comment");

        //when&then
        assertThatThrownBy(() -> commentService.saveComment(userId, postId+1, request)) // 예외가 일어나는 코드
                .isInstanceOf(BusinessException.class) // 예외가 발생해야 하는 클래스 종류
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage()); // 예외 발생 시의 메세지
    }

    @Test
    @DisplayName("대댓글 저장 실패 - 게시물 없음")
    public void failSaveComment_2(){
        //given
        User user = getUser();
        Post post = getPost();

        User savedUser = userRepository.save(user);
        Post savedPost = postRepository.save(post);

        Long userId = savedUser.getId();
        Long postId = savedPost.getId();

        CommentRequest request1 = getCommentRequest("Comment");
        CommentRequest request2 = getCommentRequest("Re-comment");

        //when
        Long savedCommentId = commentService.saveComment(userId, postId, request1);

        //then
        assertThatThrownBy(() -> commentService.saveReComment(userId, 0L, savedCommentId, request2))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("대댓글 저장 실패 - 부모 댓글 없음")
    public void failSaveComment_3(){
        //given
        User user = getUser();
        Post post = getPost();

        User savedUser = userRepository.save(user);
        Post savedPost = postRepository.save(post);

        Long userId = savedUser.getId();
        Long postId = savedPost.getId();

        CommentRequest request = getCommentRequest("Re-comment");

        //when&then
        assertThatThrownBy(() -> commentService.saveReComment(userId, postId,0L, request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DATA_ERROR_NOT_FOUND.getMessage());

    }

    /**
     * 부모 삭제
     * 대댓글 존재
     * DB와 화면에서는 지워지지 않고, "삭제된 댓글입니다"라고 표시
     */
    @Test
    @DisplayName("부모 삭제 - 대댓글 존재")
    public void delComment_1() throws Exception {
        //given
        Post post = getPost();
        Long commentId = saveComment(post);
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);
        saveReComment(commentId);

        assertThat(commentService.findCommentById(commentId).getChildList().size()).isEqualTo(4);

        //when
        commentService.deleteComment(commentId, post.getId()); // 부모 댓글 삭제

        //then
        Comment findComment = commentService.findCommentById(commentId);
        assertThat(findComment).isNotNull();
        assertThat(findComment.getIsDeleted()).isEqualTo(1);
        assertThat(findComment.getChildList().size()).isEqualTo(4);

        assertThat(post.getComments().size()).isEqualTo(1);
        assertThat(post.getComments().get(0).getIsDeleted()).isEqualTo(1); // 부모 댓글 삭제 처리 확인
        assertThat(post.getComments().get(0).getChildList().size()).isEqualTo(4);
    }

    /**
     * 부모 댓글 삭제 & 대댓글 없을 때
     */
    @Test
    @DisplayName("부모 삭제 - 대댓글 없음")
    public void delComment_2() throws Exception {
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);

        //when
        commentService.deleteComment(commentId, post.getId());

        //then
        assertThat(post.getComments().size()).isEqualTo(1);
        assertThat(post.getComments().get(0).getIsDeleted()).isEqualTo(1);
    }

    /**
     * 부모 삭제
     * 대댓글 존재하지만 (isDeleted = 1) 논리적으로 모두 삭제된 경우
     * 부모댓글 삭제 시 댓글, 대댓글 DB에서 일괄 삭제
     */
    @Test
    @DisplayName("부모 삭제 - 대댓글 논리적으로 모두 삭제")
    public void delComment_3() throws Exception{
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(post);
        Long reCommend1 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommend2 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommend3 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommend4 = saveReComment(user.getId(), post.getId(), commentId);

        Assertions.assertThat(commentService.findCommentById(commentId).getChildList().size()).isEqualTo(4);

        //대댓글 삭제 -  삭제 검증
        commentService.deleteComment(reCommend1, post.getId());
        commentService.deleteComment(reCommend2, post.getId());
        commentService.deleteComment(reCommend3, post.getId());
        commentService.deleteComment(reCommend4, post.getId());

        Assertions.assertThat(commentService.findCommentById(reCommend1).getIsDeleted()).isEqualTo(1);
        Assertions.assertThat(commentService.findCommentById(reCommend2).getIsDeleted()).isEqualTo(1);
        Assertions.assertThat(commentService.findCommentById(reCommend3).getIsDeleted()).isEqualTo(1);
        Assertions.assertThat(commentService.findCommentById(reCommend4).getIsDeleted()).isEqualTo(1);

        //when
        commentService.deleteComment(commentId, post.getId());

        //then
        LongStream.rangeClosed(commentId, reCommend4).forEach(
                id -> assertThat(commentService.findCommentById(id).getIsDeleted()).isEqualTo(1));
//        LongStream.rangeClosed(commentId, reCommend4).forEach(id ->
//                assertThat(assertThrows(Exception.class, () -> commentService.findCommentById(id)).getMessage()).isEqualTo("해당 데이터를 찾을 수 없습니다.")
//        );

    }

    /**
     * 대댓글 삭제
     * 부모 존재 (isDeleted = 0)
     * 대댓글 논리적으로 삭제 (isDeleted = 1)
     */
    @Test
    @DisplayName("대댓글 삭제 - 부모 존재")
    public void delComment_4() throws Exception{
        //given
        Post post = getPost();
        Long commentId = saveComment(post);
        Long reCommend1Id = saveReComment(commentId);

        //when
        commentService.deleteComment(reCommend1Id, post.getId());

        //then
        Assertions.assertThat(commentService.findCommentById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findCommentById(reCommend1Id)).isNotNull();
        Assertions.assertThat(commentService.findCommentById(commentId).getIsDeleted()).isEqualTo(0);
        Assertions.assertThat(commentService.findCommentById(reCommend1Id).getIsDeleted()).isEqualTo(1);
    }

    /**
     * 대댓글 삭제
     * 부모, 다른 대댓글 모두 논리적 삭제 (isDeleted = 1)된 경우
     * 부모, 대댓글 모두 DB에서 일괄 삭제 (화면에도 X)
     */
    @Test
    @DisplayName("대댓글 삭제 - 부모, 다른 대댓글 모두 논리적 삭제")
    public void delComment_5() throws Exception{
        //given
        Post post = getPost();
        Long commentId = saveComment(post);
        Long reCommend1 = saveReComment(commentId);
        Long reCommend2 = saveReComment(commentId);
        Long reCommend3 = saveReComment(commentId);

        commentService.deleteComment(commentId, post.getId());
        commentService.deleteComment(reCommend2, post.getId());
        commentService.deleteComment(reCommend3, post.getId());

        Comment commentById = commentService.findCommentById(commentId);

        Assertions.assertThat(commentService.findCommentById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findCommentById(commentId).getChildList().size()).isEqualTo(3);

        //when
        commentService.deleteComment(reCommend1, post.getId());

        //then
        LongStream.rangeClosed(commentId, reCommend3).forEach(
                id -> assertThat(commentService.findCommentById(id).getIsDeleted()).isEqualTo(1));
//        LongStream.rangeClosed(commentId, reCommend3).forEach(id ->
//                assertThat(assertThrows(Exception.class, () -> commentService.findCommentById(id)).getMessage()).isEqualTo("해당 데이터를 찾을 수 없습니다.")
//        );
    }

    @Test
    @DisplayName("대댓글 삭제 - 부모, 다른 대댓글 중 마지막 대댓글만 논리적 삭제")
    public void deleteReComment_1(){
        //given
        Post post = getPost();
        Long commentId = saveComment(post);
        saveReComment(commentId);
        saveReComment(commentId);
        Long reCommend3 = saveReComment(commentId);

        //when
        commentService.deleteComment(reCommend3, post.getId());
        Comment pComment = commentService.findCommentById(commentId);

        //then
        assertThat(pComment.getChildList().size()).isEqualTo(3);
        assertThat(pComment.getChildList().get(2).getIsDeleted()).isEqualTo(1);
    }

    @Test
    @DisplayName("대댓글 삭제 - 부모 댓글 삭제 시도 시 남아있는 대댓글이 있다면 삭제 대상에 아무것도 들어가지 않아야 한다.")
    public void deleteReComment_2(){
        //given
        Post post = getPost();
        Long commentId = saveComment(post);
        Long reCommend1 = saveReComment(commentId);
        Long reCommend2 = saveReComment(commentId);
        Long reCommend3 = saveReComment(commentId);

        //when
        commentService.deleteComment(reCommend1, post.getId()); // 대댓글 하나 삭제

        Comment pComment = commentService.findCommentById(commentId); // 부모 댓글
        assertThat(pComment.getChildList().size()).isEqualTo(3);

        List<Comment> removableList = pComment.findRemovableList();

        //then
        assertThat(removableList.size()).isEqualTo(0);
    }

    /**
     * 대댓글 삭제
     * 부모 논리적 삭제 (isDeleted = 1), 다른 대댓글 존재 (isDeleted = 0)하는 경우
     * 삭제할 대댓글만 (isDeleted = 1) 논리적 삭제 (DB에 존재), "삭제된 댓글입니다" 표시
     */
    @Test
    @DisplayName("대댓글 삭제 - 부모 논리적 삭제, 다른 대댓글 존재")
    public void delComment_6() throws Exception{
        //given
        Post post = getPost();
        Long commentId = saveComment(post);
        Long reCommend1 = saveReComment(commentId);
        Long reCommend2 = saveReComment(commentId);
        Long reCommend3 = saveReComment(commentId);

        //질문! 왜 reCommend1부터 지우면 안되지..?
        Comment commentById = commentService.findCommentById(commentId);
        commentService.deleteComment(reCommend1, post.getId());
        commentService.deleteComment(commentId, post.getId());

        //reCommend1부터 지우면 여기서 comment 못찾아옴
        // -> isAllChildDel에서 제일 첫번째 대댓글이 지워져서 다 지워졌다고 판단한건가..?
        // stream()을 잘못 사용했나? 다시 알아보기
        Assertions.assertThat(commentService.findCommentById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findCommentById(commentId).getChildList().size()).isEqualTo(3);

        //when
        commentService.deleteComment(reCommend2, post.getId()); // 부모댓, 대댓1, 대댓2 삭제 상태
        Assertions.assertThat(commentService.findCommentById(commentId)).isNotNull();
        Assertions.assertThat(commentService.findCommentById(commentId).getChildList().size()).isEqualTo(3);

        //then
        Assertions.assertThat(commentService.findCommentById(reCommend1)).isNotNull();
        Assertions.assertThat(commentService.findCommentById(reCommend1).getIsDeleted()).isEqualTo(1);

        Assertions.assertThat(commentService.findCommentById(reCommend2).getId()).isNotNull();
        Assertions.assertThat(commentService.findCommentById(reCommend2).getIsDeleted()).isEqualTo(1);

        Assertions.assertThat(commentService.findCommentById(reCommend3).getId()).isNotNull();
        Assertions.assertThat(commentService.findCommentById(reCommend3).getIsDeleted()).isEqualTo(0);

        Assertions.assertThat(commentService.findCommentById(commentId).getId()).isNotNull();
        Assertions.assertThat(commentService.findCommentById(commentId).getIsDeleted()).isEqualTo(1);
        Assertions.assertThat(commentService.findCommentById(commentId).getChildList().size()).isEqualTo(3);
    }

    /**
     * 부모 댓글 삭제 & 대댓글 없을 때
     * 삭제 이후 해당 게시글의 댓글 조회 시, 조회 대상에 포함되지 않는다.
     */
    @Test
    @DisplayName("부모 삭제 - 대댓글 없음 : 댓글 조회 대상 아님")
    public void checkingComment_1(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);

        //when
        commentService.deleteComment(commentId, post.getId());
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(0);
    }

    /**
     * 부모 댓글 존재
     * 해당 게시글의 댓글 조회 시, 조회 대상에 포함
     */
    @Test
    @DisplayName("부모 존재 - 대댓글 없음 : 댓글 조회 대상 포함")
    public void checkingComment_2(){
        //given
        User user = getUser();
        Post post = getPost();
        saveComment(user.getId(), post);

        //when
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(1);
    }

    /**
     * 부모 댓글, 대댓글 존재
     * 해당 게시글의 댓글 조회 시, 조회 대상에 포함
     */
    @Test
    @DisplayName("부모 존재 - 대댓글 존재 : 댓글 조회 대상 포함")
    public void checkingComment_3(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        saveReComment(user.getId(), post.getId(), commentId);
        saveReComment(user.getId(), post.getId(), commentId);

        //when
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(1);
        assertThat(commentList.get(0).getChildComment().size()).isEqualTo(2);
    }

    /**
     * 부모 댓글, 대댓글 여러 개 존재 중 하나 삭제
     * 해당 게시글의 댓글 조회 시, 조회 대상에 포함
     */
    @Test
    @DisplayName("부모 존재 - 대댓글 여러 개 중 하나 삭제 : 댓글 조회 대상 포함")
    public void checkingComment_4(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        saveReComment(user.getId(), post.getId(), commentId);
        Long reCommentId = saveReComment(user.getId(), post.getId(), commentId);

        //when
        commentService.deleteComment(reCommentId, post.getId());
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(1);
        assertThat(commentList.get(0).getChildComment().size()).isEqualTo(2);
    }

    /**
     * 부모 댓글, 대댓글 여러 개 존재하나 모두 삭제
     * 해당 게시글의 댓글 조회 시, 조회 대상에 포함
     */
    @Test
    @DisplayName("부모 존재 - 대댓글 여러 개 모두 삭제: 댓글 조회 대상 포함")
    public void checkingComment_5(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        Long reCommentId1 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommentId2 = saveReComment(user.getId(), post.getId(), commentId);

        //when
        commentService.deleteComment(reCommentId1, post.getId());
        commentService.deleteComment(reCommentId2, post.getId());
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(1);
        assertThat(commentList.get(0).getChildComment().size()).isEqualTo(2);
    }

    /**
     * 부모 댓글 삭제, 대댓글 여러 개 존재
     * 해당 게시글의 댓글 조회 시, 조회 대상에 포함 안됨
     */
    @Test
    @DisplayName("부모 삭제 - 대댓글 여러 개: 댓글 조회 대상 포함")
    public void checkingComment_6(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        Long reCommentId1 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommentId2 = saveReComment(user.getId(), post.getId(), commentId);

        //when
        commentService.deleteComment(commentId, post.getId());
        commentService.deleteComment(reCommentId1, post.getId());
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(1);
        assertThat(commentList.get(0).getContent()).isEqualTo("댓글이 삭제되었습니다.");
        assertThat(commentList.get(0).getChildComment().size()).isEqualTo(2);
    }

    /**
     * 부모 댓글 삭제, 대댓글 여러 개 존재하나 모두 삭제
     * 해당 게시글의 댓글 조회 시, 조회 대상에 포함 안됨
     */
    @Test
    @DisplayName("부모 삭제 - 대댓글 여러 개 모두 삭제: 댓글 조회 대상 안됨")
    public void checkingComment_7(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);
        Long reCommentId1 = saveReComment(user.getId(), post.getId(), commentId);
        Long reCommentId2 = saveReComment(user.getId(), post.getId(), commentId);

        //when
        commentService.deleteComment(commentId, post.getId());
        commentService.deleteComment(reCommentId1, post.getId());
        commentService.deleteComment(reCommentId2, post.getId());
        List<CommentResponse> commentList = commentService.findPostCommentList(user, post.getId());

        //then
        assertThat(commentList.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 삭제 시, authorName에 알 수 없음 으로 변환되어야 한다.")
    public void passAsUnknown(){
        //given
        User user = getUser();
        Post post = getPost();
        Long commentId = saveComment(user.getId(), post);

        //when
        commentService.deleteComment(commentId, post.getId());
        CommentResponse commentResponse = commentService.convertToResponse(user, commentId);

        //then
        assertThat(commentResponse.getAuthorName()).isEqualTo("알 수 없음");

    }










    private User getUser() {
        User user = User.builder()
                .username("username")
                .nickname("nickname")
                .email("test@naver.com")
                .role(Role.STUDENT.getKey())
                .build();
        return userRepository.save(user);
    }

    private Post getPost(){
        Post post = Post.builder()
                .category(PostCategory.FREE)
                .author("author")
                .title("title")
                .content("content")
                .isAnonymous(0)
                .build();
        return postRepository.save(post);
    }

    private Comment getComment(){
        return Comment.builder()
                .author("author")
                .content("content")
                .isAnonymous(0)
                .build();
    }

    private Long saveComment(Post post){
        Comment comment = Comment.builder()
                .author("부모")
                .content("부모 댓글")
                .isAnonymous(0)
                .build();

        Comment newComment = commentRepository.save(comment);
        newComment.setPost(post);

        return newComment.getId();
    }

    private Long saveComment(Long userId, Post post){
        CommentRequest request = getCommentRequest("부모 댓글");

        Long savedCommentId = commentService.saveComment(userId, post.getId(), request);
        return savedCommentId;
    }

    private Long saveReComment(Long parentId){
        Comment parent = commentRepository.findById(parentId).orElse(null);
        Comment comment = Comment.builder()
                .author("자식")
                .content("자식 댓글")
//                .parent(parent)
                .isAnonymous(0)
                .build();

//        Long id = commentRepository.save(comment).getId();
//        parent.addChild(commentService.findCommentById(id));
        Comment savedComment = commentRepository.save(comment);
        savedComment.setParent(parent);

        return savedComment.getId();
    }

    private Long saveReComment(Long userId, Long postId, Long parentId){
        Comment parent = commentService.findCommentById(parentId);
        CommentRequest request = getCommentRequest("자식 댓글");

        Long savedReCommentId = commentService.saveReComment(userId, postId, parentId, request);
        return savedReCommentId;
    }

    private CommentRequest getCommentRequest(Long parentId, String content) {
        return CommentRequest.builder()
                .parentId(parentId)
                .authorName("nickname")
                .content(content)
                .isAnonymous(1) // true
                .build();
    }
    private CommentRequest getCommentRequest(String content) {
        return CommentRequest.builder()
                .authorName("nickname")
                .content(content)
                .isAnonymous(1) // true
                .build();
    }
}
