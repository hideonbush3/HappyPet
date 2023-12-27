import { useState } from 'react';
import style from './Writing.module.css';
import { call } from '../../service/ApiService';
import { useNavigate } from 'react-router-dom';
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
      call('/post', 'POST', {title: title, content: content})
      .then((res) => {
        if(res.error !== undefined){
            alert("게시글 작성을 실패했습니다");
        }else{
            navigate('/board/view', {state: {post: res}});
        }
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

        </div>
        </div>

    )
}