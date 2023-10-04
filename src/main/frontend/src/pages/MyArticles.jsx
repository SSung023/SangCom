import React from "react";
import Leftside from "../components/main/Leftside";
import MyPost from "../components/my-articles/MyPost";

export default function MyArticles() {
    return (
        <div className="container">
            <Leftside />
            <MyPost />
        </div>
    );
}
