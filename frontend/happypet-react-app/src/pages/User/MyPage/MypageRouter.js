import { useLocation } from "react-router-dom"
import Mypage from "./Mypage";

export default function MypageRouter(){
    const location = useLocation();
    const path = location.pathname;
    const lastPart = path.replace("/user/mypage/", "");
    return(
        <>
            <Mypage location={lastPart}/>
        </>
    );
}