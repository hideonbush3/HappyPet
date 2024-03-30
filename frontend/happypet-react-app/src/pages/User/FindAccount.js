import style from './FindAccount.module.css';
import { BsKey, BsPerson } from "react-icons/bs";


export default function FindAccount() {
    const goToHomePage = () =>{
        window.location.href = '/';
    }
    const goToIdPage = () => {
        window.location.href = '/user/findaccount/id';
    };

    const goToPasswordPage = () => {
        window.location.href = '/user/findaccount/password';
    };

    return (
        <div className={style.container}>
            <div className={style.logo} onClick={goToHomePage}>Happy Pet</div>
            <div className={style.guide}>무엇을 할까요?</div>
            <div className={style.find_btn} onClick={goToIdPage}>
                <p className={style.icon}><BsPerson/></p>
                <p className={style.find_text}>아이디 찾기</p>
            </div>
            <div className={style.find_btn} onClick={goToPasswordPage}>
                <p className={style.icon}><BsKey/></p>
                <p className={style.find_text}>비밀번호 찾기</p>
            </div>
        </div>
    );
}
