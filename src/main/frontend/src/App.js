import './App.css';
import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';

import Root from './pages/Root';
import Login from './pages/Login';
import Main from './pages/Main';
import Board from './pages/Board';
import TimeTable from './pages/TimeTable';
import MealOfMonth from './pages/MealOfMonth';
import NotFound from './pages/NotFound';
import Register from './pages/Register';
import Auth from "./pages/Auth";
import StudentRegister from "./components/register/StudentRegister";
import TeacherRegister from "./components/register/TeacherRegister";
import PrivateRoute from './lib/PrivateRoute';
import PublicRoute from './lib/PublicRoute';

const router = createBrowserRouter([
  {
    path: '/login',
    element: <PublicRoute component={<Login/>} restricted={true}/>,
    errorElement: <NotFound />,
  },
  {
    path: '/',
    element: <PrivateRoute component={<Root/>}/>,
    errorElement: <NotFound />,
    children: [
      { index: true, element: <Main /> } ,
      { path: 'board/free', element: <Board />},
      { path: 'board/council', element: <Board />},
      { path: 'board/club', element: <Board />},
      { path: 'timetable', element: <TimeTable />},
      { path: 'mealPage', element: <MealOfMonth />},
    ],
  },
  {
    path: '/register',
    element : <PublicRoute component={<Register/>} restricted={true}/>,
    errorElement : <NotFound/>,
  },
  {
    path : '/auth',
    element : <PublicRoute component={<Auth/>} restricted={true}/>,
    errorElement : <NotFound />,
  },
  {
    path: '/register/student',
    element : <PublicRoute component={<StudentRegister/>} restricted={true}/>,
    errorElement : <NotFound/>,
  },
  {
    path: '/register/teacher',
    element : <PublicRoute component={<TeacherRegister/>} restricted={true}/>,
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

//<RouterProvider router={router}/>
export default App;
