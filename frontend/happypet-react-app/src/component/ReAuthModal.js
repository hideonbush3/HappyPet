import { Modal } from "react-bootstrap";
import style from './ReAuthModal.module.css';
import { ModalBody, ModalHeader } from "react-bootstrap";

export default function ReAuthModal(props){
    const process = props.message === '회원정보수정' ? 'modify' : 'withdrawal';
    const authHandler = () => {
        const password = document.getElementById('password');
        if(password.value.trim().length === 0) {
            alert("비밀번호를 입력하세요."); return;
        }
        props.reAuth(password.value, process);
    }
    return(
        <Modal show={props.show} onHide={props.onHide}>
            <ModalHeader closeButton className={style.headerContainer}>
                <div className={style.header}>
                    <p>{props.message}</p>
                </div>
            </ModalHeader>
            <ModalBody>
                <div className={style.tip}>
                    <p>비밀번호를 입력하세요</p>
                </div>
                <div className={style.reAuthForm}>
                    <p>비밀번호</p>
                    <input type="password" id="password"/>
                    <button onClick={authHandler}>확인</button>
                </div>
            </ModalBody>
        </Modal>
    )
}