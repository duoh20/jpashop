package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    //@PersistenceContext //이 어노테이션을 만나면 스프링이 생성한 엔티티 메니저를 주입해줌
    //private EntityManager em;

    //!!  EntityManagerFactory를 직접 주입 받고싶다면 이렇게 하자
    //    @PersistenceUnit
    //    private EntityManagerFactory emf;

    //클래스에 RequiredArgsConstru@ctor를 주고, EntityManager에 final을 주어 컴파일 시점에 누락되지 않도록 한다.
    private final EntityManager em;

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id){
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {

        return em.createQuery("select m from Member m", Member.class).getResultList();
        //createQuery(String JPQL, <T> returnType) 쿼리를 직접 입력하여 반환
        //테이블을 대상으로하는 sql과 달리 jpql은 엔티티 클래스를 대상으로 한다.
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name",Member.class)
                .setParameter("name", name)
                .getResultList();
        //createQuery 메소드 작성 후 setParameter로 파라미터 값을 설정해준 후 리스트로 반환받는다.
    }
}
