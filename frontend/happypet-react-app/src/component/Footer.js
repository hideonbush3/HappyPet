import { Col, Container, Row } from "react-bootstrap";
import style from './Footer.module.css';
import React from "react";
function Footer(){
    return(
        <Container fluid className={`${style.mainContainer}`}>
            <Row className={`${style.subContainer}`}>
                <Col className={`${style.content}`}>
                    <div>
                        <h3>해피펫</h3>
                    </div>
                    <div className={`${style.info}`}>
                    <p>주소&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(우)00000 정보없음</p>
                    <p>연락처&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;정보없음</p>
                    <p>개인정보보호책임자 &nbsp;ppoii15923@gmail.com</p>
                    </div>
                </Col>
            </Row>
        </Container>

    )
}

export default Footer;