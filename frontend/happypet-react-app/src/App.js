import { useEffect, useState } from 'react';
import FacilityList from './component/FacilityList';
import InfoModal from './component/InfoModal';
import SearchBar from './component/SearchBar';
import MyPagination from './component/MyPagination';
import style from './Main.module.css';
import LoadFacilityListService from './service/LoadFacilityListService';

function App() {
  const [addedToFavorites, setAddedToFavorites] = useState();  
  const [facilityList, setFacilityList] = useState([{}]); 
  const [filteredFacilityList, setFilteredFacilityList] = useState([]);
  const [markerList, setMarkerList] = useState([]);

  const [currentPage, setCurrentPage] = useState(1);  
  
  const [myMap, setMyMap] = useState();

  const [selectedFacility, setSelectedFacility] = useState(null);
  const [showModal, setShowModal] = useState(false);
  
  const [siguns, setSiguns] = useState();
  const [types, setTypes] = useState();

  const [searchTerm, setSearchTerm] = useState('');
    
  function facilityNameClickEH(facility){
    myMap.setCenter(new window.kakao.maps.LatLng(facility.lat, facility.lot));
    myMap.setLevel(3);

  }
  
  function viewModal(f){
    setShowModal(true);
  }

  useEffect(() => {
    LoadFacilityListService({
      setAddedToFavorites,
      setFacilityList, 
      setMyMap, 
      setSelectedFacility, 
      setMarkerList, 
      viewModal,
      setSiguns,
      setTypes
    });
  }, []);

  // 검색버튼클릭시 새로운 마커 생성
  useEffect(() => {
    let markers = [];
    if(filteredFacilityList.length > 0){
      filteredFacilityList.forEach((facility) => {
        const markerPosition = new window.kakao.maps.LatLng(facility.lat, facility.lot);
        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
        });
        marker.setMap(myMap);

        const content = `<div style="padding:8px; display:flex; align-items:center; justify-content:center; min-width: 150px;max-width: 150px;">
                            ${facility.name}
                        </div>`;

        const infowindow = new window.kakao.maps.InfoWindow({
          content: content
        });
        window.kakao.maps.event.addListener(marker, 'mouseover', function() {
          setSelectedFacility(facility);
          infowindow.open(myMap, marker);
        });
        
        window.kakao.maps.event.addListener(marker, 'mouseout', function() {
          infowindow.close();
        });

        window.kakao.maps.event.addListener(marker, 'click', function() {
          viewModal(facility);
          facilityNameClickEH(facility);
        });
        markers.push(marker);
      });
      setMarkerList(markers);
    }else{
    }
  },[filteredFacilityList]);

  function clearMarker() {
    markerList.forEach((marker) => {
      marker.setMap(null);
    });
    setMarkerList([]);
  }
  
  function searchFacilityBtnEventHandler(selectedSigun, selectedType, searchTerm) {
    let result;
    // 검색어 없을경우
    if(searchTerm.length === 0){
      if(selectedSigun === '자치시' && selectedType === '시설유형') {
        result = [...facilityList];
      }else if(selectedSigun === '자치시' && selectedType !== '시설유형'){
        result = facilityList.filter((f) => {return f.type === selectedType});
      }else if(selectedSigun !== '자치시' && selectedType === '시설유형'){
        result = facilityList.filter((f) => {return f.sigun === selectedSigun});
      }else{
        result = facilityList.filter((f) => {return f.sigun === selectedSigun && f.type === selectedType});
      }
    }

    else{
      if(selectedSigun === '자치시' && selectedType === '시설유형') {
        result = facilityList.filter((f) => {return f.name.includes(searchTerm)})
      }else if(selectedSigun === '자치시' && selectedType !== '시설유형'){
        result = facilityList.filter((f) => {return f.type === selectedType && f.name.includes(searchTerm)});
      }else if(selectedSigun !== '자치시' && selectedType === '시설유형'){
        result = facilityList.filter((f) => {return f.sigun === selectedSigun && f.name.includes(searchTerm)});
      }else{
        result = facilityList.filter((f) => {return f.sigun === selectedSigun && f.type === selectedType && f.name.includes(searchTerm)});
      }
    }

    if(result.length !== 0){
      clearMarker();
      setFilteredFacilityList(result);
      let latlng;
      if(selectedSigun === '자치시' && selectedType === '시설유형' && searchTerm === ''){
        latlng = new window.kakao.maps.LatLng(37.4198715, 127.126405);
      }else latlng = new window.kakao.maps.LatLng(result[0].lat,result[0].lot);
      myMap.setCenter(latlng)
      setCurrentPage(1);
    }else{
      alert('일치하는 시설이 없습니다.');
      setSearchTerm('');
    }
  }

  return (
    <div fluid className={`${style.container} `} >

        <div className={`${style.subContainer}`} >

            <div className={style.section1}>
              <div className={`${style.title}`}><h4>우리동네 반려동물 의료시설</h4></div>
              <SearchBar 
                types={types} 
                setTypes={setTypes} 
                siguns={siguns} 
                searchTerm={searchTerm}
                setSearchTerm={setSearchTerm}
                facilityList={facilityList} 
                searchFacilityBtnEventHandler={searchFacilityBtnEventHandler} />

              <div id="kakao-map" style={{width:"100%", height:"500px"}}></div>                                          
            </div>
            
            <div className={style.section2}>
              <div className={`${style.facilityListCtn}`}>
                <FacilityList 
                  facilities={facilityList} filteredFacilityList={filteredFacilityList} 
                  currentPage={currentPage} viewModal={viewModal} setSelectedFacility={setSelectedFacility} 
                  facilityNameClickEH={facilityNameClickEH} setAddedToFavorites={setAddedToFavorites}/>
              </div>
              <div className={`${style.paginationCtn}`}>
                <MyPagination 
                  facilities={facilityList} filteredFacilityList={filteredFacilityList} 
                  setCurrentPage={setCurrentPage} currentPage={currentPage}/>
              </div>
            </div>

      
        </div>


      <InfoModal 
        facility={selectedFacility} 
        addedToFavorites={addedToFavorites} 
        setAddedToFavorites={setAddedToFavorites}
        show={showModal} onHide={() => setShowModal(false)} />
    </div>
  );
}

export default App;
