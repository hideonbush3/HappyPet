import { Col, Container, Modal, Row } from "react-bootstrap";
import style from './InfoModal.module.css';
import { BsGeoAlt, BsHouse, BsTelephone } from "react-icons/bs";
import { AiOutlineStar, AiFillStar } from "react-icons/ai";
import { useNavigate } from "react-router-dom";
import { call } from "../service/ApiService";
import { useState } from "react";
import ToastMessage from "./ToastMessage";

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
      call('/favorite', 'POST', facility)
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

    const clickDeleteFavorite = () => {
      call('/favorite/removeinmodal', 'DELETE', addedToFavorites)
      .catch(error => {
        setAddedToFavorites(null);
        setProcess("즐겨찾기 삭제")
        callToastMessage();
      })
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
                  ? <AiFillStar onClick={clickDeleteFavorite} className={style.star} size="40px"color="yellow"/> 
                  : <AiOutlineStar onClick={clickAddFavorite} className={style.star} size="40px"color="yellow"/>}
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