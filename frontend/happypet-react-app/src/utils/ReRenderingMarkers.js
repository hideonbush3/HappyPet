export default function ReRenderingMarkers(props){
    let markers = [];
    if(props.filteredFacilityList.length > 0){
      props.filteredFacilityList.forEach((facility, index) => {
        const markerPosition = new window.kakao.maps.LatLng(facility.lat, facility.lot);
        const marker = new window.kakao.maps.Marker({
          position: markerPosition,
        });
        marker.setMap(props.myMap);
        const content = `<div style="padding:8px; display:flex; align-items:center; justify-content:center; min-width: 150px;max-width: 150px;">
                          ${facility.name}
                         </div>`;
        const infowindow = new window.kakao.maps.InfoWindow({
          content
        });
        
        window.kakao.maps.event.addListener(marker, 'mouseover', function() {
          props.setSelectedFacility(facility);
          infowindow.open(props.myMap, marker);
        });
        
        window.kakao.maps.event.addListener(marker, 'mouseout', function() {
          infowindow.close();
        });

        window.kakao.maps.event.addListener(marker, 'click', function() {
          props.viewModal(facility);
          props.facilityNameClickEH(facility);
        });
        markers.push(marker);
      });
      props.setMarkerList(markers);
    }else{
    }
}