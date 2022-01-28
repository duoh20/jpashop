package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberJpaRepository {

    @PersistenceContext //스프링 컨테이너가 JPA의 EntityManager(영속성 컨텍스트)를 가지고 온다.
    private EntityManager em; //JPA가 알아서 CRUD 쿼리를 DB에 날려사 가져옴

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

}
