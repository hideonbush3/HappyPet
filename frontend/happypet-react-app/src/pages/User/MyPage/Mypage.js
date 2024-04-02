import React, { useEffect, useState } from "react";
import style from './MyPage.module.css';
import {call} from '../../../service/ApiService';
import InfoModal from "../../../component/InfoModal";
import { useNavigate } from "react-router-dom";
import ReAuthModal from "../../../component/ReAuthModal";
import NoData from "../../../component/NoData";

function Mypage(props) {
    const navigate = useNavigate();
    const mypageUrl = '/user/mypage/';
    const location = `${mypageUrl}${props.location}`;

    const [sessionUser, setSessionUser] = useState({});
    const [activeTab, setActiveTab] = useState(location);
    
    // 내정보수정, 탈퇴하기
    const [showReAuthModal, setShowReAuthModal] = useState(false);
    const [message, setMessage] = useState('');

    // 즐겨찾기
    const [favoriteList, setFavoriteList] = useState();
    const [selectedFacility, setSelectedFacility] = useState();
    const [addedToFavorites, setAddedToFavorites] = useState();  // 즐겨찾기에 추가돼있는 시설
    const [showModal, setShowModal] = useState(false);

    // 내 게시글
    const [postList, setPostList] = useState();
    
    // 유저정보
    useEffect(() => {
        call('/user', 'GET', null)
        .then((res) => {
            setSessionUser(res)
        })
    }, [])

    // 즐겨찾기 리스트
    useEffect(() => {
        call('/favorite', 'GET', null)
        .then((res) => {
            if(res.data !== null){
                setFavoriteList(res.data);
            }else if(res.error !== null){
                alert('즐겨찾기 목록을 불러오는 도중 알수없는 에러가 발생했습니다.\n관리자에게 문의하세요.');
                return;
            }
        })
    }, [addedToFavorites])

    // 게시글
    useEffect(() => {
        call('/post/mypost', 'GET', null)
        .then((res) => {
            setPostList(res.data)
        })
    }, [])

    const clickTap = (tab) => {
        setActiveTab(`${mypageUrl}${tab}`);
        window.history.pushState(null, null, tab);
    }

    const facilityNameClickEH = (facility) => {
        call('/favorite/isexist', 'POST', facility)
        .then((res) => {
            if(res.object !== null){
                setAddedToFavorites(res.object)

            }else{
                setAddedToFavorites(null);
                setSelectedFacility(facility);
                setShowModal(true);
            }
        });
        setSelectedFacility(facility);
        setShowModal(true);
    }

    const postNameClickEH = (id) => {
        call(`/post/view?id=${id}`, 'GET', null)
        .then((res) => {
            navigate('/board/view', {state: {post: res}});
        });
    }

    const modifyInfoHandler = () => {
        setMessage('회원정보수정')
        setShowReAuthModal(true);
    }

    const withdrawalHandler = () => {
        setMessage('회원탈퇴')
        setShowReAuthModal(true);
    }

    const reAuth = (password, process) => {
        call(`/user/reauth?process=${process}`, 'POST', {password: password})
        .then((res) => {        
            if(res.message === 'modify') window.location.href = '/user/modify';
            else if(res.message === 'withdrawal'){
                const result = window.confirm("회원탈퇴 하시겠습니까?");
                if(result){
                    call('/user/remove', 'DELETE')
                    .then((res) => {
                        if(res.message === '탈퇴완료'){
                            alert("회원탈퇴가 완료됐습니다.");
                            setShowReAuthModal(false);
                            window.location.href = '/';
                            localStorage.setItem("happypetToken", null);
                        }else{
                            alert('회원님의 데이터를 삭제하는도중에 알수없는 에러가 발생했습니다.\n재시도하거나 관리자에게 문의하세요.')
                            return;
                        }    
                    });
                }else {
                    setShowReAuthModal(false);
                    return;
                }
            }
            else if(res.message === '비밀번호불일치'){
                alert("비밀번호를 다시 확인하세요.");
                return;
            }else{
                alert('비밀번호 확인도중에 알수없는 에러가 발생했습니다.\n재시도하거나 관리자에게 문의하세요.');
                return;
            }
        });
    }

    return(
        <div className={`${style.mainContainer}`}>
            <div className={`${style.subContainer}`}>

                <div className={style.accordion}>
                <div style={{width: '85%'}}>
                    <div className={activeTab === `${mypageUrl}myinfo` ? style.activeAccordionItem : style.accordionItem} onClick={() => clickTap('myinfo')}>
                        <div className={style.accordionHeader}>내 정보 수정</div>
                    </div>

                    <div className={activeTab === `${mypageUrl}favorite` ? style.activeAccordionItem : style.accordionItem} onClick={() => clickTap('favorite')}>
                        <div className={style.accordionHeader}>즐겨찾기 목록</div>
                    </div>

                    <div className={activeTab === `${mypageUrl}post` ? style.activeAccordionItem : style.accordionItem} onClick={() => clickTap('post')}>
                        <div className={style.accordionHeader}>내가 쓴 글</div>
                    </div>
                </div>
                </div>

                {activeTab === `${mypageUrl}myinfo` && (
                    <div className={style.body}>
                        <div className={style.header}><h3>내 정보 수정</h3></div>
                        <div className={style.myinfo}>
                            <div><p>아이디</p><p>{sessionUser.username}</p></div>
                            <div><p>비밀번호</p><p>●●●●●●●●●●</p></div>
                            <div><p>닉네임</p><p>{sessionUser.nickname}</p></div>
                            <div><p>이메일</p><p>{sessionUser.email}</p></div>
                        </div>
                        <div className={style.btn}>
                            <button onClick={modifyInfoHandler}>수정하기</button>
                            <button onClick={withdrawalHandler}>탈퇴하기</button>
                        </div>
                    </div>
                )}
                {activeTab === `${mypageUrl}favorite` && (
                    <div className={style.body}>
                        <div className={style.header}><h3>즐겨찾기 목록</h3></div>
                        <div className={style.table}>
                            <div className={style.thead}>
                                <p className={style.si}>시</p>
                                <p className={style.type}>시설유형</p>
                                <p className={style.fname}>시설명</p>
                            </div>
                        </div>
                            {favoriteList !== undefined && favoriteList.length > 0
                            ?
                            favoriteList.map((f, index) => {
                                return(
                                    <div key={index} className={`${style.table} mt-2`}>
                                        <div className={style.favoriteTbody}>
                                            <p>{f.sigun}</p>
                                            <p>{f.type}</p>
                                            <p onClick={() => facilityNameClickEH(f)}>
                                                {(f.name).length > 24 
                                                ? `${(f.name).slice(0, 24)}...`
                                                : f.name}
                                            </p> 
                                        </div>
                                    </div>
                                )
                            })
                            :
                            <NoData message="추가한 시설이 없습니다"/>                         
                            }
                    </div>
                )}
                {activeTab === `${mypageUrl}post` && (
                    <div className={style.body}>
                    <div className={style.header}><h3>내가 쓴 글</h3></div>
                    <div className={style.table}>
                        <div className={style.thead}>
                            <p className={style.title}>제목</p>
                            <p className={style.views}>조회수</p>
                            <p className={style.regdate}>작성일</p>
                        </div>
                    </div>
                        {postList !== undefined && postList.length > 0  
                        ?
                         postList.map((p, index) => {
                            return(
                                <div key={index} className={`${style.table} mt-2`}>
                                    <div className={style.postTbody}>
                                        <p onClick={() => postNameClickEH(p.id)}>
                                            {(p.title).length > 27 
                                            ? `${(p.title).slice(0, 27)}...`
                                            : p.title}
                                        </p>
                                        <p>{p.views}</p>
                                        <p>{p.regdate}</p> 
                                    </div>
                                </div>
                            )
                        })
                        :
                        <NoData message="작성한 게시글이 없습니다"/>
                        }
                </div>
                )}

            </div>
            <InfoModal 
                facility={selectedFacility} 
                addedToFavorites={addedToFavorites} 
                setAddedToFavorites={setAddedToFavorites}
                show={showModal} onHide={() => setShowModal(false)} />
            
            <ReAuthModal
                message={message}
                show={showReAuthModal} 
                onHide={() => setShowReAuthModal(false)} 
                reAuth={reAuth}/>
        </div>
    )
}

export default Mypage;