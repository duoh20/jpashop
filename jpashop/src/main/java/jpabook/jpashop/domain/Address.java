package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable //jpa의 내장 타입임을 명시하여 어딘가에 내장이 될 수 있음을 알려줌
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA에서 기본 생성자는 필수이다.
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
