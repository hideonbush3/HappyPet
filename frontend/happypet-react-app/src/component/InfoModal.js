import { Col, Container, Modal, Row } from "react-bootstrap";
import style from './InfoModal.module.css';
import { BsGeoAlt, BsHouse, BsTelephone } from "react-icons/bs";
import { useNavigate } from "react-router-dom";
import { call } from "../service/ApiService";
import { useState } from "react";
import ToastMessage from "./ToastMessage";
import { BsSuitHeart, BsSuitHeartFill } from "react-icons/bs";


function InfoModal(props){
    const {facility} = props;
    const {addedToFavorites} = props;
    const navigate = useNavigate();
    const [showToastMessage, setShowToastMessage] = useState(false);
    const [process, setProcess] = useState("");
    const {setAddedToFavorites} = props;

    if (!facility || !facility.name) {
      return null;
    }

    const detailFacilityBtnEventHandler = () => {
      navigate("/facility/detail", { state: {facility: facility}});
    }

    const callToastMessage = () => {
      setShowToastMessage(true);
      setTimeout(() => {
          setShowToastMessage(false);
      }, 500)
  }

    const clickAddFavorite = () => {
      const token = localStorage.getItem('happypetToken');
      if(token === 'null'){
        alert('즐겨찾기 추가를 하려면 로그인하셔야 합니다.');
        return;
      }
      call('/favorite', 'POST', facility)
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
    
    return(
      <Modal {...props} aria-labelledby="contained-modal-title-vcenter">
      <Modal.Header closeButton className={`${style.header}`}>
        <Modal.Title>
          {facility.name}
        </Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Container>
           <Row className="mb-2 mt-2">
            <Col xs={1}>
              <BsGeoAlt/>
            </Col>
            <Col xs={{offset:1}}>
              {facility.addr}
            </Col>
          </Row>
          <Row className="mb-2">
            <Col xs={1}>
              <BsTelephone/>
            </Col>
            <Col xs={{offset:1}}>
              {facility.tel === "null" ? "정보없음" : facility.tel}
            </Col>
          </Row>
          <Row className="mb-5">
            <Col xs={1}>
              <BsHouse/>
            </Col>
            <Col xs={{offset:1}}>
              {facility.type}
            </Col>
          </Row>
          <div className={`${style.footer} mb-2`}>
                <div className={style.starContainer}>
                  {addedToFavorites !== null
                  ? <BsSuitHeartFill onClick={clickDeleteFavorite} className={style.star} size="40px"color="#fc1232"/> 
                  : <BsSuitHeart onClick={clickAddFavorite} className={style.star} size="40px"color="black"/>}
                  </div>
                <div className={style.detailBtnContainer}>
                  <button onClick={detailFacilityBtnEventHandler}>상세보기</button>
                </div>
          </div>
          <ToastMessage show={showToastMessage} onHide={() => setShowToastMessage(false)} process={process}/>

        </Container>
      </Modal.Body>
    </Modal>
    )
  }

  export default InfoModal;