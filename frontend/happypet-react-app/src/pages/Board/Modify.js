import { useEffect, useState } from 'react';
import style from './Writing.module.css';
import { call } from '../../service/ApiService';
import { useLocation, useNavigate } from 'react-router-dom';
export default function Modify(){
    const location = useLocation();
    const post = location.state.post;
    const navigate = useNavigate();
    const [urlAndFile, setUrlAndFile] = useState(new Map());

    const [title, setTitle] = useState(post.title);
    const [content, setContent] = useState(post.content);
  
    // 첫 렌더링시 원래내용 표시
    useEffect(() => {
        const contentContainer = document.getElementById('content');
        contentContainer.innerHTML = post.content;
    }, [])  

    const cancelEventHandler = () => {
        navigate('/board/view', {state: {post: post}});
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

    const handleSubmit = (e) => {
        e.preventDefault();
        if(post.title === title && post.content === content){
            alert("변경된 내용이 없습니다");
            return;
        }else if(title === ''){
            alert('제목을 입력하세요');
            return;
        }

        const input = document.getElementById('content');
        const text = input.textContent.trim();
        const images = input.querySelectorAll('img');

        if(text === '' && images.length ===0){
            alert('내용을 입력하세요');
            return;
        }

      call('/post/modify', 'PUT', {id: post.id, title: title, content: content})
      .then((res) => {
        if(res === undefined || res === null){
            alert("게시글 수정실패");
        }else{
            navigate("/board/view", {state: {post: res}});
        }
      })
    };
  
    return(
        <div className={style.body}>
        <div className={style.container}>
            <h2>게시글 수정</h2>
            
            <div className={style.postForm}>
                <div className={style.title}>
                    <input 
                        placeholder='제목을 입력하세요'
                        value={title}
                        onChange={handleTitleChange}/>
                </div>
                <div id='content'
                    contentEditable='true'
                    className={style.content}
                    onInput={handleContentChange}>
                </div>
            </div>
            <div className={style.btn}>
                    <button onClick={cancelEventHandler}>취소</button>
                    <button onClick={handleSubmit}>수정 완료</button>               
            </div>
            <input multiple="multiple" type='file' id='imageInput' accept='image/*' onChange={handleImageUpload}/>
        </div>
        </div>
    )
}