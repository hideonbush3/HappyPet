import { Modal } from "react-bootstrap";
import style from './ToastMessage.module.css';
export default function ToastMessage(props){
    return(
        <Modal show={props.show} onHide={props.onHide}>
            <div className={style.content}>{props.process} 완료!</div>
        </Modal>
    )
}