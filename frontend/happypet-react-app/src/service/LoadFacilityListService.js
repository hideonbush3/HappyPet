import { call } from "./ApiService";

export default function LoadFacilityListService(props){
    const key = process.env.REACT_APP_API_KEY;
    const {setAddedToFavorites} = props;
    fetch("http://localhost:8080/facilityAPI?key=" + key, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      })
        .then((res) => res.json())
        .then((res) => {
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
              call('/favorite/isexist', 'POST', facility)
              .then((res) => {
                  if(res.object !== null){
                      setAddedToFavorites(res.object)
                  }else{
                      setAddedToFavorites(null);
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
        })
        .catch((error) => {
          console.error("에러 발생: ", error);
        });
}