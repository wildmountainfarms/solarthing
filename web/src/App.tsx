import React from 'react';
import './App.css';
import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Home from "./pages/Home";
import Login from "./pages/Login";
import ClassicWithLayout from "./classic/Classic";
import Authorized from './pages/Authorized';
import GraphQLView from './pages/GraphQLView';
import GraphQLQuery from "./pages/GraphQLQuery";
import Status from "./pages/Status";


function App() {
  return (
    <Router>
      <Routes>
        <Route path='/' element={<Home/>}/>
        <Route path='/login' element={<Login/>}/>
        <Route path='/authorized' element={<Authorized/>}/>
        <Route path='/classic' element={<ClassicWithLayout/>}/>
        <Route path='/status' element={<Status/>}/>
        <Route path='/graphql-view' element={<GraphQLView/>}/>
        <Route path='/graphiql' element={<GraphQLQuery/>}/>
      </Routes>
    </Router>
  );
}

export default App;
