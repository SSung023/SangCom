import './App.css';
import Topnav from "./components/Topnav";
import Container from "./components/Container";

function App() {
  return (
    <>
      <Topnav />
      {/* 나중에 Routing을 위해 leftside와 main을 감싸는 컴포넌트가 하나 더 있어야 함. */}
      <Container />
    </>
  );
}

export default App;
