import React, {Component} from "react";
import { NavLink } from "react-router-dom";
import styles from './Topnav.module.css';

class Mainmenu extends Component{
    render() {
        const mainMenus = {
            "게시판": [
                { lnbMenu: '자유 게시판', category: 'free', to: 'board/free' },
                { lnbMenu: '1학년 게시판', category: 'grade1', to: 'board/grade1' },
                { lnbMenu: '2학년 게시판', category: 'grade2', to: 'board/grade2' },
                { lnbMenu: '3학년 게시판', category: 'grade3', to: 'board/grade3' }
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
                {/* <ul>
                    <li>
                        <NavLink 
                            to='board/free' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})} 
                            className={styles.item}
                        >게시판</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='board/council' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})} 
                            className={styles.item}
                        >학생회</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='board/club' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})} 
                            className={styles.item}
                        >동아리</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='timetable' 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})} 
                            className={styles.item}
                        >시간표</NavLink>
                    </li>
                    <li>
                        <NavLink 
                            to='mealPage' 
                            className={styles.item} 
                            style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})} 
                        >급식표</NavLink>
                    </li>
                </ul> */}
                <ul>
                    {Object.keys(mainMenus).map((mainMenu) => {
                        const lnbMenus = mainMenus[mainMenu];

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
                            <li key={mainMenu} className={styles.item}>
                                <LNB lnbMenus={lnbMenus} mainMenu={mainMenu}/>                                    
                            </li>
                        );
                    })}
                </ul>
            </div>
        );
    }
}

function LNB({ lnbMenus, mainMenu }) {
    return (
        <>
            <div>
                {mainMenu}
            </div>
            {/* <NavLink 
                to={lnbMenus.to} 
                style={({isActive}) => ({ color: isActive ? 'var(--blue-color)' : ''})}
            >{mainMenu}
            </NavLink> */}
            <div className={styles.lnbMenu}>
                {lnbMenus.map((lnbMenu) => {
                    // console.log(lnbMenu);
                })}
            </div>
        </>
    );
}

export default Mainmenu;