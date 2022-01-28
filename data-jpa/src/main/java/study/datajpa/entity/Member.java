package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    private Long id;
    private String userName;

    protected Member() {}
    //private로 하면 JPA 구현체들이 프록시하면서 강제로 객체를 생성해야하므로 private으로 접근 제어하면 안된다.
    // protect로 하여 아무곳에서 생성되지 못하도록 제한하자.

    public Member(String userName) {
        this.userName = userName;
    }
}
