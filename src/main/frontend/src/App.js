import './App.css';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import Root from './pages/Root';
import Login from './pages/Login';
import Main from './pages/Main';
import Board from './pages/Board';
import TimeTable from './pages/TimeTable';
import NotFound from './pages/NotFound';
import Register from './pages/Register';
import Auth from "./pages/Auth";
import StudentRegister from "./components/register/StudentRegister";
import TeacherRegister from "./components/register/TeacherRegister";

const router = createBrowserRouter([
  {
    path: '/',
    element: <Login />,
    errorElement: <NotFound />,
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
    path : '/auth',
    element : <Auth />,
    errorElement : <NotFound />,
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
