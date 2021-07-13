package cn.tp.entities.vo;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoTimeDisplay {
    String date;
    List<PhotoDisplayVo> photoDisplayVoList;
}
