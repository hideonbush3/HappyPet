import { API_BASE_URL } from "../api-config";

export function call(api, method, request){
    let headers = new Headers({
        "Content-Type": "application/json",
    });

    const accessToken = localStorage.getItem("token");
    if(accessToken && accessToken!==null){
        headers.append("Authorization", "Bearer " + accessToken);
    }

    let options = {
        headers: headers,
        url: API_BASE_URL + api,
        method: method,
    };
    if(request) {
        options.body = JSON.stringify(request);
    }
    return fetch(options.url, options).then((res) => {
        if(res.status === 200) {
            return res.json();
        }else if(res.status === 403){
            window.location.href = "/user/login";
        }else{
            return res.json();
        }
    })
    .catch(error => {
        throw error;
    })
}

// 로그인
export function signin(userDTO){
    return call("/user/signin", "POST", userDTO)
        .then((res) => {
            if(res.object !== null){
                localStorage.setItem("token", res.object.token);
                window.location.href = "/"
            }else{
                if(res.error === '아이디') alert('해당 아이디와 일치하는 회원은 없습니다.');
                else alert('비밀번호를 다시 확인하세요.');
            }
        });
}

// 로그아웃
export function signout(){
    localStorage.setItem("token", null);
    window.location.href = "/login";
}

// 회원가입
export function signup(userDTO){
    return call("/user/signup", "POST", userDTO);
}