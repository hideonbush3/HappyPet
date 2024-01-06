import { useLocation, useNavigate } from 'react-router-dom'
import style from './View.module.css'
import { call } from '../../service/ApiService';
import { useEffect, useState } from 'react';
import Comment from '../../component/Comment';
import { AiOutlineUser, AiFillEye , AiOutlineClockCircle, AiOutlineComment} from "react-icons/ai";
import { ImCancelCircle } from "react-icons/im";



export default function View(){
    const location = useLocation();
    const navigate = useNavigate();
    const post = location.state.post;   // 게시글 정보

    // 로그온 유저
    const [sessionUser, setSessionUser] = useState({});
    
    // 댓글 리스트
    const [commentList, setCommentList] = useState(post.commentList);

    // 대댓글 입력값
    const [replyContent, setReplyContent] = useState("");

    // 대댓글 UI on/off
    const [selectedCommentId, setSelectedCommentId] = useState(null);

    useEffect(() => {
        console.log(post);
        call('/user', 'GET', null)
        .then((res) => {
            setSessionUser(res);
        });
    }, [])    
    
    const modifyHandler = () => {
        navigate('/board/view/modify', {state: {post: post}});
    }

    const removeHandler = () => {
        const confirm = window.confirm("정말로 삭제하시겠습니까?")
        if(confirm){
            call(`/post/remove?id=${post.id}`, "DELETE", null)
            .then((res) => {
                if(res.error !== undefined) {
                    alert('이미 삭제된 게시글이거나 알 수 없는 문제가 발생했습니다 게시판 메인화면으로 이동합니다');
                    window.location.href = '/board';
                }
                else{
                    window.location.href = '/board';
                }
            })
        } 
    }
    
    const commentRemoveHandler = (comment) => {
        console.log(comment);
        if(window.confirm("삭제하시겠습니까?")){
            call(`/comment/remove`, 'DELETE', comment)
            .then((res) => {
                let newPost = post;
                newPost.commentList = res.data;
                setCommentList(res.data);
                navigate('/board/view', {state: {post: newPost}}, {replace: true});
            })
        }else return;
    }

    const replyFormHandler = (commentId) => {
        if (selectedCommentId === commentId) {
            setSelectedCommentId(null);
        } else {
            setSelectedCommentId(commentId);
        }
    };
    
    const changeReplyValueHandler = (e) => {
        e.preventDefault();
        setReplyContent(e.target.value);
        const textarea = document.getElementById('resizableTextarea');
        textarea.style.height = 'auto';
        textarea.style.height = (textarea.scrollHeight) + 'px';
    }

    const writeReplyHandler = () => {
        replyContent === "" ? alert("내용을 입력하세요") : 
        call('/reply/write', 'POST', {content: replyContent, commentId: selectedCommentId})
        .then((res) => {
            const commentId = parseInt(Object.keys(res)[0]);
            const newReplyList = res[commentId];
            let newCommentList = [...commentList];

            newCommentList.map((comment) => {
                if(comment.id === commentId){
                    comment.replyList = newReplyList;
                }
            })
            setCommentList(newCommentList);
            post.commentList = newCommentList;
            navigate('/board/view', {state: {post: post}}, {replace: true})
            setReplyContent("");

        })
        
    }

    const cancelHandler = () => {
        setSelectedCommentId(null);
    }

    const replyRemoveHandler = (key, reply) => {
        console.log(key);
        console.log(reply);
        if(window.confirm("삭제하시겠습니까?")){
            call('/reply/remove', 'DELETE', {id: reply.id, commentId: key})
            .then((res) => {
                const commentId = parseInt(Object.keys(res)[0]);
                const newReplyList = res[commentId];
                const newCommentList = [...commentList];
                newCommentList.map((comment) => {
                    if(comment.id === commentId){
                        comment.replyList = newReplyList;
                    }
                })
                setCommentList(newCommentList);
                post.commentList = newCommentList;
                navigate('/board/view', {state: {post: post}}, {replace: true});
            })
        }else return;
    }
    
    return(
        <div className={style.body}>
        <div className={style.container}>

            <h2>게시글 상세</h2>
            <div className={style.post}>
                <div className={style.title}>
                    <p>{post.title}</p>
                    {sessionUser && sessionUser.nickname && sessionUser.nickname === post.nickname && (
                        <div>
                            <button onClick={modifyHandler}>수정</button>
                            <button onClick={removeHandler}>삭제</button>
                        </div>
                    )}
                </div>
                <div className={style.writerInfo}>
                    <div>
                        <span><AiOutlineUser/> {post.nickname}</span>
                    </div>
                    <div>
                        <span>
                            <AiOutlineClockCircle/> {post.regdate}&nbsp;&nbsp;&nbsp;
                            <AiFillEye /> {post.views}&nbsp;&nbsp;&nbsp;
                            <AiOutlineComment/> {commentList.length === 0 ? 0 : commentList.length}
                        </span>
                    </div>
                </div>

                <div className={style.content}>
                    <p>{post.content}</p>
                </div>
            </div>

            <div className={style.bubbleContainer}>
                <div className={style.speechBubble}>
                    <div>
                        <p>{commentList.length === 0 ? 0 : commentList.length}</p>
                        <p>Comment</p>
                    </div>
                </div>
            </div>
            
            <div>
                {commentList.length !== 0 ?(
                    commentList.map((comment, index) => {
                        return(
                            <>
                            <div key={index}>
                                <div className={style.com} onClick={() => replyFormHandler(comment.id)}>
                                    <div>
                                        <p><AiOutlineUser/> &nbsp;{comment.nickname}</p>
                                        <p><AiOutlineClockCircle/> &nbsp;{comment.regdate}
                                            {sessionUser && sessionUser.username && comment.username === sessionUser.username &&(
                                                <>
                                                &nbsp;&nbsp;
                                                <ImCancelCircle className={style.deleteBtn} onClick={() => commentRemoveHandler(comment)}/>
                                                </>
                                            )}
                                        </p>
                                    </div>
                                    <div>
                                        <p>{comment.content}</p>
                                    </div>
                                </div>
                            </div>
                            {selectedCommentId === comment.id && (
                            <div className={style.replyFormContainer}>
                                <div className={style.replyForm}>
                                    <div>
                                        <textarea id='resizableTextarea' value={replyContent} onChange={changeReplyValueHandler} placeholder='대댓글을 작성하세요'/>
                                    </div>
                                    <div>
                                        <button onClick={cancelHandler}>취소</button>
                                        <button onClick={writeReplyHandler}>댓글 작성</button>
                                    </div>
                                </div>
                            </div>
                            )}
                            {comment.replyList.length !== 0 ? (
                                comment.replyList.map((reply, index) => {
                                    return(
                                        <div className={style.replyContainer}>
                                        <div className={style.reply} key={reply.id}>
                                            <div>
                                                <p><AiOutlineUser/> &nbsp;{reply.nickname}</p>
                                                <p><AiOutlineClockCircle/> &nbsp;{reply.regdate}
                                                    {sessionUser && sessionUser.username && sessionUser.username === reply.username && (
                                                    <>
                                                    &nbsp;&nbsp;
                                                    <ImCancelCircle className={style.deleteBtn} onClick={() => replyRemoveHandler(reply.commentId, reply)}/>
                                                    </>
                                                    )}
                                                </p>
                                            </div>
                                            <div>
                                                <p>{reply.content}</p>
                                            </div>
                                        </div>
                                    </div>    
                                    )
                                })
                            ) : null}
                            </>
                        );
                    })
                ) : <p>첫번째 댓글을 달아보세요</p>}


            </div>
            <Comment user={sessionUser} setCommentList={setCommentList} post={post}/>
        </div>
        </div>
    )
}