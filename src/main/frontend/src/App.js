import './App.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
// TODO: 주석처리된 것 삭제하기
//import { KAKAO_AUTH_URL } from './components/login/OAuth';
import Root from './pages/Root';
import Login from './pages/Login';
import Main from './pages/Main';
import Board from './pages/Board';
import TimeTable from './pages/TimeTable';
import NotFound from './pages/NotFound';
import KakaoRedirectHandler from './pages/KakaoRedirectHandler';
import Register from './pages/Register';
import StudentRegister from "./components/register/StudentRegister";
import TeacherRegister from "./components/register/TeacherRegister";

const router = createBrowserRouter([
  {
    path: '/',
    element: <Login />,
    errorElement: <NotFound />,
  },
  {
    path: 'login/oauth2/code/kakao',
    element: <KakaoRedirectHandler />,
  },
  {
    path: '/main',
    element: <Root />,
    errorElement: <NotFound />,
    children: [
     { index: true, element: <Main /> } ,
     { path: 'board', element: <Board />},
     { path: 'timetable', element: <TimeTable />},
    ],
  },
  {
    path: '/register',
    element : <Register/>,
    errorElement : <NotFound/>,
  },
  {
    path: '/studentregister',
    element : <StudentRegister/>,
    errorElement : <NotFound/>,
  },
  {
    path: '/teacherregister',
    element : <TeacherRegister/>,
    errorElement : <NotFound/>,
  },
]);

function App() {
  return (
    <>
      <RouterProvider router={router}/>
    </>
  );
}

export default App;
