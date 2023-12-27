package hideonbush3.springboot.happypet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import hideonbush3.springboot.happypet.dto.FacilityDTO;
import hideonbush3.springboot.happypet.dto.ResponseDTO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("facilityAPI")
public class FacilityController {
    final private Integer pSize = 300; 

    @GetMapping
    public ResponseEntity<?> retrieveFacilityList(@RequestParam String key) { 
        String apiUrl = "https://openapi.gg.go.kr/OTHERHALFANIMEDIWELF?key=" + key + "&type=json&psize=" + pSize;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

        // JSON 파싱을 위한 Jackson ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // JSON 문자열을 JsonNode로 파싱
            JsonNode rootNode = objectMapper.readTree(responseEntity.getBody());

            // 필요한 데이터가 담긴 row 부분만 추출
            JsonNode rowNode = rootNode.get("OTHERHALFANIMEDIWELF").get(1).get("row");

            Set<String> facilityNameSet = new TreeSet<>();
            List<FacilityDTO> uniqueObjects = new ArrayList<>();

            // JsonNode에 들어있는 객체 하나씩 가져와 "CMPNM_NM" 필드의 값을 Set에 넣는다.
            for (JsonNode node : rowNode) {
                String facilityName = node.get("CMPNM_NM").asText();

                // Set.add()를 호출 시 기존에 들어있는 값과 중복되지 않을 경우 true를 반환한다.
                // 성공적으로 넣었을경우 List에 담는다.
                if (facilityNameSet.add(facilityName)) {
                    uniqueObjects.add(new FacilityDTO(
                        node.get("CMPNM_NM").asText(), 
                        node.get("INDUTYPE_NM").asText(), 
                        node.get("SIGNGU_NM").asText(), 
                        node.get("EMD_NM").asText(), 
                        node.get("LOCPLC_ROADNM_ADDR").asText(), 
                        node.get("RPRS_TELNO").asText(), 
                        node.get("OPR_TM").asText(), 
                        node.get("SAT_OPR_TM").asText(), 
                        node.get("SUN_OPR_TM").asText(), 
                        node.get("REST_DAY").asText(), 
                        node.get("IMAGE_NM").asText(), 
                        node.get("REFINE_WGS84_LOGT").asText(), 
                        node.get("REFINE_WGS84_LAT").asText() 
                        ));
                }
            }
            
            // 시설명으로 정렬
            Collections.sort(uniqueObjects, Comparator.comparing(FacilityDTO::getName));

            ResponseDTO<FacilityDTO> res = ResponseDTO.<FacilityDTO>builder().data(uniqueObjects).build();
            return ResponseEntity.ok().body(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
}