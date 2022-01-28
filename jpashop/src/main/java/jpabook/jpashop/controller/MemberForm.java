package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Controller;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberForm {
    //엔티티를 파라미터로 받지 않고 form 클래스를 따로 만드는 이유?
    //화면에 필요한 데이터를 담을 수 있는 데이터 전송 클래스를 따로 만드는 편이 유지보수에 도움이 된다.
    //또한 엔티티 레벨과 화면 단위에서 필요한 유효성 검사가 다를 수 있는데, 역시 따로 데이터 전송 클래스를 따로 두면 복잡성이 감소된다.
    private String city;
    @NotEmpty(message = "회원 이름은 필수입니다.") //spring-validation-starter에서 제공하는 유효성 검사 애노테이션. String과 Collection의 null과 공백을 허용하지 않는다.
    private String name;
    private String street;
    private String zipcode;
}
