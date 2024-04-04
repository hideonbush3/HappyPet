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
        const token = localStorage.getItem('happypetToken');
        if(token === 'null'){
            alert('로그인 하셔야 합니다.');
            return;
        }
        content === "" ? alert("내용을 입력하세요") : 
        call('/comment/write', 'POST', {content: content, postId: props.post.id})
        .then((res) => {
            if(res.error !== null){
                alert('알수없는 에러가 발생했습니다.\n관리자에게 문의하세요.');
                return;
            }
            else if(res.message === '게시글이존재하지않음'){
                alert('게시글이 더이상 존재하지 않습니다.');
                window.location.href = '/board';
            }else{
                props.setCommentList(res.data);
                setContent("");
                post.commentList = res.data;
                navigate('/board/view', {state: {post: post}}, {replace: true});
            }
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