package hideonbush3.springboot.happypet.service;

import hideonbush3.springboot.happypet.dto.FacilityDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

public interface FacilityService {
    public ResponseDTO<FacilityDTO> loadFacilities(String key); 
}
