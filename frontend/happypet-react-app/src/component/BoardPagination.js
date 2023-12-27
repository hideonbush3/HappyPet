import { useEffect, useState } from "react";
import style from './BoardPagination.module.css';

function BoardPagination (props){
  let data = props.items;

  const [pageButtonList, setPageButtonList] = useState([]);

  const btnPerPage = 10;
  const itemsPerPage = 10;

  const pageCount = Math.ceil(data.length / itemsPerPage); 

  // 1~10페이지는 1그룹, 11~20페이지는 2그룹..
  const pageGroup = Math.ceil(props.currentPage / 10) 
  let firstPage = pageGroup * btnPerPage - btnPerPage + 1;
  let lastPage = pageGroup * btnPerPage > pageCount ? pageCount : pageGroup * btnPerPage;

  const handleClickPrev = () => {
    if (props.currentPage > 1) {
      props.setCurrentPage(props.currentPage - 1);
    }
  };

  const handleClickNext = () => {
    if (props.currentPage < pageCount) {
      props.setCurrentPage(props.currentPage + 1);
    }
  };
  
  const handleClickPage = (num) => {
    props.setCurrentPage(num);
}

  const renderPaginationItems = () => {
    return(
        pageButtonList.map((num, index) => {
            if(props.currentPage === num){
                return(
                    <a href='#' onClick={() => handleClickPage(num)} className={style.active} key={index}>{num}</a>
                )
            }else{
                return(
                    <a href='#' onClick={() => handleClickPage(num)} key={index}>{num}</a>
                )
            }
        })
    )
  };
  
  useEffect(() => {
    let pageBtnArray = [];
    for(let i = firstPage; i <= lastPage; i++){
      pageBtnArray.push(i);
    }
    setPageButtonList(pageBtnArray);
  }, [data, pageGroup])

  return (
    <div className={style.paginationContainer}>
    <div className={style.pagination}>
        {props.currentPage > 1 ? <a href="#" onClick={handleClickPrev}>이전</a> : null}
        
        {renderPaginationItems()}
        {props.currentPage < pageCount ? <a href="#" onClick={handleClickNext}>다음</a> : null}
        
    </div>
    </div>
  );
};
export default BoardPagination;