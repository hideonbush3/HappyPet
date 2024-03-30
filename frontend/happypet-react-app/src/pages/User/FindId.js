import { useState } from 'react';
import style from './FindId.module.css';
import { AiOutlineMail } from "react-icons/ai";
import { call } from '../../service/ApiService';
import ToastMessage from '../../component/ToastMessage';
export default function FindId(){
    const [showToastMessage, setShowToastMessage] = useState(false);
    const [email, setEmail] = useState('');
    const emailPattern = /^[a-zA-Z0-9]+@[a-z]+\.[a-z]+$/;
    
    const handleChangeEmail = (e) => {
        setEmail(e.target.value);
    }
    const handleIdTransmission = () => {
        if(email.trim().length === 0){
            alert('이메일을 입력하세요');
            return;
        }
        if(!emailPattern.test(email)){
            alert('이메일 형식을 다시 확인하세요');
            return;
        }

        call(`/user/is-exist?email=${email}`, 'GET')
        .then((res) => {
            if(res.message === '존재하지않는이메일'){
                alert('해당 이메일로 가입한 계정은 없습니다.');
                return;
            }
            else if(res.message === '존재하는이메일'){
                setShowToastMessage(true);
                call(`/user/find-id?email=${email}`, 'GET')
                .then((res) => {
                    if(res.message === '전송성공'){
                        setShowToastMessage(false);
                        alert('이메일로 아이디를 전송했습니다')   
                    }else if(res.error !== null){
                        alert('알 수 없는 에러가 발생했습니다.\n재전송 하시거나 관리자에게 문의바랍니다.')
                    }
                })
                
            }
        })
        
    
    }
    
    return(
        <div className={style.container}>
            <div className={style.logo}>Happy Pet</div>
            <div className={style.guide}>가입했던 이메일을 입력하세요</div>
            <div className={style.email_container}>
                <p className={style.icon}><AiOutlineMail/></p>
                <input className={style.email} placeholder='이메일을 입력하세요' onChange={handleChangeEmail} value={email}></input>
            </div>
            <div className={style.send_authcode_btn} onClick={handleIdTransmission}>
                아이디 전송
            </div>

            <ToastMessage 
                show={showToastMessage}
                process='아이디 보내는중..<br />잠시만 기다려주세요'/>
        </div>
    )
}