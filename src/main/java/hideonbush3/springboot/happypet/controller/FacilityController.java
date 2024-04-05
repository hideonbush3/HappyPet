package hideonbush3.springboot.happypet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import hideonbush3.springboot.happypet.dto.FacilityDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;
import hideonbush3.springboot.happypet.service.FacilityService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("load-facilities")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping
    public ResponseEntity<?> retrieveFacilityList(@RequestParam String key) { 
        ResponseDTO<FacilityDTO> res = facilityService.loadFacilities(key);
        return ResponseEntity.ok().body(res);
    }
}