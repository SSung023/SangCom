import { authInstance } from "../../utility/api";

// comment like
export const likeAction = async ( isLike, commentId, parentId, articleId ) => {
    const url = "/api/like/board/comment";
    const requestBody = {
        postId: `${articleId}`,
        commentId: `${commentId}`,
        parentId: `${parentId}`,
    };

    const postRequest = await authInstance.post(url, requestBody);
    const deleteRequest = await authInstance.delete(url, requestBody);

    return isLike ? postRequest : deleteRequest;
};

// comment delete


// comment report