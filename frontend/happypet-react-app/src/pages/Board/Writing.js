import { useState } from 'react';
import style from './Writing.module.css';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from "../../api-config";
export default function Writing(){
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [urlAndName, setUrlAndName] = useState(new Map());
    const [imageFiles, setImageFiles] = useState([]);
  
    const cancelEventHandler = () => {
        window.location.href = '/board';
    }

    const handleTitleChange = (e) => {
      setTitle(e.target.value);
    };
  
    const handleContentChange = (e) => {
        const content = document.getElementById('content');
        setContent(content.innerHTML);
    };

    // 파일첨부 후 본문작성란에 포커싱
    const focusContentInput = () => {
        const contentField = document.getElementById('content');
        contentField.focus({preventScroll: true});
    }
  
    const handleImageUpload = (e) => {
        const files = e.target.files;
        const originalMap = new Map(urlAndName);
        const originalImages = imageFiles;

        if(!!files){
            for(const file of files){
                const reader = new FileReader();
                reader.addEventListener('load', (e) => {
                    focusContentInput();

                    const img = document.createElement('img');
                    img.src = reader.result;
                    document.execCommand('insertHTML', false, `<br>${img.outerHTML}<br>`);

                    originalMap.set(img.src, file.name);
                    originalImages.push(file);
                });
                reader.readAsDataURL(file);
            }
        }
        setUrlAndName(originalMap);
        setImageFiles(originalImages);
        e.target.value = null;
    }

    const handleSubmit = () => {
        if(title.trim() === ''){
            alert('제목을 입력하세요');
            return;
        }
        if(content.trim() === ''){
            alert('내용을 입력하세요');
            return;
        }
        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', content);

        if(urlAndName.size !== 0){
            imageFiles.forEach((file) => {
                formData.append('images', file);
            });
            // Map => Json문자열 형변환
            const result = JSON.stringify(Object.fromEntries(urlAndName));
            formData.append('urlAndName', result);
        }


        const options = {
            url: API_BASE_URL + '/post',
            method: 'POST',
            body: formData,
            headers: {
                "Authorization": "Bearer " + localStorage.getItem('token'),
            }
        };

        return fetch(options.url, options).then((res) => {
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
                alert('세션이 만료됐습니다.\n재로그인하세요.');
                localStorage.setItem('token', null);
                window.location.href = '/user/login';
            }else{
                alert('페이지를 새로고침한 후 재작성하세요.\n그래도 작성이 안된다면 관리자에게 문의하세요.')
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
                <div id='content'
                    className={style.content} 
                    contentEditable='true'
                    value={content}
                    onInput={handleContentChange}>
                </div>
            </div>
            <div className={style.btn}>
                    <button onClick={cancelEventHandler}>취소</button>
                    <button onClick={handleSubmit}>작성 완료</button>               
            </div>
            <input multiple="multiple" type='file' id='imageInput' accept='image/*' onChange={handleImageUpload}/>

        </div>
        </div>

    )
}