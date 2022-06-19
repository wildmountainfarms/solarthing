import React from 'react';
import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Home from "./pages/Home";
import Login from "./pages/Login";
import ClassicWithLayout from "./classic/Classic";


function App() {
  return (
    <Router>
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/classic' element={<ClassicWithLayout/>}/>
      </Routes>
    </Router>
  );
}

export default App;
