package hideonbush3.springboot.happypet.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDTO<T> {  
    private String error;
    private String message;
    private List<T> data;
    private T object;
}
