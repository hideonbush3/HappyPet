import React from "react";
import { Link } from "react-router-dom";
import style from './Join.module.css';
import { signin } from "../../service/ApiService";

function Login() {
    function handleSubmit(e) {
        e.preventDefault();
        const data = new FormData(e.target);
        const username = data.get("username");
        const password = data.get("password");
        if(username == null){
            alert("아이디를 입력하세요");
            return;
        }

        if(password == null){
            alert("비밀번호를 입력하세요");
            return;
        }

        signin({username: username, password: password});
    }
    return(
        <div className={`${style.body}`}>
        <div className={`${style.container}`}>

            <h4>로그인</h4>
            <form className="mt-5" onSubmit={handleSubmit}>
                 <div className={`${style.formGroup}`}>
                    <label htmlFor="username">아이디</label>
                    <input type="text" id="username" name="username" placeholder="아이디를 입력하세요" required/>
                </div>
                <div className={`${style.formGroup}`}>
                    <label htmlFor="password">비밀번호</label>
                    <input type="password" id="password" name="password" placeholder="비밀번호를 입력하세요" required/>
                </div>
                <div>
                    <button className={style.submit_btn} style={{width: '100%'}}type="submit">로그인</button>
                </div>
            </form>
            <div className="mt-3">
                <Link to="/user/join" className={`${style.joinLink}`}>
                    계정이 없습니까? 여기서 가입하세요.
                </Link>
            </div>
            <div className="mt-2">
                <Link to="/user/findaccount" className={`${style.joinLink}`}>
                    계정정보를 잊으셨나요?
                </Link>
            </div>

        </div>
        </div>
     
    )
}

export default Login;
