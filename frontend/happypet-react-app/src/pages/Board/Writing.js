import { useEffect, useState } from 'react';
import style from './Writing.module.css';
import { useNavigate } from 'react-router-dom';
import { API_BASE_URL } from "../../api-config";
export default function Writing(){
    const navigate = useNavigate();
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [urlAndFile, setUrlAndFile] = useState(new Map());
  
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

    // 파일첨부 후 본문작성란에 포커싱 => 포커싱된 곳에 이미지 렌더링
    const focusContentInput = () => {
        document.getElementById('content').focus({preventScroll: true});
    }
    
    // 본문 입력마다 내용을 추적했을때 첨부했던 이미지를 삭제했는지 파악하고
    // 삭제했을경우 요청할때 보낼 데이터를 업데이트하기 위함
    useEffect(() => {
        detachImage();
    }, [content])
    const detachImage = () => {
        const images = document.getElementById('content').querySelectorAll('img');
        if(images.length === 0) return;
        const newMap = new Map();
        images.forEach((image) => {
            if(urlAndFile.has(image.src)){
                newMap.set(image.src, urlAndFile.get(image.src));
            }
        });

        setUrlAndFile(newMap);
    }

    const handleImageUpload = (e) => {
        const files = e.target.files;
        const originalUrlAndFile = new Map(urlAndFile);

        if(!!files){
            for(const file of files){
                focusContentInput();

                const img = document.createElement('img');
                img.src = URL.createObjectURL(file);
                document.execCommand('insertHTML', false, `<br>${img.outerHTML}<br>`);

                originalUrlAndFile.set(img.src, file);
            }
        }
        setUrlAndFile(originalUrlAndFile);
        e.target.value = null;
    }

    const handleSubmit = () => {
        if(title.trim() === ''){
            alert('제목을 입력하세요');
            return;
        }

        const input = document.getElementById('content');
        const text = input.textContent.trim();
        const imgs = input.querySelectorAll('img');
        if(text === '' && imgs.length === 0){
            alert('내용을 입력하세요');
            return;
        }

        const formData = new FormData();
        formData.append('title', title);
        formData.append('content', content);

        if(urlAndFile.size > 0){
            const files = Array.from(urlAndFile.values());
            files.forEach((file) => {
                formData.append('images', file);
            });
            // Map => Json문자열 형변환
            const urlAndName = new Map();
            urlAndFile.forEach((value, key) => {
                urlAndName.set(key, value.name);
            })

            const result = JSON.stringify(Object.fromEntries(urlAndName));
            formData.append('urlAndName', result);
        }

        const options = {
            url: API_BASE_URL + '/post',
            method: 'POST',
            body: formData,
            headers: {
                "Authorization": "Bearer " + localStorage.getItem('happypetToken'),
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
                localStorage.setItem('happypetToken', null);
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