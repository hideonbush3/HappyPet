import { useNavigate } from 'react-router-dom';
import style from '../pages/Board/Board.module.css';
import { call } from '../service/ApiService';
export default function BoardList(props){
    const navigate = useNavigate();
    const items = props.items;

    const newPostClick = () => {
        if(localStorage.getItem('token') !== 'null'){
            window.location.href = '/board/write';
        }else{
            window.location.href = '/user/login';
        }
    };

    const viewDetail = (id) => {
        call(`/post/view?id=${id}`, 'GET', null)
        .then((res) => {
            navigate('/board/view', {state: {post: res}});
        })
    };

    return(
        <>
        
        <div className={style.tableContainer}>
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