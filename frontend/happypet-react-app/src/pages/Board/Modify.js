import { useState } from 'react';
import style from './Writing.module.css';
import { call } from '../../service/ApiService';
import { useLocation, useNavigate } from 'react-router-dom';
export default function Modify(){
    const location = useLocation();
    const navigate = useNavigate();

    const post = location.state.post;

    const [originTitle, setOriginTitle] = useState(post.title);
    const [originContent, setOriginContent] = useState(post.content)
    const [title, setTitle] = useState(post.title);
    const [content, setContent] = useState(post.content);
  
    const cancelEventHandler = () => {
        navigate('/board/view', {state: {post: post}});
    }

    const handleTitleChange = (e) => {
      setTitle(e.target.value);
    };
  
    const handleContentChange = (e) => {
      setContent(e.target.value);
    };
  
    const handleSubmit = (e) => {
        e.preventDefault();
        if(originTitle === title && originContent === content){
            alert("변경된 내용이 없습니다");
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
                <div className={style.content}>
                    <textarea 
                        placeholder='내용을 입력하세요'
                        value={content}
                        onChange={handleContentChange}/>
                </div>
            </div>
            <div className={style.btn}>
                    <button onClick={cancelEventHandler}>취소</button>
                    <button onClick={handleSubmit}>수정 완료</button>               
            </div>

        </div>
        </div>
    )
}