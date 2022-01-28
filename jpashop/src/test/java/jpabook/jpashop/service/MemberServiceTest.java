package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional //스프링 테스트에서 이 애노테이션을 사용하면 Rollback한다.
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired EntityManager em;


    @Test
    public void 회원가입() throws Exception {
        //Test 기법
        //given
        Member member = new Member();
        member.setName("kim");

        //when
        Long savedId = memberService.join(member);

        //then
        em.flush(); //콘솔에서 insert 쿼리를 확인하고 싶을 때 사용한다. 실제 저장되진 않는다.
        assertEquals(member, memberRepository.findOne(savedId));
        //Test 클래스에서 @Transaction을 걸면 테스트한 후 롤백해버린다.

        //콘솔에서 쿼리 결과를 보면 JPA는 persist를 했지만, insert문이 적용되지 않은 것을 확인할 수 있다.
        //DB에 실제 insert가 이뤄지려면 @Rollback(false) 애노테이션을 추가한다.
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("kim");
        Member member2 = new Member();
        member2.setName("kim");

        //when
        memberService.join(member1);
        //방법1(jUnit5)
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> memberService.join(member2)); //이름이 중복되어서 예외가 발생해야한다.
        assertEquals("이미 존재하는 회원입니다.", thrown.getMessage());

//        방법2: 직접 try-catch
//        try {
//            memberService.join(member2); //이름이 중복되어서 예외가 발생해야한다.
//        } catch (IllegalStateException e) {
//            return;
//        }

//        //then
//        fail("예외가 발생해야 한다."); //여기에 오면 실패해야 하는 테스트임!
//        //member2를 추가할 때 이미 예외처리되서 이 메소드를 빠져나가야하기 때문이다.
    }
}