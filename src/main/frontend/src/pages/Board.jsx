import React, { useEffect } from 'react';
import ArticleCreate from '../components/board/ArticleCreate';
import Modal from '../components/ui/Modal';
import BoardBodyLayout from '../layouts/BoardBodyLayout';
import { authInstance } from '../utility/api';

export default function Board() {
    
    // TODO: 배포 판에서는 지워야 하나?
    useEffect(() => {
        authInstance.get('api/board/test');
    }, []);
    return (
        <div>
            <div className='container'>
                <BoardBodyLayout 
                    boardTitle="자유 게시판" 
                    bestApi="api/board/free/best"
                    listApi="api/board/free/list"
                />
            </div>

            <Modal iconName="MdCreate" feature={"글을 작성하세요!"}>
                <ArticleCreate category="FREE" />
            </Modal>
        </div>
        
    );
}

