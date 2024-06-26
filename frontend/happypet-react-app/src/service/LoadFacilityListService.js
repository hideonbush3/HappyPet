import { call } from "./ApiService";

export default function LoadFacilityListService(props){
    const key = process.env.REACT_APP_API_KEY;
    const {setAddedToFavorites} = props;
    
    const {setShowToastMessage} = props;
    setShowToastMessage(true);

    fetch("http://localhost:8080/load-facilities?key=" + key, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((res) => res.json())
        .then((res) => {
          if(res.error !== null){
            alert('시설 목록을 불러오다가 에러가 발생했습니다.\n관리자에게 문의하세요.');
            return;
          }
          else{
            props.setFacilityList(res.data);
    
            const container = document.getElementById('kakao-map');
            const options = {
              center: new window.kakao.maps.LatLng(37.4198715, 127.126405),
              level: 7
            };
            const map = new window.kakao.maps.Map(container, options);
            props.setMyMap(map);
        
            let markers = [];
            (res.data).forEach((facility) => {
              const markerPosition = new window.kakao.maps.LatLng(facility.lat, facility.lot);
              const marker = new window.kakao.maps.Marker({
                position: markerPosition,
              })
              marker.setMap(map);
    
              const content = `<div style="padding:8px; display:flex; align-items:center; justify-content:center; min-width: 150px;max-width: 150px;">
                                  ${facility.name}
                              </div>`;
  
              const infowindow = new window.kakao.maps.InfoWindow({
                content: content
              });
              
              window.kakao.maps.event.addListener(marker, 'mouseover', function() {
                props.setSelectedFacility(facility);
                infowindow.open(map, marker);
              });
              
              window.kakao.maps.event.addListener(marker, 'mouseout', function() {
                infowindow.close();
              });
    
              window.kakao.maps.event.addListener(marker, 'click', function() {
                call('/favorite/is-added', 'POST', facility)
                .then((res) => {
                    if(res.object !== null){
                        setAddedToFavorites(res.object)
                    }else if(res.message === '추가되지않음'){
                        setAddedToFavorites(null);
                    }else{
                      alert('즐겨찾기 여부를 확인하다가 에러가 발생했습니다.\n관리자에게 문의하세요.');
                      return;
                  }
                });
                props.viewModal(facility);
                map.setCenter(new window.kakao.maps.LatLng(facility.lat, facility.lot));
              });
            
              markers.push(marker);
            });
            props.setMarkerList(markers);
    
            let siguns = res.data.map((facility) => facility.sigun);
            let uniqueSiguns = [...new Set(siguns)];
            props.setSiguns(uniqueSiguns);
            
            let types = res.data.map((facility) => facility.type);
            let uniqueTypes = [...new Set(types)];
            props.setTypes(uniqueTypes);

            setShowToastMessage(false);
          }
        })
}