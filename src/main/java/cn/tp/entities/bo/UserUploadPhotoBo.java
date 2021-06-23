package cn.tp.entities.bo;

import lombok.*;

import javax.persistence.Entity;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserUploadPhotoBo {

    private long userId;
    private List<String> photoList;

}
