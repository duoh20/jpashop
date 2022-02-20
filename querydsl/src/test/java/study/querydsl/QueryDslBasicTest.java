package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

    @Autowired EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void loadContents() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);

        em.flush();
        em.clear();
    }

    @Test
    public void startJPQL(){
        //find
        Member findMember = em.createQuery("select m from Member m where m.name = : name", Member.class)
                            .setParameter("name", "member1")
                            .getSingleResult();

        assertThat(findMember.getName()).isEqualTo("member1");
    }

    @Test
    public void startQueryDsl() {
        //1.QueryDsl 사용을 위해 JPAQueryFactory 생성
        queryFactory = new JPAQueryFactory(em);
        //2.Q클래스 생성
        QMember m = new QMember("m");
        //3.find 로직 작성
        //컴파일 시점에 오류를 잡을 수 있고, IDE 자동 완성의 도움을 받을 수 있다.
        Member findMember = queryFactory.select(m)
                                        .from(m)
                                        .where(m.name.eq("member1")) //파라미터 바인딩 처리
                                        .fetchOne();

        assertThat(findMember.getName()).isEqualTo("member1");
    }

}
