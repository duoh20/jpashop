package study.datajpa.entity;

import lombok.*;
import org.springframework.stereotype.Service;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name", "age"}) //연관 관계는 toString을 결면 무한 루프에 빠질 수 있으므로 연관관계 필드는 가급적 toString하지 말자.
@NamedQuery( //엔티티에 NamedQuery 등록
        name = "Member.findByName", //작성한 이름은 관습적으로 사용하는 이름, 어떤 이름을 써도 상관 없다.
        query = "select m from Member m where m.name = :name"
)
@NamedEntityGraph(name="Member.all", attributeNodes = @NamedAttributeNode("team")) //JPA 표준 스펙
public class Member extends BaseEntity {

    @Id @GeneratedValue
    @Column(name="member_id")
    private Long id;
    private String name;
    private int age;

    @ManyToOne(fetch = FetchType.LAZY) //ToOne관계는 모두 LAZY 로딩으로 패치 전략을 설정해야 성능 최적화에 도움이 된다.
    @JoinColumn(name="team_id")
    private Team team;

    //protected Member() {}
    //private로 하면 JPA 구현체들이 프록시하면서 강제로 객체를 생성해야하므로 private으로 접근 제어하면 안된다.
    //protect로 하여 아무곳에서 생성되지 못하도록 제한하자.
    //JPA는 기본 생성자가 필요하다.

    public Member(String userName) {
        this.name = name;
    }

    public Member(String name, int age, Team team) {
        this.name = name;
        this.age = age;
        if(team != null) { //team이 null일 수 있으므로 null 체크
            changeTeam(team);
        }
    }

    public Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    //연관 관계를 설정하는 메서드를 만들어 주어야한다.
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
