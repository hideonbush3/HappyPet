import { useState } from 'react';
import style from '../pages/Board/View.module.css';
import { call } from '../service/ApiService';
import { useLocation, useNavigate } from 'react-router-dom';

export default function Post(props){
    const location = useLocation();
    const navigate = useNavigate();
    let post = location.state.post;

    const [content, setContent] = useState("");
    
    const commentChangehandler = (e) => {
        e.preventDefault();
        setContent(e.target.value);
        const textarea = document.getElementById('resizableTextarea');
        textarea.style.height = 'auto';
        textarea.style.height = (textarea.scrollHeight) + 'px';
    }

    const handleSubmit = (e) => {
        e.preventDefault();
        content === "" ? alert("내용을 입력하세요") : 
        call('/comment/write', 'POST', {content: content, postId: props.post.id})
        .then((res) => {
            props.setCommentList(res.data);
            setContent("");
            post.commentList = res.data;
            navigate('/board/view', {state: {post: post}}, {replace: true});
        })
    }
    return (
        <div className={style.commentFormContainer}>
            <div className={style.comment}>
                <textarea id='resizableTextarea' placeholder='댓글을 작성하세요' value={content} onChange={commentChangehandler}></textarea>
            </div>
            <div>
                <button onClick={handleSubmit}>댓글 작성</button>
            </div>
        </div>
    )
}