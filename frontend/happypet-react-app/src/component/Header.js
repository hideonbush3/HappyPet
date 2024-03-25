import { Col, Container, Nav, Navbar, Row } from "react-bootstrap";
import style from './Header.module.css';
function Header(){
    const token = localStorage.getItem("token");
    const signOutEvent = () => {
        localStorage.setItem("token", null);
    }

    return(
        <div className={style.body}>
            <Container className={style.container}>
                <Row>
                    <Col>
                        <Navbar collapseOnSelect expand="lg">
                            <Navbar.Brand href="/">해피펫</Navbar.Brand>
                            <Navbar.Toggle aria-controls="responsive-navbar-nav" />
                            <Navbar.Collapse id="responsive-navbar-nav">
                            <Nav className="me-auto"></Nav>
                            <Nav>
                                {token !== 'null' ? (
                                    <>
                                    <Nav.Link href="/board">게시판</Nav.Link>
                                    <Nav.Link href="/user/mypage/myinfo">마이페이지</Nav.Link>
                                    <Nav.Link href="/" onClick={signOutEvent}>로그아웃</Nav.Link>
                                    </>
                                ) :
                                    <>
                                    <Nav.Link href="/board">게시판</Nav.Link>
                                    <Nav.Link href="/user/login">로그인</Nav.Link>
                                    </>
                                }
                            </Nav>
                            </Navbar.Collapse>
                        </Navbar>
                    </Col>
                </Row>
            </Container>            
        </div>
    )
}

export default Header;