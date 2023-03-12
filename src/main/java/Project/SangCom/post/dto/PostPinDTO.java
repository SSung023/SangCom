package Project.SangCom.post.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@ToString
@NoArgsConstructor
public class PostPinDTO {

    List<String> pinList = new ArrayList<>();

    @Builder
    public PostPinDTO(List<String> pinList) {
        this.pinList = pinList;
    }
}
