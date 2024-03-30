import 'bootstrap/dist/css/bootstrap.min.css';
import React from "react";
import { BrowserRouter, Route, Routes, useLocation, useParams } from "react-router-dom";
import App from "./App";

import Login from "./pages/User/Login";
import Join from "./pages/User/Join";

import Header from "./component/Header";
import Footer from "./component/Footer";

import DetailFacility from "./pages/DetailFacility";

import Board from './pages/Board/Board';
import Writing from './pages/Board/Writing';
import View from './pages/Board/View';
import Modify from './pages/Board/Modify';
import MypageRouter from './pages/User/MyPage/MypageRouter';
import FindAccount from './pages/User/FindAccount';
import FindId from './pages/User/FindId';
import FindPassword from './pages/User/FindPassword';

function ModifyRoute() {
    const { title } = useParams();
    return <Join title={title} />;
  }
  
  function AppRouter() {
      return (
        <BrowserRouter>
          <AppContent />
        </BrowserRouter>
      );
    }
  function AppContent() {
    const location = useLocation();

    const shouldShowHeaderAndFooter = !location.pathname.startsWith("/user/findaccount");
  
    return (
      <div>
        {shouldShowHeaderAndFooter && <Header />}
        <Routes>
          <Route path="/" element={<App />} />
          <Route path="/user/login" element={<Login />} />
          <Route path='/user/findaccount' element={<FindAccount />} />
          <Route path="/user/findaccount/id" element={<FindId />} />
          <Route path="/user/findaccount/password" element={<FindPassword />} />
          <Route path="/user/:title" element={<ModifyRoute />} />
          <Route path="/user/mypage/*" element={<MypageRouter />} />
          <Route path="/facility/detail" element={<DetailFacility />} />
          <Route path="/board" element={<Board />} />
          <Route path="/board/write" element={<Writing />} />
          <Route path='/board/view' element={<View />} />
          <Route path='/board/view/modify' element={<Modify />} />
        </Routes>
        {shouldShowHeaderAndFooter && <Footer />}
      </div>
    );
}

export default AppRouter;