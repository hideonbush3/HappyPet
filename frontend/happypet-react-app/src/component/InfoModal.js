import { Col, Container, Modal, Row } from "react-bootstrap";
import style from '../Main.module.css';
import { BsGeoAlt, BsHouse, BsTelephone } from "react-icons/bs";
import { AiOutlineStar, AiFillStar } from "react-icons/ai";
import { useNavigate } from "react-router-dom";
import { call } from "../service/ApiService";

function InfoModal(props){
    const {facility} = props;
    const {addedToFavorites} = props;
    const navigate = useNavigate();

    if (!facility || !facility.name) {
      return null;
    }

    const detailFacilityBtnEventHandler = () => {
      navigate("/facility/detail", { state: {facility: facility}});
    }

    const clickAddFavorite = () => {
      call('/favorite', 'POST', facility)
      .then((res) => {
        if(res.error === undefined){
          props.setAddedToFavorites(res);
        }else{
          alert("이미 즐겨찾기 추가한 시설입니다.")
        }
      })
    }

    const clickDeleteFavorite = () => {
      call('/favorite/removeinmodal', 'DELETE', addedToFavorites)
      .catch(error => {
        console.log(`설계상 에러\n메세지 => `, error);
        props.setAddedToFavorites(null);
      })
    }
    return(
      <Modal {...props} aria-labelledby="contained-modal-title-vcenter">
      <Modal.Header closeButton className={`${style.modalHeaderBgColor}`}>
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
                <div className={style.detailBtnContainer}><button className={style.detailBtn} onClick={detailFacilityBtnEventHandler}>상세보기</button></div>
          </div>
        </Container>
      </Modal.Body>
    </Modal>
    )
  }

  export default InfoModal;