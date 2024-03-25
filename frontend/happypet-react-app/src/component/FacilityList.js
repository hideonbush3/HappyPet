import React from "react";
import style from './FacilityList.module.css';
import { call } from "../service/ApiService";


export default function FacilityList(props){
    const {currentPage} = props;
    const {facilities} = props;
    const {filteredFacilityList} = props;
    const {setAddedToFavorites} = props;

    let lastItemIndex = currentPage * 10 - 1;
    let firstItemIndex = lastItemIndex - 9

    function facilityNameClickEH (facility){
        call('/favorite/isexist', 'POST', facility)
        .then((res) => {
            if(res.object !== null){
                setAddedToFavorites(res.object);
            }else{
                setAddedToFavorites(null);
            }
        });
        props.setSelectedFacility(facility);
        props.viewModal(facility);
        props.facilityNameClickEH(facility);
    }

    if(filteredFacilityList.length === 0){
        return(
            <div className={style.container}>

                <div>총 <span className={`${style.count}`}>{facilities.length}</span>건</div>
                <table>
                <thead> 
                    <tr>
                        <th>시설명</th>
                        <th>시설유형</th>
                        <th>연락처</th>
                    </tr>
                </thead>
                <tbody>
                    {facilities.slice(firstItemIndex, lastItemIndex + 1).map((facility, i) => (
                    <tr key={i} >
                        <td onClick={() => facilityNameClickEH(facility)}>
                            {facility.name && facility.name.length < 18 ? facility.name 
                            : (facility.name && `${facility.name.substring(0, 18)}...`)}
                        </td>
                        <td>{facility.type}</td>
                        <td>{facility.tel === "null" ? "정보없음" : facility.tel}</td>
                    </tr>
                    ))}
                </tbody>
                </table>

            </div>
        )
    }else{
        return(
            <div className={style.container}>

                <div>총 <span className={`${style.count}`}>{filteredFacilityList.length}</span>건</div>
                <table>
                <thead> 
                    <tr>
                        <th>시설명</th>
                        <th>시설유형</th>
                        <th>연락처</th>
                    </tr>
                </thead>
                <tbody>
                    {filteredFacilityList.slice(firstItemIndex, lastItemIndex + 1).map((facility, i) => (
                    <tr key={i}>
                        <td onClick={() => facilityNameClickEH(facility)}>{facility.name}</td>
                        <td>{facility.type}</td>
                        <td>{facility.tel}</td>
                    </tr>
                    ))}
                </tbody>
                </table>

            </div>
        )
    }
}