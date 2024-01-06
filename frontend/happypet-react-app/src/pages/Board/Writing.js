import { useState } from 'react';
import style from './Writing.module.css';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from "../../api-config";
export default function Writing(){
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
  
    const cancelEventHandler = () => {
        window.location.href = '/board';
    }

    const handleTitleChange = (e) => {
      setTitle(e.target.value);
    };
  
    const handleContentChange = (e) => {
      setContent(e.target.value);
    };

  
    const handleSubmit = () => {
        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', content);

        const imageInput = document.getElementById('imageInput');
        for (const file of imageInput.files) {
            formData.append('images', file);
        }

        const accessToken = localStorage.getItem("token");
        const options = {
            url: API_BASE_URL + '/post',
            method: 'POST',
            body: formData,
        };
        if(accessToken && accessToken!==null){
            options.headers = {
                "Authorization": "Bearer " + accessToken,
            };
        }

        return fetch(options.url, options).then((res) => {
            console.log(res);
            if(res.status === 200) {
                return res.json();
            }else if(res.status === 403){
                window.location.href = "/user/login";
            }else{
                return res.json();
            }
        })
        .then((res) => {
            if(res.error === undefined){
                navigate('/board/view', {state: {post: res}});
            }else if(res.error === '존재하지 않는 유저'){
                alert('세션이 만료됐습니다. 재로그인하세요.');
                localStorage.setItem('token', null);
                window.location.href = '/user/login';
            }else{
                alert('이미지 파일만 첨부할 수 있습니다.')
            }
          })
        .catch(error => {
            throw error;
        })
    };
  
    return(
        <div className={style.body}>
        <div className={style.container}>
            <h2>게시글 작성</h2>
            
            <div className={style.postForm}>
                <div className={style.title}>
                    <input 
                        placeholder='제목을 입력하세요'
                        value={title}
                        onChange={handleTitleChange}/>
                </div>
                <div className={style.content}>
                    <textarea 
                        placeholder='내용을 입력하세요'
                        value={content}
                        onChange={handleContentChange}/>
                </div>
            </div>
            <div className={style.btn}>
                    <button onClick={cancelEventHandler}>취소</button>
                    <button onClick={handleSubmit}>작성 완료</button>               
            </div>
            <input multiple="multiple" type='file' id='imageInput'/>

        </div>
        </div>

    )
}