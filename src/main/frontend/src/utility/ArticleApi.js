import {authInstance} from "./api";
import {post} from "axios";

export const articleLike = async ( articleId, commentId, parentId ) => {
    const url = `/api/like/board`;
    const requestBody = {
        postId: `${articleId}`,
        commentId: `${commentId}`,
        parentID: `${parentId}`,
    };
    return await (await authInstance.post(url, requestBody)).data;
}

export const articleDelete = async ( postId ) => {
    const url = `/api/board/${postId}`;
    return await (await authInstance.delete(url)).data;
}

export const articleScrap = async (postId) => {
    const url = `/api/scrap/${postId}`;
    return await (await authInstance.post(url));
}

export const articleUnscrap = async (postId) => {
    const url = `/api/scrap/${postId}`;
    return await (await authInstance.delete(url));
}