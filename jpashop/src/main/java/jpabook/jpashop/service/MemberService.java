package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)//데이터 변경 시 트랜잭션이 꼭 있어야한다.
//javax, springframework 패키지에 각각 @Transactional이 존재하는데, springframework 하위의 것을 사용하자.
//조회 시 이 옵션을 걸면 성능에 도움이 된다. 조회만 할 땐 가급적 넣어주다. 단, 쓰기가 팔요할 땐 readOnly 옵션을 걸면 안된다.
//쓰는 메소드에는 @Transaction만 걸어주자, 디폴트가 readOnly = false이다.
@RequiredArgsConstructor
public class MemberService {

    //setter injection
    //스프링이 주입하는 것이 아니라, setter로 직접 주입하는 방식이다.
    //테스트 케이스 작성 시 mock을 적용에 도움이 된다.
    //단점으로 런타임에 누군가 memberRepository를 바꿔버린다면? 위험성이 있으므로 생성자 주입 방식이 좋다.
    //필드는 애플리케이션 실행 후 변경하지 않고, 컴파일 체크를 위해 final을 적용하자.
    private final MemberRepository memberRepository;

//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    //constructor injection
//    @Autowired //이 어노테이션이 없어도 스프링이 자동으로 주입해줌
//                //롬복을 사용한다면 @RequiredArgConstructor를 사용해서 생략할 수 있다.
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
        //jpa에서 em.persist하면 영속성 컨텍스트에 member 객체를 올린다.
        //이 때 member의 id 필드가 키가 되어 key-value로 식별하기 때문에 id 값은 생성되어 있는 것이 보장된다.
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
        //이름이 일치하는 회원 수를 조회해서 검증하는 방법도 있다.
        //validation 로직의 문제점 : 만약 동시에 같은 이름으로 중복 검사한다면 중복을 거르지 못하고 두 개의 이름이 들어갈 수도 있다.
        //따라서 멀티스레드 환경을 고려하여 2중 체크하자. (ex. db에 unique 제약 조건을 걸어두기)
    }

    /**
     * 회원 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) { return memberRepository.findOne(memberId);}

    @Transactional
    public void update(Long id, String name) {
        memberRepository.findOne(id).setName(name); //@Transactional에 의해 변경 감지가 이루어짐
    }
}
