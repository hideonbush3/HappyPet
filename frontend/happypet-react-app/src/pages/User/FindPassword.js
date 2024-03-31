import style from './FindPassword.module.css';
import { BsPerson, BsKey } from "react-icons/bs";
import { useState, useEffect} from 'react';
import { call } from '../../service/ApiService';
import ToastMessage from '../../component/ToastMessage';
export default function FindPassword(){
    const process = '처리중입니다..<br/>잠시만 기다려주세요';

    const [userId, setUserId] = useState('');
    const [showToastMessage, setShowToastMessage] = useState(false);
    const [timer, setTimer] = useState(300);
    const [authCodeCreatedDate, setAuthCodeCreatedDate] = useState('');
    const [transmissionBtnText, setTransmissionBtnText] = useState('인증코드 전송');
    const [authCode, setAuthCode] = useState('');
    const [email, setEmail] = useState('');
    
    useEffect(() => {
        let countdown;

        const startTimer = () => {
            countdown = setInterval(() => {
                setTimer((prevTimer) => {
                    if (prevTimer === 0) {
                        clearInterval(countdown);
                        return 0;
                    }
                    return prevTimer - 1;
                });
            }, 1000);
        };

        if (authCodeCreatedDate.length > 0) {
            startTimer();
        }

        // 언마운트될때 타이머 clean up
        return () => {
            clearInterval(countdown);
        };
    }, [authCodeCreatedDate]);

    const formatTime = (time) => {
        const minutes = Math.floor(time / 60);
        const seconds = time % 60;
        return `${minutes}:${seconds < 10 ? `0${seconds}` : seconds}`;
    };


    const changeUserIdHandler = (e) => {
        setUserId(e.target.value);
    }

    const changeAuthCodeHandler = (e) =>{
        setAuthCode(e.target.value);
    }
    
    const usernameRegex = /^[a-z0-9]{6,40}$/;

    const transmissionHandler = () => {
        if(userId.trim().length === 0){
            alert('아이디를 입력하세요.');
            return;
        }
        if(!usernameRegex.test(userId)){
            alert('아이디 형식이 올바르지 않아서 존재할 수 없는 아이디입니다.');
            return;
        }
        call(`/user/checksignup/id?userId=${userId}`, 'GET')
        .then((res) => {
            if(res.message === '존재한다'){
                
                // 인증코드 재전송일 경우 인증코드가 만들어진 날짜를 param으로 넣는다.
                const params = transmissionBtnText === '인증코드 전송' ? '' : `?createdDate=${authCodeCreatedDate}`;
                setShowToastMessage(true);
                call(`/auth-code/create${params}`, 'POST', {
                    userId: userId,
                    title: 'HappyPet 비밀번호 찾기 인증코드',
                    body: '인증코드는 %s 입니다.\n비밀번호 찾기 페이지로 돌아가세요.'
                })
                .then((res) => {
                    if(res.object !== null){
                        setShowToastMessage(false);
                        alert('이메일로 인증코드를 보냈습니다.\n인증코드의 유효기간은 5분입니다.');
                        setTimer(300);
                        setAuthCodeCreatedDate(res.object.createdDate);
                        setEmail(res.object.email);
                        setTransmissionBtnText('인증코드 재전송');
                    }else if(res.error !== null){
                        alert('알수없는 에러가 발생했습니다.\n재시도 하시거나 관리자에게 문의하세요.')
                    }
                })
            }else if(res.message === '존재하지않음'){
                alert('존재하지 않는 아이디입니다.');
                return;
            }else{
                alert('알수없는 에러가 발생했습니다.\n재시도 하시거나 관리자에게 문의하세요.');
                return;
            }
        })
    }

        const checkAuthCode = () => {
            if(authCode.trim().length === 0){
                alert('인증코드를 입력하세요');
                return;
            }
            setShowToastMessage(true);
            call(`/auth-code/check?process=password`, 'DELETE', {
                email: email,
                authCode: authCode
            })
            .then((res) => {
                setShowToastMessage(false);
                if(res.message === '유효시간종료'){
                    alert('만료된 인증코드입니다.\n재전송 하세요.');
                    return;
                }else if(res.error !== null){
                    alert('알수없는 에러가 발생했습니다\n재전송하거나 관리자에게 문의하세요.');
                    return;
                }else if(res.message === '틀린인증코드'){
                    alert('인증코드가 일치하지 않습니다.');
                    return;    
                }              
                else if(res.message === '인증성공'){
                    alert('이메일로 새로운 비밀번호를 보냈습니다.\n로그인 페이지로 이동합니다.');
                    window.location.href='/user/login';
                }
            })
        };
    
    return(
        <div className={style.container}>
            <div className={style.logo}>Happy Pet</div>
            <div className={style.guide}>가입했던 계정의 이메일로 인증코드를 전송합니다.</div>
            <div className={style.id_container}>
                <p className={style.icon}><BsPerson/></p>
                <input className={style.id} placeholder='아이디를 입력하세요' onChange={changeUserIdHandler}></input>
            </div>
            {authCodeCreatedDate && (
                <>
                <span style={{ color: timer === 60 ? 'red' : 'initial' }} 
                    className={style.timer}>{timer > 0 ? '남은시간 ' + formatTime(timer) : '인증코드가 만료됐습니다 재전송 하세요'}
                </span>
                <div className={style.id_container}>
                    <p className={style.icon}><BsKey/></p>
                    <input className={style.password} placeholder='인증코드를 입력하세요' type='password' onChange={changeAuthCodeHandler}></input>
                </div>
                </>
            )}

            {authCodeCreatedDate && (
                <div className={style.send_authcode_btn} onClick={checkAuthCode}>
                    인증코드 확인
                </div>
            )}
            <div className={style.send_authcode_btn} onClick={transmissionHandler}>
                {transmissionBtnText}
            </div>
            <ToastMessage 
                show={showToastMessage}
                process={process}/>
        </div>
    )
}