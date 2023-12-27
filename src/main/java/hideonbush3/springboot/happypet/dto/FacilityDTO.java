package hideonbush3.springboot.happypet.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FacilityDTO {
    private String name;
    private String type;
    private String sigun;
    private String dong;
    private String addr;
    private String tel;
    private String opTime;
    private String satOpTime;
    private String sunOpTime;
    private String restDay;
    private String img;
    private String lot;
    private String lat;
}
