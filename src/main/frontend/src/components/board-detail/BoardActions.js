import { authInstance } from "../../utility/api";

// comment like
export const likeAction = async ( isLike, articleId, commentId, parentId ) => {
    const url = "/api/like/board/comment";
    const requestBody = {
        postId: `${articleId}`,
        commentId: `${commentId}`,
        parentId: `${parentId}`,
    };

    const postRequest = await (await authInstance.post(url, {...requestBody})).data;
    const deleteRequest = await (await authInstance.delete(url, {...requestBody})).data;

    console.log(requestBody);
    // Promise를 반환
    return isLike ? deleteRequest : postRequest;
};

// comment delete


// comment report