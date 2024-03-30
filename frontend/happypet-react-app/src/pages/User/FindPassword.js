import style from './FindPassword.module.css';
import { BsPerson } from "react-icons/bs";

export default function FindPassword(){
    
    return(
        <div className={style.container}>
            <div className={style.logo}>Happy Pet</div>
            <div className={style.guide}>가입했던 계정의 이메일로 인증코드를 전송합니다.</div>
            <div className={style.id_container}>
                <p className={style.icon}><BsPerson/></p>
                <input className={style.id} placeholder='아이디를 입력하세요'></input>
            </div>
            <div className={style.send_authcode_btn}>
                인증코드 전송
            </div>
        </div>
    )
}