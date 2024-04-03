import React, { useEffect, useState } from "react"; 
import style from './Join.module.css';
import { call, signup } from "../../service/ApiService";
import ToastMessage from "../../component/ToastMessage";

function Join(props){
    const process = props.title === 'join' ? '회원가입' : '내 정보 수정';
    const btn = process === '회원가입' ? '가입하기' : '수정하기';
    const [sessionUser, setSessionUser] = useState();


    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [repassword, setRepassword] = useState('');
    const [nickname, setNickname] = useState('');
    const [email1, setEmail1] = useState('');
    const [email2, setEmail2] = useState('');
    
    const [authCode, setAuthCode] = useState('');
    const [authCodeCreatedDate, setAuthCodeCreatedDate] = useState('');
    const [showAuthCodeInput, setShowAuthCodeInput] = useState(false);
    const [authCodeBtnText, setAuthCodeBtnText] = useState(props.title === 'join' ? '이메일 인증코드 전송' : '다른 메일로 인증하기');
    const [showToastMessage, setShowToastMessage] = useState(false);
    const [timer, setTimer] = useState(300);
    const [verifiedEmail, setVerifiedEmail] = useState('');


    const onChangeHandler = (e, type) => {
        e.preventDefault();
        switch(type){
            case 'username': setUsername(e.target.value);       break; 
            case 'password': setPassword(e.target.value);       break;
            case 'repassword': setRepassword(e.target.value);   break;
            case 'nickname': setNickname(e.target.value);       break;
            case 'email1': setEmail1(e.target.value);           break;
            case 'authcode': setAuthCode(e.target.value);       break;
            default: setEmail2(e.target.value);  break;  
        }
    }

    // validation
    const usernameRegex = /^[a-z0-9]{6,40}$/;
    const passwordRegex = /^[a-z0-9!@#$%^&*]{8,20}$/;
    const nicknameRegex = /^[a-zA-Z가-힣]{1,20}$/;
    const emailSuffixRegex = /^[a-z]{1,}[.][a-z]{1,}$/;

    const [emailOption, setEmailOption] = useState('직접입력');
    const [cantWrite, setCantWrite] = useState(false);
    
    useEffect(() => {
        if(process === '내 정보 수정'){
            call('/user', 'GET', null)
            .then((res) => {
                if(res.object !== null){
                    setSessionUser(res.object);
                    setUsername(res.object.username);
                    setPassword(res.object.password);
                    setRepassword(res.object.password);
                    setNickname(res.object.nickname);
                    const email = (res.object.email).split('@');
                    setEmail1(email[0]);
                    setEmail2(email[1]);
                    setVerifiedEmail(res.object.email);
                }else if(res.error !== null){
                    alert('유저 정보를 불러오다 실패했습니다.\n관리자에게 문의하세요.');
                    return;
                }
            })
        }
    }, []);

    const handleEmailChange = (e) => {
        e.preventDefault();
        if(e.target.value === '직접입력'){
            setCantWrite(false);
            setEmailOption(e.target.value);
        }else {
            setCantWrite(true);
            setEmailOption(e.target.value);
            setEmail2(e.target.value);
        }
        
    };

    useEffect(() => {
        const dom = document.getElementById("email2");
        if(emailOption === "직접입력"){
            dom.value = "";
            dom.placeholder = "직접 입력하세요";
        }else {
            dom.value = emailOption;
        }
    }, [emailOption])

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
            // authCodeCreatedDate 값이 있을 때만 타이머 시작
            startTimer();
        }

        return () => {
            clearInterval(countdown);
        };
    }, [authCodeCreatedDate]);

    const formatTime = (time) => {
        const minutes = Math.floor(time / 60);
        const seconds = time % 60;
        return `${minutes}:${seconds < 10 ? `0${seconds}` : seconds}`;
    };
    
    const requestAuthCode = (e, anotherEmailAuth) => {
        e.preventDefault();

        if(anotherEmailAuth === true){
            setAuthCodeBtnText('이메일 인증코드 전송');
            setAuthCode('');
            setVerifiedEmail('');
            return;
        }

        if(email1 === null || email2 ==="직접입력"){
            alert("이메일을 입력하세요.");
            return;
        }

        if(!emailSuffixRegex.test(email2)){
            alert("이메일형식이 맞지않습니다.")
            return;
        }

        const email = email1 + '@' + email2;
        call(`/user/checksignup/email?email=${email}`, 'GET')
        .then((res) => {
            if(res.message === '존재하는이메일'){
                alert('이미 가입한 이메일 입니다.\n하나의 이메일로 중복가입 할 수 없습니다.');
                return;
            }else if(res.error !== null){
                alert('시스템상의 문제가 발생했습니다.\n재전송하시고도 문제가 발생하면 관리자에게 문의하세요.');
                return;
            }

            setShowToastMessage(true);
            const params = authCodeBtnText === '이메일 인증코드 전송' ? '' : `?createdDate=${authCodeCreatedDate}`;
            call(`/auth-code/create${params}`, 'POST', {
                email: email,
                title: 'HappyPet 이메일 인증코드',
                body: '인증코드는 %s 입니다.\n회원가입으로 돌아가세요.'
            })
            .then((res) => {
                return res.object;
            })
            .then((object) => {
                setAuthCodeCreatedDate(object.createdDate);
                setShowAuthCodeInput(true);
            })
            .finally(() => {
                setAuthCodeBtnText('인증코드 재전송');
                setShowToastMessage(false);
                setTimer(300);
            });
        });
    }

    const verifyAuthCode = () => {
        const email = email1 + '@' + email2;
        call('/auth-code/check', 'DELETE', {
            email: email,
            authCode: authCode})
        .then((res) => {
            return res.message;
        })
        .then((msg) => {
            if(msg === '인증성공') {
                alert('이메일 인증을 완료했습니다.');
                setShowAuthCodeInput(false);
                setAuthCodeBtnText('다른 메일로 인증하기');
                setVerifiedEmail(email);
            }else if(msg === '유효시간종료') {
                alert('이미 만료된 인증코드입니다.\n인증코드를 재전송하세요.');
                return;
            }else if(msg === '틀린인증코드'){
                alert('인증코드가 올바르지 않습니다.');
                return;
            }else{
                alert('알수없는 에러가 발생했습니다\n재시도하거나 관리자에게 문의하세요.');
                return;
            }
        })
    }
    const handleSubmit = (e) => {
        e.preventDefault();

        if (!usernameRegex.test(username)) {
            alert("아이디는 영소문자, 숫자조합 6~40자")
            return;
        }
    
        if(sessionUser === undefined){
            if (!passwordRegex.test(password)) {
                alert("비밀번호는 영소문자, 숫자, 특수문자 조합(8~20)자")
                return;
            }
        }

        if(sessionUser !== undefined && sessionUser.password !== password){
            if (!passwordRegex.test(password)) {
                alert("비밀번호는 영소문자, 숫자, 특수문자 조합(8~20)자")
                return;
            }
        }

    
        if (password !== repassword) {
            alert("비밀번호가 일치하지 않습니다.")
            return;
        }
    
        if (!nicknameRegex.test(nickname)) {
            alert("닉네임은 한글, 영문 대소문자 20자이내이며 공백은 허용하지 않습니다.");
            return;
        }

        if(verifiedEmail.length === 0) {
            alert('이메일 인증을 완료해야합니다.');
            return;
        }

        if(sessionUser === undefined){
            signup({
                    username: username,
                    password: password,
                    nickname: nickname,
                    email: email1 + '@' + email2
            }).then((res) => {
                if(res.message === '아이디 중복') {
                    alert("이미 존재하는 아이디입니다.");
                    return;
                }
                else if(res.message === '닉네임 중복'){
                    alert("이미 존재하는 닉네임입니다.");
                    return;  
                }else if(res.error !== null){
                    alert('알수없는 에러가 발생했습니다.\n재시도하거나 관리자에게 문의하세요.');
                } 
                else window.location.href = "/user/login";
            });
        }else{
            const token = localStorage.getItem('happypetToken');
            if(token == null){
                alert('세션이 만료됐습니다.\n다시 로그인하세요.');
                window.location.href='/user/login';
            }
            call('/user/modify', 'PUT', {
                username: username,
                password: password,
                nickname: nickname,
                email: email1 + '@' + email2
            })
            .then((res) => {
                if(res.message === '아이디중복'){
                    alert("이미 존재하는 아이디입니다.");
                    return;
                }
                else if(res.message === '닉네임중복'){
                    alert("이미 존재하는 닉네임입니다.");
                    return;
                }
                else{
                    alert("회원님의 정보를 수정했습니다.");
                    window.location.href = '/user/mypage/myinfo';
                }
            });
        }
    }
    
    return(
        <div className={`${style.body}`}>
        <div className={`${style.container}`}>

            <h4>{process}을 진행해주세요.</h4>
            <p>이메일은 아이디/비밀번호 찾기에 사용됩니다.</p>
            <form className="mt-5" onSubmit={handleSubmit}>
                 <div className={`${style.formGroup}`}>
                    <label htmlFor="username">아이디</label>
                    <input type="text" id="username" name="username" placeholder="영소문자, 숫자조합 6~40자" required value={username} onChange={(e) => onChangeHandler(e, 'username')}/>
                </div>
                <div className={`${style.formGroup}`}>
                    <label htmlFor="password">비밀번호</label>
                    <input type="password" id="password" name="password" placeholder="영문, 숫자, 특수문자 조합(8~20)자" required value={password} onChange={(e) => onChangeHandler(e, 'password')}/>
                </div>
                <div className={`${style.formGroup}`}>
                    <label htmlFor="password2">비밀번호 재입력</label>
                    <input type="password" id="password2" name="password2" placeholder="비밀번호를 한번더 입력하세요"required value={repassword} onChange={(e) => onChangeHandler(e, 'repassword')}/>
                </div>
                <div className={`${style.formGroup}`}>
                    <label htmlFor="nickname">닉네임</label>
                    <input type="text" id="nickname" name="nickname" placeholder="한글, 영문 대소문자 조합해서 20자까지 가능" required value={nickname} onChange={(e) => onChangeHandler(e, 'nickname')}/>
                </div>
                <div className={`${style.inputGroup}`}>
                    <label htmlFor="email">이메일</label>
                    <div className={style.inlineInput}>
                        <input type="text" id="email1" name="email1" required placeholder="Example" value={email1} onChange={(e) => onChangeHandler(e, 'email1')}/>
                        <span>@</span>
                        <input type="text" id="email2" name="email2" required readOnly={cantWrite} value={email2} onChange={(e) => onChangeHandler(e, 'email2')}/>
                        <select onChange={handleEmailChange} value={emailOption}>
                            <option>직접입력</option>
                            <option>gmail.com</option>
                            <option>naver.com</option>
                            <option>daum.net</option>
                            <option>kakao.com</option>
                            <option>yahoo.com</option>
                            <option>hotmail.com</option>
                            <option>outlook.com</option>
                        </select>
                    </div>
                </div>
                <div className={style.formGroup} hidden={!showAuthCodeInput}>
                    <label htmlFor="authcode">인증코드를 입력하세요</label>
                    <span style={{ color: timer === 60 ? 'red' : 'initial' }}>
                        {timer > 0 ? '남은시간 ' + formatTime(timer) : '인증코드가 만료됐습니다 재전송 하세요'}
                    </span>
                    <div className={style.authcode}>
                        <input type='password' id="authcode" value={authCode} 
                                onChange={(e) => onChangeHandler(e, 'authcode')}/>
                        <button onClick={verifyAuthCode}>인증하기</button>
                    </div>
                </div>
                <div>
                    <button className='mb-2' style={{width: '100%'}} 
                            onClick={(e) => requestAuthCode(e, authCodeBtnText === '다른 메일로 인증하기' ? true : false)} >
                            {authCodeBtnText}
                    </button>
                </div>

                <div>
                    <button className={style.submit_btn}
                            style={{width: '100%'}}
                            type="submit">{btn}
                    </button>
                </div>
            </form>

        </div>
        <ToastMessage 
            show={showToastMessage}
            process='인증코드 보내는중..<br />잠시만 기다려주세요'/>
        </div>
     
    )
}

export default Join;