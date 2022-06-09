import React from 'react';
import './App.css';
import {graphQLClient} from "./client";
import {useHomeQuery} from "./generated/graphql";
import {BrowserRouter as Router, Link, Route, Routes} from 'react-router-dom';
import Home from "./pages/Home";
import Login from "./pages/Login";
import Legacy from "./legacy/Legacy";


function App() {
  return (
    <Router>
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/legacy' element={<Legacy/>}/>
      </Routes>
    </Router>
  );
}

export default App;
