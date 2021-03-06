package com.crowdsourcing.test.dto.user;

import com.crowdsourcing.test.domain.CommonCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Getter @Setter /** 클래스 내 모든 필드의 Getter/Setter 메소드 생성 */
public class UserDto {

    private String username;
    private String password;
    private String commonCode;
}
