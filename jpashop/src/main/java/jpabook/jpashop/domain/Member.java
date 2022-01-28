package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded //내장 타입을 포함했다는 어노테이션으로 매핑해주면 된다. @Embeddable 또는 Embedded 둘 중에 하나만 있어도 된다.
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member") //Order의 member 필드에 의해 매핑된 것임을 명시, 읽기 전용, 값을 넣는다고 하여서 fk가 변경되지 않음
    private List<Order> orders = new ArrayList<>();
}
