import { authInstance } from "./api";

// comment like
export const commentLike = async ( articleId, commentId, parentId ) => {
    const url = "/api/like/board/comment";
    const requestBody = {
        postId: `${articleId}`,
        commentId: `${commentId}`,
        parentId: `${parentId}`,
    };
    const postRequest = await (await authInstance.post(url, requestBody)).data;

    // Promise를 반환
    return postRequest;
};

// comment delete
export const commentDelete = async( category, postId, commentId ) => {
    const url = `/api/board/${category}/${postId}/comment/${commentId}`;
    const deleteRequest = await (await authInstance.delete(url)).data;
    
    return deleteRequest;
}

// comment report