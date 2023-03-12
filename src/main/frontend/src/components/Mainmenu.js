import React, { useState } from "react";
import { useSelector } from "react-redux";
import { NavLink } from "react-router-dom";
import styles from './Topnav.module.css';

export default function Mainmenu() {
    const role = useSelector((state) => state.loginReducer.user.info.role);
    const grade = useSelector((state) => state.loginReducer.user.info.grade);

    const mainMenus = {
        "게시판": [
            { lnbMenu: '자유 게시판', category: 'free', to: 'board/free' },
            { lnbMenu: '학년별 게시판', category: 'grade1', to: 'board/grade1' },
            { lnbMenu: '학년별 게시판', category: 'grade2', to: 'board/grade2' },
            { lnbMenu: '학년별 게시판', category: 'grade3', to: 'board/grade3' }
        ],
        "학생회": [
            { lnbMenu: "학생회 공지 게시판", category: 'council', to: 'board/council' },
            { lnbMenu: "학생회 건의 게시판", category: 'suggestion', to: 'board/suggestion' }
        ],
        "동아리": [
            { lnbMenu: "동아리 게시판", category: 'club', to: 'board/club' }
        ],
        "시간표": [
            { to: 'timetable' }
        ],
        "급식표": [
            { to: 'mealPage' }
        ]
    }

    return (
        <div className={styles.mainMenu}>
            <ul className={styles.menuUl}>
                {Object.keys(mainMenus).map((mainMenu) => {
                    const lnbMenus = mainMenus[mainMenu];

                    if(mainMenu === "게시판" && role && role.includes("TEACHER")){
                        return;
                    }

                    return (
                        lnbMenus.length === 1 ?   
                        <li key={mainMenu} className={styles.item}>
                            {lnbMenus.map((menu) => {
                                return (
                                <NavLink 
                                    to={menu.to}
                                    style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})}
                                    key={mainMenu}
                                >{mainMenu}</NavLink>);
                            })}
                        </li> :
                        <LNB key={mainMenu} lnbMenus={lnbMenus} mainMenu={mainMenu} grade={grade}/>
                    );
                })}
            </ul>
        </div>
    );
}

function LNB({ lnbMenus, mainMenu, grade }) {
    const [isHide, setHide] = useState(true);
    const onMouseEnter = () => { setHide(false) };
    const onMouseLeave = () => { setHide(true) };

    return (
        <>
            <li 
                key={mainMenu} 
                className={styles.item}
                onMouseEnter={onMouseEnter}
                onMouseLeave={onMouseLeave}
            >
                <NavLink 
                    to={lnbMenus[0].to} 
                    style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})}
                >
                {mainMenu}                                  
                </NavLink>

                {!isHide && <div className={styles.lnb}>
                    <ul className={styles.menuUl}>
                    {lnbMenus.map((menu) => {
                        return (
                            // category가 자유 게시판이 아니라면 학년에 맞는 메뉴가 표시되도록 렌더링
                            
                            menu.lnbMenu !== "학년별 게시판" ?
                            <li key={menu.category}>
                                <NavLink 
                                    to={menu.to}
                                    style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})}
                                    key={menu.category}
                                >{menu.lnbMenu}</NavLink>
                            </li>
                            : 
                            menu.category === `grade${grade}` && 
                            <li key={menu.category}>
                                <NavLink 
                                    to={menu.to}
                                    style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})}
                                    key={menu.category}
                                >{menu.lnbMenu}</NavLink>
                            </li>
                        );
                    })}
                    </ul>
                </div>}
            </li>
        </>
    );
}