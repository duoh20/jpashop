package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

//@SpringRunner는 jUnit4에서 사용해야했지만, jUnit5에서 @SpringBootTest를 사용한다.
@SpringBootTest //스프링 빈을 주입 받아야하기 때문에 스프링 컨테이너가 필요함
@Transactional
@Rollback(false) //스프링테스트 기본이 롤백이므로 실제 쿼리를 보내지 않으므로 콘솔에 삽입,조회 쿼리가 보이지 않고 디비에도 저장되지 않는다.
//등록 쿼리를 보고 싶다면 @Rollback을 false로 주자.
class MemberJpaRepositoryTest {

    @Autowired MemberJpaRepository memberJpaRepository;

    @Test //Test import시 주의!! org.junit.jupiter.api.Test;임 (jupiter == jUnit5)
    public void testMember() {
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUserName()).isEqualTo(member.getUserName());
        assertThat(findMember).isEqualTo(member); //JPA는 같은 @Transaction 안에서 같은 인스턴스임이 보장된다.
    }
}