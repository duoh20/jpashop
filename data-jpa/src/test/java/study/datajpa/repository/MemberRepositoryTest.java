package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember() {
        System.out.println("memberRepository = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        //결과가 있을 수도 없을 수도 있기 때문에 Optional로 반환한다.
        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.get(); //원래 orElse()등을 사용해서 null처리를 해야한다.

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getName()).isEqualTo(member.getName());
        assertThat(findMember).isEqualTo(member);
    }


    @Test
    public void basicCRUD() {
        Member memberA = new Member("memberA");
        Member memberB = new Member("memberB");
        memberRepository.save(memberA);
        memberRepository.save(memberB);

        //단건 조회 검증
        Member findMemberA = memberRepository.findById(memberA.getId()).get();
        Member findMemberB = memberRepository.findById(memberB.getId()).get();
        assertThat(findMemberA).isEqualTo(memberA);
        assertThat(findMemberB).isEqualTo(memberB);

        //더티체킹 확인
        findMemberA.setName("memberName");
        assertThat(findMemberA.getName()).isEqualTo("memberName");

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //카운트 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(memberA);
        memberRepository.delete(memberB);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByNameAndAgeGreaterThan() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members  = memberRepository.findByNameAndAgeGreaterThan("AAA",  15);

        assertThat(members.get(0).getName()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);
        assertThat(members.size()).isEqualTo(1);
    }

    @Test
    public void testNamedQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members  = memberRepository.findByName("AAA");

        Member findMember = members.get(0);
        assertThat(findMember).isEqualTo(member1);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members  = memberRepository.findUser("AAA", 10);

        assertThat(members.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUserNameList() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> nameList = memberRepository.findUserNameList();
        for (String name : nameList) {
            System.out.println("name = " + name);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member = new Member("AAA", 10);
        member.setTeam(team);
        memberRepository.save(member);

        List<MemberDto> memberDtos = memberRepository.findMemberDto();

        for (MemberDto dto : memberDtos) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 10);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));

        for (Member m : members) {
            System.out.println("name = " + m.getName());
        }
    }

    @Test
    public void returnType() {
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 10);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> list = memberRepository.findMemberListByName("AAA");
        Member member = memberRepository.findMemberByName("AAA");
        Optional<Member> optionalMember = memberRepository.findOptionalMemberByName("AAA");

        System.out.println("collection == " + list.get(0).getName());
        System.out.println("single == " + member.getName());
        System.out.println("optional == " + optionalMember.get().getName());
    }

    @Test
    public void paging() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "name"));
        //PageRequest 클래스의 of(페이지, 데이터갯수, 정렬조건);를 사용하여 페이징 조건을 설정한다.
        //페이지는 제로 인덱스임


        //when
        //반환 타입을 Page로 받으면 토탈 카운트 쿼리까지 함께 생성하여 보낸다.
        Page<Member> page = memberRepository.findPageMemberByAge(age, pageRequest);

        //[주의]외부에 데이터를 보낼 때에는 DTO에 감싸서 보내자.
        page.map(member -> new MemberDto(member.getId(), member.getName(), member.getTeam().getName()));

        //then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();
        for(Member member : content) {
            System.out.println("memebr = " + member);
        }
        System.out.println(totalElements);

        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getTotalElements()).isEqualTo(5); //getTotalElements() 전체 콘텐츠 수
        assertThat(page.getNumber()).isEqualTo(0); //getNumber() 페이지 번호 가져오기
        assertThat(page.getTotalPages()).isEqualTo(2); //getTotalPages() 총 페이지 갯수
        assertThat(page.isFirst()).isTrue(); //isFirst() 첫번째 페이지인지 확인
        assertThat(page.hasNext()).isTrue(); //hasNext() 다음 페이지가 있는지 확안
    }

    @Test
    public void pagingSlice() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "name"));
        //PageRequest 클래스의 of(페이지, 데이터갯수, 정렬조건);를 사용하여 페이징 조건을 설정한다.
        //페이지는 제로 인덱스임


        //when
        //반환 타입을 Page로 받으면 토탈 카운트 쿼리까지 함께 생성하여 보낸다.
        Slice<Member> page = memberRepository.findSliceMemberByAge(age, pageRequest);


        //then
        List<Member> content = page.getContent();
        assertThat(content.size()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0); //getNumber() 페이지 번호 가져오기
        assertThat(page.isFirst()).isTrue(); //isFirst() 첫번째 페이지인지 확인
        assertThat(page.getNumberOfElements()).isEqualTo(3); //getNumberOfElements() 현재 페이지에 나올 데이터 수
        assertThat(page.hasNext()).isTrue(); //hasNext() 다음 페이지가 있는지 확안
    }

    @Test
    public void bulkUpdate() {
        //given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 35));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        em.flush(); //같은 트랜잭션이면 같은 EntityManager를 사용한다.
        em.clear();

        System.out.println(memberRepository.findByName("member5").get(0));
        //flush()와 clear()를 실행하지 않으면 member5의 나이가 36이 아니라 35로 저장되는 문제가 발생한다.
        //이는 영속성 컨텍스트에서 commit 시점에 한 번에 쿼리를 날리기 때문이다.
        //벌크 수정 쿼리는 바로 업데이트 되기 때문에 영속성 컨텍스트를 비워야한다.
        //혹은 벌크 수정 쿼리를 작성한 JPA 레포지토리 인터페이스에 @Modifying(clearAutomatically = true)을 걸어주자.


        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        //given
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1",10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        Member member3 = new Member("member2", 10, teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        em.flush();
        em.clear(); //영속성 컨텍스트의 캐시를 DB에 저장 후 초기화

        //when
        //N+1  문제가 발생하는 쿼리
        List<Member> members = memberRepository.findAll();
        for(Member member : members) {
            System.out.println("member = " + member.getName());
            System.out.println("member.team = " + member.getTeam().getName());
        }

        //N+1을 해결하기 위한 방법 : Fetch Join
        // Team을 join하는 쿼리 메소드를 추가한다.
        // 쿼리가 하나로 생성되는 것을 확인할 수 있다.
        List<Member> membersFetchJoin = memberRepository.findMemberFetchJoin();

        //fetch 조인을 @Query가 아니라 JPA 쿼리 메소드처럼 사용하고 싶다면
        //JPA 리포지토리의 메소드에 @EntityGraph 어노테이션을 붙여준다.
        List<Member> membersEntityGraph = memberRepository.findMemberEntityGraphBy();
    }

    @Test
    public void queryHint() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear(); //member1 생성 후 영속성 컨텍스트 캐시 초기화

        //when
        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setName("member2");

        em.flush();
        //flush()할 때 변경 감지가 일어나는데, 덕분에 따로 save()하지 않아도 저장된다.
        //히지만 변경 감지를 위해 원본 데이터를 하나 더 들고 있어야하기 때문에 메모리를 소비하는 단점이 있다.
        //변경 감지 필요없이 조회만 필요할 경우 더티 체킹이 일어나지 않도록 하이버네이트에서 기능을 제공하지만
        //JPA에서는 제공하지 않고 힌트를 사용한다.
    }

    @Test
    public void lock() {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear(); //member1 생성 후 영속성 컨텍스트 캐시 초기화

        //when
        //실행해서 jpa 쿼리를 확인해보면 select for update 구문을 확인할 수 있다.
        //select for update는 mySQL에서 데이터 수정 중에 다른 트랜잭션이 일어나지 않도록 lock을 거는 기능이다.
        List<Member> findMember = memberRepository.findLockByName("member1");
    }

    //사용자 정의 레포지토리
    @Test
    public void callCustom() {
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void JpaEventBaseEntity(){
        Member member = new Member("member1");
        memberRepository.save(member);

    }
}
