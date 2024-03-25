import { useEffect, useState } from 'react';
import style from './Board.module.css';
import { call } from '../../service/ApiService';
import BoardPagination from '../../component/BoardPagination';
import BoardList from '../../component/BoardList';

function Board(){
    const [items, setItems] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);  

    useEffect(() => {
        call('/post', 'GET', null)
          .then((res) => {
            setItems(res.data);
          });
      }, []);

    return(

    <div className={style.body}>
    <div style={{width: '1150px', margin: 'auto'}}>
      <h2>자유게시판</h2>
    </div>
    <div className={style.container}>
      
      <BoardList items={items} currentPage={currentPage}/>
      <BoardPagination items={items} setCurrentPage={setCurrentPage} currentPage={currentPage}/>

    </div>
    </div>

    )

}

export default Board;