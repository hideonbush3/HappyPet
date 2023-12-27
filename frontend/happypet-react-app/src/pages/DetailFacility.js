import React, { useEffect, useState } from "react";
import { Card, Col, Container, Row } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import {Map, MapMarker} from "react-kakao-maps-sdk";
import style from './DetailFacility.module.css';
import {call} from '../service/ApiService';
import { AiOutlineStar, AiFillStar } from "react-icons/ai";
import ToastMessage from '../component/ToastMessage';

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
        .catch(error => {
          console.log(`설계상 에러\n메세지 => `, error);
          setAddedToFavorites(null);
          setProcess("즐겨찾기 삭제")
          callToastMessage();
        })
      }

    const clickAddFavorite = () => {
    call('/favorite', 'POST', f)
    .then((res) => {
        if(res.error === undefined){
        setAddedToFavorites(res);
        setProcess("즐겨찾기 추가")
        callToastMessage();
        }else{
        alert("이미 즐겨찾기 추가한 시설입니다.")
        }
    })
    }

    useEffect(() => {
        call('/favorite/isexist', 'POST', f)
        .then((res) => {
            if(res.error === undefined){
                setAddedToFavorites(res)
            }else{
                setAddedToFavorites(null);
                console.log(`즐겨찾기에 추가되지 않은 시설\n메세지 => `, res.error);
            }
        });
    }, [f])
    function toMainPage(){
        window.location.href = '/';
    }

    return(
        <Container fluid className={`${style.container}`}>
        <Row className={style.body}>
        <Col className={`${style.contentContainer}`}>

            <Card className="mt-3 mb-3">
                <Card.Header className={`${style.detailFacilityTitle} text-center`}>
                    {f.name}
                    {addedToFavorites !== null
                  ? <AiFillStar onClick={clickDeleteFavorite} className={style.star} size="30px"color="yellow"/> 
                  : <AiOutlineStar onClick={clickAddFavorite} className={style.star} size="30px"color="yellow"/>}
                </Card.Header>
                <Card.Body className="text-muted">
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
                        <tbody className={`${style.detailFacilityInfoTable}`}>
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
                </Card.Body>
                <Card.Body>
                    <div className="text-end">
                        <button onClick={toMainPage}>목록으로</button>
                    </div>
                </Card.Body>
            </Card>

        </Col>
        </Row>
        <ToastMessage show={showToastMessage} onHide={() => setShowToastMessage(false)} process={process}/>
        </Container>
    )
}

export default DetailFacility;