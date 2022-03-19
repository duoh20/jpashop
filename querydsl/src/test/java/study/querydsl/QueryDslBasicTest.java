package study.querydsl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import java.util.List;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

@SpringBootTest
@Transactional
public class QueryDslBasicTest {

    @Autowired
    private EntityManager em;
    private JPAQueryFactory queryFactory;

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
    public void startJPQL() {
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
        //2.Q클래스 인스턴트 생성하기
        //2-1. 별칭 직접 지정
        //QMember m = new QMember("m");
        //2-2. 기본 스테틱 인스턴스 사용 [권장]
        //QMember.member로 바로 사용하거나,
        //static import로 불러다 놓고 사용하자. (import static study.querydsl.entity.QMember.*;)
        //3.find 로직 작성
        //컴파일 시점에 오류를 잡을 수 있고, IDE 자동 완성의 도움을 받을 수 있다.
        Member findMember = queryFactory.select(member)
                .from(member)
                .where(member.name.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();

        assertThat(findMember.getName()).isEqualTo("member1");
    }

    @Test
    public void search() {
        queryFactory = new JPAQueryFactory(em);
        Member findMember = queryFactory
                .selectFrom(member)
                //이름이 member1 이고, 나이가 10인 맴버 조회
                .where(
                        member.name.eq("member1")
                                .and(member.age.eq(10))
                )
                .fetchOne();

        assertThat(findMember.getName()).isEqualTo("member1");
    }

    @Test
    public void searchAndParam() {
        queryFactory = new JPAQueryFactory(em);
        Member findMember = queryFactory
                .selectFrom(member)
                //기본적으로 where는 and로 검색한다. 따라서 and 조건이라면 .and()로 체인을 걸지 않고, 콤마로 구분해도 된다.
                .where(
                        member.name.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();

        assertThat(findMember.getName()).isEqualTo("member1");
    }

    @Test
    public void resultFetch() {
        queryFactory = new JPAQueryFactory(em);

        //리스트 조회
        List<Member> fetch = queryFactory
                .selectFrom(member)
                .fetch();

        //단건 조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .where(member.name.eq("member1"))
                .fetchOne();

        //처음 한 건만 조회
        Member fetchFirst = queryFactory
                .selectFrom(member)
                //.limit(1).fetchOne()과 같음
                .fetchFirst();

        //페이징 정보를 포함한 조회
        QueryResults<Member> results = queryFactory.selectFrom(member)
                .fetchResults();
        System.out.println("total " + results.getTotal());
        List<Member> members = results.getResults();

        //전체 count 조회
        long count = queryFactory.selectFrom(member)
                .fetchCount();
    }


    /**
     * 회원정렬순서
     * 1.회원 나이 내림차순
     * 2.회원 이름 올림차순
     * 단 2에서 회원 이름 없으면 마지막에 출력
     */
    @Test
    public void order() {

        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));
        em.persist(new Member(null, 100));

        queryFactory = new JPAQueryFactory(em);

        List<Member> result = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.name.asc().nullsLast()) //nullFirst()도 있음
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member nullMember = result.get(2);
        assertThat(member5.getName()).isEqualTo("member5");
        assertThat(member6.getName()).isEqualTo("member6");
        assertThat(nullMember.getName()).isEqualTo(null);
    }

    @Test
    public void paging() {
        queryFactory = new JPAQueryFactory(em);
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.name.desc())
                .offset(1)
                .limit(2)
                .fetch();

        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void aggregation() {
        queryFactory = new JPAQueryFactory(em);

        //Tuple은 queryDSL이 제공하는 타입
        Tuple result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min()
                )
                .from(member)
                .fetchOne();

        assertThat(result.get(member.count())).isEqualTo(4);
        assertThat(result.get(member.age.sum())).isEqualTo(100);
        assertThat(result.get(member.age.avg())).isEqualTo(25);
        assertThat(result.get(member.age.max())).isEqualTo(40);
        assertThat(result.get(member.age.min())).isEqualTo(10);
    }


    /**
     * 팀의 이름과 각 팀의 평균 연령을 구하라
     */
    @Test
    public void group() throws Exception {
        queryFactory = new JPAQueryFactory(em);

        List<Tuple> result = queryFactory.select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                //.having() having절도 추가할 수 있다.
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /**
     * teamA에 소속된 모든 회원
     */
    @Test
    public void join() {
        queryFactory = new JPAQueryFactory(em);
        List<Member> result = queryFactory.selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();
        assertThat(result)
                .extracting("name")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타조인
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        queryFactory = new JPAQueryFactory(em);
        List<Member> result = queryFactory
                .select(member)
                .from(member, team) //from절에서 나열
                .where(member.name.eq(team.name))
                .fetch();
        //모든 회원을 가져오고 모든 팀을 가져온 다음에 모두 조인을 해서 where절에서 필터링 (DB에서 최적화 함)

        assertThat(result)
                .extracting("name")
                .containsExactly("teamA", "teamB");
    }

    /**
     * join on
     * ex)회원과 팀을 조인하면서 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     */
    @Test
    public void join_on_filtering() {
        queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory //select 대상의 타입이 여러가지이므로 Tuple을 반환한다.
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA")) //맴버는 다 가져오는데, 팀은 이름이 'teamA'만 것만 가져온다.
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple =" + tuple);
        }
    }

    /**
     * 연관 관계가 없는 엔티티 외부 조인
     * 회원의 이름과 팀 이름과 같은 대상 외부 조인
     */
    @Test
    public void join_on_no_relation() {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.name.eq(team.name)) //member.team으로 들어가면 ID로 매칭을 함, ㅡ드ㅠㄷㄱF
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple =" + tuple);
        }
    }

    @PersistenceUnit
    EntityManagerFactory emf;
    //엔티티 메니저를 만드는 팩토리
    //이 객체는 .getPersistenceUnitUtil().isLoaded(엔티티)와 같이 로딩된 엔티티인지 알려주는 메소드를 가지고 있다.

    @Test
    public void fetchjoin_not() {
        em.flush();
        em.clear();

        queryFactory = new JPAQueryFactory(em);
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.name.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치 조인 미적용").isFalse(); //as는 디스크립션을 작성하는 메소드
    }

    @Test
    public void fetchjoin_use() {
        em.flush();
        em.clear();

        queryFactory = new JPAQueryFactory(em);
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.name.eq("member1"))
                .fetchOne();

        boolean loaded = emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("패치 조인 미적용").isTrue(); //as는 디스크립션을 작성하는 메소드
    }

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery() {
        queryFactory = new JPAQueryFactory(em);
        QMember memberSub = new QMember("memberSub"); //subQuery의 member가 외부의 member가 겹치면 안되서 다른 알리아스를 가진 MEMBER 생성
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(
                        select(memberSub.age.max()) //알리아스를 다르게 두기 위함
                                .from(memberSub)
                )).fetch();
        assertThat(result).extracting("age").containsExactly(40);
    }

    /**
     * 나이가 평균 이상인 회원
     */
    @Test
    public void subQuery_goe() {
        queryFactory = new JPAQueryFactory(em);
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(
                        select(memberSub.age.avg())
                                .from(memberSub)
                )).fetch();

        assertThat(result).extracting("age").containsExactly(30, 40);
    }

    /**
     * 나이가 10살 초과
     * (효율적이지 않은 쿼리지만 예제상으로 만듦)
     */
    @Test
    public void subQuery_in() {
        queryFactory = new JPAQueryFactory(em);
        QMember memberSub = new QMember("memberSub");

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10)) //나이가 10 초과
                )).fetch();

        assertThat(result).extracting("age").containsExactly(20, 30, 40);
    }

    @Test
    public void subQuery_select() {
        //queryFactory = new JPAQueryFactory(em);
        QMember memberSub = new QMember("memberSub");

        List<Tuple> result = queryFactory
                .select(member.name,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple t : result) {
            System.out.println("tuple = " + t);
        }
    }

    @Test
    public void basicCase() {
        queryFactory = new JPAQueryFactory(em);
        List<String> result = queryFactory.select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase() {
        queryFactory = new JPAQueryFactory(em);
        List<String> result = queryFactory
                .select(new CaseBuilder() //복잡한 케이스를 작성할 경우 CaseBuilder를 사용한다.
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void constant() {
        queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory
                .select(member.name, Expressions.constant("A"))
                .from(member)
                .fetch();
        for (Tuple tupel : result) {
            System.out.println("tupel = " + tupel);
        }
    }


    @Test
    public void concat() {
        queryFactory = new JPAQueryFactory(em);
        //name_age 형태로 concat하기
        List<String> result = queryFactory
                .select(member.name.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.name.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void simpleProjection() {
        queryFactory = new JPAQueryFactory(em);
        List<String> result = queryFactory
                                .select(member.name)
                                .from(member)
                                .fetch();
        for(String s: result)
            System.out.println("s = " + s);
    }

    @Test
    public void tupleProjection() {
        queryFactory = new JPAQueryFactory(em);
        List<Tuple> result = queryFactory
                .select(member.name, member.age)
                .from(member)
                .fetch();
        for(Tuple tuple : result) {
            String name = tuple.get(member.name);
            Integer age = tuple.get(member.age);
            System.out.println("tuple = " + name + " , " + age);
        }
    }

    @Test
    public void findDtoByJPQL() {
        List<MemberDto> resultList = em.createQuery("select new study.querydsl.dto.MemberDto(m.name, m.age) from Member m", MemberDto.class)
                                        .getResultList();
        for(MemberDto dto : resultList)
            System.out.println("memberDto = " + dto);
    }


    @Test
    public void findDtoByQueryDsl() {
        queryFactory = new JPAQueryFactory(em);
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class, member.name, member.age))
                .from(member)
                .fetch();
        for (MemberDto dto : result)
            System.out.println("memberDto = " + dto);
    }

    @Test
    public void findDtoByField() {
        queryFactory = new JPAQueryFactory(em);
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class, member.name, member.age))
                .from(member)
                .fetch();
        for (MemberDto dto : result)
            System.out.println("memberDto = " + dto);
    }


    @Test
    public void findDtoByConstructor() {
        queryFactory = new JPAQueryFactory(em);
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class, member.name, member.age))
                .from(member)
                .fetch();
        for (MemberDto dto : result)
            System.out.println("memberDto = " + dto);
    }

    @Test
    public void findUserDto() {
        queryFactory = new JPAQueryFactory(em);
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                                            member.name.as("userName"),
                                            ExpressionUtils.as(JPAExpressions //서브쿼리, 이름이 없으므로 ExpressionUtils으로 두번째 파라미터로 알리아스를 정해줄 수 있다.
                                                            .select(memberSub.age.max())
                                                            .from(memberSub), "age")
                ))
                .from(member)
                .fetch();

        for (UserDto dto : result)
            System.out.println("memberDto = " + dto);
    }
}
