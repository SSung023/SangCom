package Project.SangCom.post.service;

import Project.SangCom.like.domain.Likes;
import Project.SangCom.like.repository.LikeRepository;
import Project.SangCom.like.service.LikeService;
import Project.SangCom.post.domain.Post;
import Project.SangCom.post.domain.PostCategory;
import Project.SangCom.post.dto.PostRequest;
import Project.SangCom.post.dto.PostResponse;
import Project.SangCom.post.repository.PostRepository;
import Project.SangCom.user.domain.User;
import Project.SangCom.user.service.UserService;
import Project.SangCom.util.exception.BusinessException;
import Project.SangCom.util.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static Project.SangCom.post.dto.PostResponse.FALSE;
import static Project.SangCom.post.dto.PostResponse.TRUE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final UserService userService;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;


    /**
     * RequestDTO를 Entity로 변환하고 repository를 통해 저장
     * @param writerId 게시글을 작성한 사용자의 id(PK)
     * @param postCategory 게시글의 게시판 종류 정보
     * @param postRequest 사용자에게 전달받은 게시글 정보
     */
    @Transactional
    public Long savePost(Long writerId, PostCategory postCategory, PostRequest postRequest) {
        User user = userService.findUserById(writerId);
        Post post = postRequest.toEntity(postCategory);

        Post savedPost = postRepository.save(post);
        savedPost.addUser(user); // user는 이 메서드 안에서 찾아서 넣어주어야 한다.

        return savedPost.getId();
    }

    /**
     * post_id(PK)를 통해 Post 객체를 찾아서 반환
     * @param postId repository(DB)에서 찾고자 하는 Post의 id
     */
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
    }

    /**
     * postId에 해당하는 게시글을 postRequest의 내용으로 수정
     * @param postId 수정하고자하는 게시글의 Id(PK)
     * @param postRequest 수정하고자 하는 내용
     */
    @Transactional
    public Long updatePost(Long postId, PostRequest postRequest){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
        post.updatePost(postRequest);

        return post.getId();
    }

    /**
     * postId에 해당하는 게시글을 삭제 처리(isDeleted = 1(true)로 변경)
     * @param postId 삭제처리 할 게시글의 Id(PK)
     */
    @Transactional
    public Long deletePost(Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));
        post.deletePost();

        return post.getId();
    }

    /**
     * Post DB 테이블에 있는 모든 정보들을 삭제
     */
    @Transactional
    public void clearAll(){
        postRepository.deleteAllInBatch();
    }

    /**
     * Post에 저장되어 있는 User의 id와 사용자의 id가 일치하는지 확인
     * 작성자가 맞으므로 postResponse 객체의 isOwner를 TRUE(1)로 설정,
     * 작성자가 아니라면 postRepsonse 객체의 isOwner를 FALSE(0)으로 설정
     */
    public int checkIsPostOwner(User user, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        if (post.getUser().getId().equals(user.getId())){
            return 1;
        }
        else {
            return 0;
        }
    }


    /**
     * 찾고자 하는 게시판에서 삭제되지 않은 게시글들을 페이징하여 반환
     * @param category 게시글을 찾고자하는 게시판 종류
     */
    public Slice<PostResponse> getNotDeletedPostList(User user, PostCategory category, Pageable pageable){
        Slice<Post> posts = postRepository.findPostNotDeleted(0, category, pageable);

        return posts.map(p -> convertToPreviewResponse(user, p));
    }

    /**
     * 대상 사용자가 작성한 모든 글을 조회하여 페이징하여 반환
     * @param user 내가 쓴 글을 조회하는 사용자
     */
    public Slice<PostResponse> getAllWritePostList(User user, Pageable pageable){
        Slice<Post> posts = postRepository.findAllWrotePosts(user.getId(), pageable);
        return posts.map(p -> convertToPreviewResponse(user, p));
    }

    /**
     * 찾고자 하는 게시판에서 제목/내용/제목+내용
     * @param query 검색하는 방법: 제목(title)/내용(content)/제목+내용(all)
     * @param category 검색하고자 하는 게시판 종류
     */
    public Slice<PostResponse> searchPosts(User user, String query, String keyword, PostCategory category, Pageable pageable){
        Slice<Post> posts = null;

        if (query.equals("title")) {
            posts = postRepository.searchPostByTitle(keyword, category, pageable);
        }
        else if (query.equals("content")) {
            posts = postRepository.searchPostByContent(keyword, category, pageable);
        }
        else if (query.equals("all")) {
            posts = postRepository.searchPost(keyword, keyword, category, pageable);
        }

        return posts.map(p -> convertToPreviewResponse(user, p));
    }

    /**
     * 특정 게시판에서 24시간 내에 좋아요 수가 제일 많은 게시글을 검색
     * 만일 존재하지 않으면 optional.empty 반환
     * @param category 검색하고자하는 게시판의 종류
     */
    public List<Post> getMostLikedPost(PostCategory category, Pageable pageable){
        LocalDateTime threshold = LocalDateTime.now().minusDays(1);

        List<Post> posts = postRepository.findMostLikedPost(threshold, category, pageable);
        return posts;
    }

    public PostCategory checkCategory(String category){
        switch (category) {
            case "free":
                return PostCategory.FREE;
            case "grade1":
                return PostCategory.GRADE1;
            case "grade2":
                return PostCategory.GRADE2;
            case "grade3":
                return PostCategory.GRADE3;
            case "council":
                return PostCategory.COUNCIL;
            case "suggestion":
                return PostCategory.SUGGESTION;
            case "club":
                return PostCategory.CLUB;
        }
        throw new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND);
    }






    /**
     * 자유게시판에 전달할 PostResponse(Post) 객체로 반환
     *
     * 상황에 따른 유의미한 필드 값
     * 1. 게시글 목록으로 전달 시 convertToPreviewResponse
     *    -> id, author, title, content, commentCount, likeCount, isLikePressed에만 유의미한 값
     * 2. 게시글 상세 조회 시 convertToDetailResponse
     *    -> 모든 필드에 유의미한 값 전달
     * @param postId PostResponse로 변환하고 싶은 post의 PK
     */
    public PostResponse convertToDetailResponse(User user, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        return PostResponse.builder()
                .id(post.getId())
                .boardCategory(post.getCategory().toString())
                .author(checkIsAnonymous(post))
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .isLikePressed(checkIsLikePressed(user, post))
                .isOwner(checkIsPostOwner(user, post.getId()))
                .isAnonymous(post.getIsAnonymous())
                .createdDate(post.getCreatedDate())
                .build();
    }
    public PostResponse convertToDetailResponse(User user, Post post){
        return PostResponse.builder()
                .id(post.getId())
                .boardCategory(post.getCategory().toString())
                .author(checkIsAnonymous(post))
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .isLikePressed(checkIsLikePressed(user, post))
                .isOwner(checkIsPostOwner(user, post.getId()))
                .isAnonymous(post.getIsAnonymous())
                .createdDate(post.getCreatedDate())
                .build();
    }

    public PostResponse convertToPreviewResponse(User user, Long postId){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.DATA_ERROR_NOT_FOUND));

        return PostResponse.builder()
                .id(post.getId())
                .author(checkIsAnonymous(post))
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .isLikePressed(checkIsLikePressed(user, post))
                .createdDate(post.getCreatedDate())
                .build();
    }
    public PostResponse convertToPreviewResponse(User user, Post post){
        return PostResponse.builder()
                .id(post.getId())
                .author(checkIsAnonymous(post))
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .likeCount(post.getLikeCount())
                .isLikePressed(checkIsLikePressed(user, post))
                .createdDate(post.getCreatedDate())
                .build();
    }

    private String checkIsAnonymous(Post post){
        if (post.getIsAnonymous() == 0){
            return post.getAuthor();
        }
        else {
            return "익명";
        }
    }
    private int checkIsLikePressed(User user, Post post){
        Optional<Likes> likes = likeRepository.findLikes(user.getId(), post.getId());
        if (likes.isPresent()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
