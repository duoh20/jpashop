package study.datajpa.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) //JPA는 기본 생성자가 필요함
@ToString(of = {"id", "name"})
public class Team {

    @Id @GeneratedValue
    @Column(name="team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team") //mappedBy는 foreiegnkey가 없는 쪽에 걸자.
    private List<Member> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }
}
