package com.project.dto;

import com.project.entity.User;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReq {
    @NotBlank
    private String name;
    @NotBlank
    private String userName;
    private String email;

    @Min(18)
    private Integer age;

    @NotBlank
    private String password;

    public User toUser(){
        return User.builder().
                name(this.name).
                age(this.age).
                email(this.email).
                username(this.userName).
                password(this.password).
                build();
    }
}
