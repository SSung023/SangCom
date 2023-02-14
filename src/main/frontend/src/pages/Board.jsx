import React from 'react';
import { useEffect } from 'react';
import ArticleCreate from '../components/board/ArticleCreate';
import Modal from '../components/ui/Modal';
import { authInstance } from '../utility/api';

export default function Board() {
    useEffect(()=> {
        authInstance.get('api/board/free')
        .then(function (res) {
            console.log(res.data);
        })
    }, []);

    return (
        <div>
            <Modal iconName="MdCreate" feature={"글을 작성하세요!"}>
                <ArticleCreate category="FREE" />
            </Modal>
            <div className='container'>
                
            </div>
        </div>
        
    );
}

