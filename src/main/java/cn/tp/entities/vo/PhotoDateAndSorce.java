package cn.tp.entities.vo;

import cn.tp.entities.Photo;
import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PhotoDateAndSorce {
    String date;
    List<Photo> photoList;
}
