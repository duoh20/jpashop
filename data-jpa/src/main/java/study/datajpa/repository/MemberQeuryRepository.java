package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQeuryRepository {
    //예를 들어 복잡한 로직이 필요한 경우 그냥 Repository를 직접 만들어서 사용해도 된다.
    //커맨드와 쿼리 분리, 핵심 비지니스와 아닌 것을 변경, 라이프 사이클에 따라 달라지는 변경점을 살펴보고
    //상황에 맞춰 커스텀 인터페이스를 사용하거나, 클래스 래포지토리를 만들어 사용하자.

    private final EntityManager em;

    List<Member> findAllMembers() {
        return em.createQuery("select m from Member m").getResultList();
    }
}
