package cn.tp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
    @JsonProperty("password")
    private String password;
    @JsonProperty("airSetId")
    private String airSetId;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date
            createTime = new Date();

    public User hidePassword() {
        this.password = "";
        return this;
    }
}
