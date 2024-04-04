import { useNavigate } from 'react-router-dom';
import style from './BoardList.module.css';
import { call } from '../service/ApiService';
export default function BoardList(props){
    const navigate = useNavigate();
    const items = props.items;

    const newPostClick = () => {
        if(localStorage.getItem('happypetToken') !== 'null'){
            window.location.href = '/board/write';
        }else{
            alert('로그인 하셔야 합니다.');
            return;
        }
    };

    const viewDetail = (id) => {
        call(`/post/view?id=${id}`, 'GET', null)
        .then((res) => {
            if(res.message === '존재하지않는게시글'){
                alert('이미 삭제된 게시글입니다.');
                return;
            }else if(res.error !== null){
                alert('알 수 없는 에러가 발생했습니다.\n관리자에게 문의하세요.');
                return;
            }else{
                navigate('/board/view', {state: {post: res.object}});
            }
        });
    };

    return(
        <>
        
        <div className={style.container}>
        <div className={style.headContainer}>
            <button onClick={newPostClick}>글 작성</button>
        </div>
        <table>
            <thead>
                <tr>
                    <th className={style.title}>제목</th>
                    <th className={style.writer}>작성자</th>
                    <th className={style.views}>조회수</th>
                    <th className={style.date}>작성일</th>
                </tr>
            </thead>
            <tbody>
                {
                items.slice((props.currentPage - 1) * 10, props.currentPage * 10).map((item, index) => (
                    <tr key={index}>
                        <td className={style.tdTitle} onClick={() => viewDetail(item.id)}>{item.title}</td>
                        <td>{item.nickname}</td>
                        <td>{item.views}</td>
                        <td>{(item.regdate).slice(0, 8)}</td>
                    </tr>
                ))
                }
            </tbody>

        </table>
    </div>
    </>
    )
}