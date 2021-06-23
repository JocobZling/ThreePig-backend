package cn.tp.entities.vo;

import lombok.*;

import javax.persistence.Entity;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserEmailAndPassword {
    String email;
    String password;
}
