import React from 'react';
import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Home from "./pages/Home";
import Login from "./pages/Login";
import ClassicWithLayout from "./classic/Classic";
import Authorized from './pages/Authorized';
import GraphQLView from './pages/GraphQLView';


function App() {
  return (
    <Router>
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/authorized' element={<Authorized/>}/>
        <Route path='/classic' element={<ClassicWithLayout/>}/>
        <Route path='/graphql-view' element={<GraphQLView/>}/>
      </Routes>
    </Router>
  );
}

export default App;
