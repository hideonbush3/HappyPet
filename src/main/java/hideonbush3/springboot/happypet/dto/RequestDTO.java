package hideonbush3.springboot.happypet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 시설 검색 시 요청바디
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestDTO {
    private String key;
    private String type;
}
