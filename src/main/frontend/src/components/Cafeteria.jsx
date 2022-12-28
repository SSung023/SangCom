import React, {Component,useState} from "react";
import './cafeteria.css';

export default function Cafeteria(){
        return(
            <Body/>
        );
}

/*달력 몸통*/
const Body = () => {
    return(
        <div className="body">
            <table className="cafeteria">
                <thead>
                <tr>
                    <th>일요일</th>
                    <th>월요일</th>
                    <th>화요일</th>
                    <th>수요일</th>
                    <th>목요일</th>
                    <th>금요일</th>
                    <th>토요일</th>
                </tr>
                </thead>
                <tbody id="month">
                <tr>
                    <td className="sun"><div className="datenum">27
                        <div className="content"></div>
                    </div></td>
                    <td className="mon"><div className="datenum">28</div></td>
                    <td className="tues"><div className="datenum">29</div></td>
                    <td className="wed"><div className="datenum">30</div></td>
                    <td className="thurs"><div className="datenum">1</div></td>
                    <td className="fri"><div className="datenum">2</div></td>
                    <td className="sat"><div className="datenum">3</div></td>
                </tr>
                <tr>
                    <td className="sun"><div className="datenum">4</div></td>
                    <td className="mon"><div className="datenum">5</div></td>
                    <td className="tues"><div className="datenum">6</div></td>
                    <td className="wed"><div className="datenum">7</div></td>
                    <td className="thurs"><div className="datenum">8</div></td>
                    <td className="fri"><div className="datenum">9</div></td>
                    <td className="sat"><div className="datenum">10</div></td>
                </tr>
                <tr>
                    <td className="sun"><div className="datenum">11</div></td>
                    <td className="mon"><div className="datenum">12</div></td>
                    <td className="tues"><div className="datenum">13</div></td>
                    <td className="wed"><div className="datenum">14</div></td>
                    <td className="thurs"><div className="datenum">15</div></td>
                    <td className="fri"><div className="datenum">16</div></td>
                    <td className="sat"><div className="datenum">17</div></td>
                </tr>
                <tr>
                    <td className="sun"><div className="datenum">18</div></td>
                    <td className="mon"><div className="datenum">19</div></td>
                    <td className="tues"><div className="datenum">20</div></td>
                    <td className="wed"><div className="datenum">21</div></td>
                    <td className="thurs"><div className="datenum">22</div></td>
                    <td className="fri"><div className="datenum">23</div></td>
                    <td className="sat"><div className="datenum">24</div></td>
                </tr>
                <tr>
                    <td className="sun"><div className="datenum">25</div></td>
                    <td className="mon"><div className="datenum">26</div></td>
                    <td className="tues"><div className="datenum">27</div></td>
                    <td className="wed"><div className="datenum">28</div></td>
                    <td className="thurs"><div className="datenum">29</div></td>
                    <td className="fri"><div className="datenum">30</div></td>
                    <td className="sat"><div className="datenum">31</div></td>
                </tr>
                </tbody>
            </table>

        </div>
    );
}

