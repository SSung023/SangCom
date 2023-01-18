import './App.css';
import { BrowserRouter, createBrowserRouter, RouterProvider, Routes, Route } from 'react-router-dom';

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
      { path: 'board', element: <Board />},
      { path: 'timetable', element: <TimeTable />},
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
    path: '/studentregister',
    element : <PublicRoute component={<StudentRegister/>} restricted={true}/>,
    errorElement : <NotFound/>,
  },
  {
    path: '/teacherregister',
    element : <PublicRoute component={<TeacherRegister/>} restricted={true}/>,
    errorElement : <NotFound/>,
  },
]);


// const router = createBrowserRouter(
//   <Routes>
//     <Route 
//       path='/' 
//       element={<PrivateRoute component={<Root/>}/>}
//       children={[
//         { index: true, element: <Main /> } ,
//         { path: 'board', element: <Board />},
//         { path: 'timetable', element: <TimeTable />},
//       ]}
//     />
//     <Route
//       path='/login'
//       element={<PublicRoute component={<Login/>} restricted/>}
//     />
//     <Route
//       path='/register'
//       element={<PublicRoute component={<Register/>} restricted/>}
//     />
//     <Route
//       path='/studentregister'
//       element={<PublicRoute component={<StudentRegister/>} restricted/>}
//     />
//     <Route
//       path='/teacherregister'
//       element={<PublicRoute component={<TeacherRegister/>} restricted/>}
//     />
//     <Route
//       path='/auth'
//       element={<PublicRoute component={<Auth/>} restricted/>}
//     />
//   </Routes>
// );


function App() {
  return (
    <>
      <RouterProvider router={router}/>
    </>
  );
}

//<RouterProvider router={router}/>
export default App;
