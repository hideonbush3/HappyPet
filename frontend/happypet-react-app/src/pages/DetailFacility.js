import React, { useEffect, useState } from "react";
import { useLocation } from "react-router-dom";
import {Map, MapMarker} from "react-kakao-maps-sdk";
import style from './DetailFacility.module.css';
import {call} from '../service/ApiService';
import ToastMessage from '../component/ToastMessage';
import { BsSuitHeart, BsSuitHeartFill } from "react-icons/bs";


function DetailFacility(){
    const location = useLocation();
    const f = location.state.facility;
    const position = {lat: f.lat, lng: f.lot};
    const [addedToFavorites, setAddedToFavorites] = useState();
    const [showToastMessage, setShowToastMessage] = useState(false);
    const [process, setProcess] = useState("");

    const callToastMessage = () => {
        setShowToastMessage(true);
        setTimeout(() => {
            setShowToastMessage(false);
        }, 500)
    }

    const clickDeleteFavorite = () => {
        call('/favorite/removeinmodal', 'DELETE', addedToFavorites)
        .then((res) => {
          if(res.message === '삭제성공'){
            setAddedToFavorites(null);
            setProcess('즐겨찾기 삭제 완료!');
            callToastMessage();
          }else if(res.error !== null){
            alert('알수없는 에러가 발생했습니다.\n재시도하거나 관리자에게 문의하세요.');
            return;
          }
        });
      }

    const clickAddFavorite = () => {
      const token = localStorage.getItem('happypetToken');
      if(token === 'null'){
        alert('즐겨찾기 추가를 하려면 로그인하셔야 합니다.');
        return;
      }
      call('/favorite', 'POST', f)
      .then((res) => {
        if(res.object !== null){
          setAddedToFavorites(res.object);
          setProcess("즐겨찾기 추가 완료!")
          callToastMessage();
        }else if(res.error !== null){
          alert('알수없는 에러가 발생했습니다.\n재시도하거나 관리자에게 문의하세요.');
          return;
        }
      })
    }

    useEffect(() => {
        call('/favorite/is-added', 'POST', f)
        .then((res) => {
            if(res.object !== null){
                setAddedToFavorites(res.object)
            }else if(res.message === '추가되지않음'){
                setAddedToFavorites(null);
            }else{
                alert('즐겨찾기 여부를 확인하다가 에러가 발생했습니다.\n관리자에게 문의하세요.');
                return;
            }
        });
    }, [f])
    function toMainPage(){
        window.location.href = '/';
    }

    return(
        <div fluid className={`${style.container}`}>
        <div className={`${style.contentContainer}`}>

                <div className={`${style.detailFacilityTitle} text-center`}>
                    {f.name}
                    {addedToFavorites !== null
                  ? <BsSuitHeartFill onClick={clickDeleteFavorite} className={style.star} size="30px"color="#fc1232"/> 
                  : <BsSuitHeart onClick={clickAddFavorite} className={style.star} size="30px"color="black"/>}
                </div>
                <div className="text-muted">
                    <Map
                        center={position}
                        style={{
                            width: "100%",
                            height: "350px"
                        }}
                        level={3}>
                        <MapMarker position={position}/>
                    </Map>
                    <div className="text-center mt-2"><strong>시설유형</strong></div>
                    <div className="text-center mb-2">{f.type}</div>
                    <table>
                        <tbody className={`${style.info_table}`}>
                            <tr><td><strong>도로명주소</strong></td><td className="text-center">{f.addr}</td></tr>
                            <tr><td><strong>시설유형</strong></td><td className="text-center">{f.type}</td></tr>
                            <tr><td><strong>자치시</strong></td><td className="text-center">{f.sigun}</td></tr>
                            <tr><td><strong>연락처</strong></td><td className="text-center">{f.tel === "null" ? '-' : f.tel}</td></tr>
                            <tr><td><strong>운영시간</strong></td><td className="text-center">
                                평일 {f.opTime === 'null' ? '-' : f.opTime} <br/>
                                토요일 {f.satOpTime === 'null' ? '-' : f.satOpTime} <br/>
                                일요일 {f.sunOpTime === 'null' ? '-' : f.sunOpTime}
                                </td></tr>
                            <tr><td><strong>휴업일</strong></td><td className="text-center">{f.restDay === "null" ? "-" : f.restDay}</td></tr>
                        </tbody>
                    </table>
                </div>
                <div>
                    <div className={style.btn_container}>
                        <button className={style.to_main_btn}onClick={toMainPage}>목록으로</button>
                    </div>
                </div>


        </div>
        <ToastMessage show={showToastMessage} onHide={() => setShowToastMessage(false)} process={process}/>
        </div>
    )
}

export default DetailFacility;