import React from 'react';
import { forwardRef } from 'react';
import { useEffect } from 'react';
import { useRef } from 'react';
import { useState } from 'react';
import styles from './Modal.module.css';
import { MdCreate } from "react-icons/md";

export default function Modal({ children, iconName, feature }) {
    const [modal, setModal] = useState(false);
    const [windowWidth, setWindowWidth] = useState(window.innerWidth);

    const outside = useRef();

    const handleClickOutside = (e) => {
        if( outside.current && outside.current == e.target){
            window.confirm("변경 내용이 저장되지 않습니다. 정말로 나가시겠습니까?") && setModal(false);
        }
    }

    // resizing
    useEffect(() => {
        let timerId;

        function handleResize() {
        clearTimeout(timerId);
        timerId = setTimeout(() => {
            setWindowWidth(window.innerWidth);
        }, 250);
        }

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    // click outside
    useEffect(() => {
        document.addEventListener("click", handleClickOutside, false);
        return () => {
            document.removeEventListener("click", handleClickOutside, false);
        }
    }, []);

    return (
        <div className={`${styles.container}`}>
            { windowWidth >= 768 ? 
                <button 
                    className={`${styles.openBtn}`}
                    onClick={ () => setModal(true) }
                >
                    {iconName === "MdCreate" ? <MdCreate/> : null}
                    <p>{feature}</p>
                </button>
                :
                <button 
                    className={`${styles.openBtn} ${styles.small}`}
                    onClick={ () => setModal(true) }
                >
                    {iconName === "MdCreate" ? <MdCreate/> : null}
                </button>
            }
                
            {modal && 
            <ModalBG ref={outside}>
                <div className={`${styles.modal}`}>
                    <div className={`${styles.ui}`}>
                        <button 
                            className={`${styles.closeBtn}`} 
                            onClick={ () => {
                                window.confirm("변경 내용이 저장되지 않습니다. 정말로 나가시겠습니까?") && setModal(false);
                            }}
                        >x</button>
                    </div>

                    <div className={`${styles.body}`}>
                        { children }
                    </div>
                </div>
            </ModalBG>}
        </div>
        
    );
}

const ModalBG = forwardRef(function ModalBG({ children }, ref) {
    return (
        <div 
            ref={ref}
            className={`${styles.background}`}
        >
            { children }
        </div>
    );
});
