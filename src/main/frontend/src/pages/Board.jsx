import React, { useEffect } from 'react';
import ArticleCreate from '../components/board/ArticleCreate';
import Modal from '../components/ui/Modal';
import BoardBody from '../components/board/BoardBody';
import { authInstance } from '../utility/api';
import { useSelector } from 'react-redux';
import { useParams } from 'react-router-dom';
import BoardDetail from '../components/board-detail/BoardDetail';
import SearchBody from '../components/board/SearchBody';

export default function Board() {
    // const userInfo = useSelector((state) => state.loginReducer.user.info);
    const params = useParams();
    const category = params.category;
    const id = params.id;
    const search = params.search;
    
    // TODO: 배포 판에서는 지우기
    useEffect(() => {
        authInstance.get('api/board/test');
    }, []);
    
    return (
        <div>
            <div className='container'>
                {/* {id ? <BoardDetail /> : search ? <BoardBody category={category} /> : <BoardBody category={category} />} */}
                {id ? <BoardDetail /> : (search ? <SearchBody category={category} /> : <BoardBody category={category} />)}
                {/* BoardRightSide */}
            </div>
            
            { !id &&  <Modal iconName="MdCreate" feature={"글을 작성하세요!"}><ArticleCreate category="FREE" /></Modal>}
        </div>
    );
}

