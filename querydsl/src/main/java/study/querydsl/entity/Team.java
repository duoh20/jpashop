package study.querydsl.entity;

import lombok.*;
import org.springframework.util.AutoPopulatingList;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Team {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;

    @OneToMany(mappedBy = "team")
    @ToString.Exclude
    private List<Member> members = new ArrayList<>(); //연관 관계의 주인이 아니므로 외래키를 업데이트하지 않았다.

    public Team(String name) {
        this.name = name;
    }
}
