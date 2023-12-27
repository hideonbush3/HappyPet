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
                    <p>주소&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;01234 서울특별시 중구 세종대로 12345</p>
                    <p>전화&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;02-1234-5678또는02-8765-4321</p>
                    <p>개인정보보호책임자 hideonbush3(email)</p>
                    </div>
                </Col>
            </Row>
        </Container>

    )
}

export default Footer;