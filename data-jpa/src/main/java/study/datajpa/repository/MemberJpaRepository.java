package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

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

    public Optional<Member> findById(Long id) { //Optional로 조회하는 기능
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member); //member가 null이거나 아닐 수 있음, Optional로 감싸서 반환
    }

    public List<Member> findAll() {
        //여러 건을 조회하거나 필터를 걸어야할 때엔 JPQL을 사용한다,
        return em.createQuery("select m from Member m", Member.class).getResultList();
    }

    public long count() {
        //count 결과는 Long 타입으로 반환된다.
        //getSingleResult()는 결과 하나만, getResultList는 여러 결과를 반환
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findByNameAndAgeGreaterThan(String name, int age) {
        return em.createQuery("select m from Member m where m.name = :name and m.age > :age")
                .setParameter("name", name)
                .setParameter("age", age)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createNamedQuery("Member.findByName", Member.class)
                 .setParameter("name", name)
                 .getResultList();
    }

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m" +
                        "  from Member m" +
                        " where m.age = :age" +
                        " order by m.name desc")
                  .setFirstResult(offset) //몇부터 (zero index)
                  .setMaxResults(limit) //몇개의 데이터
                  .setParameter("age", age)
                  .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m)" +
                "  from Member m" +
                " where m.age = m.age", Long.class).getSingleResult();
    }

    //벌크성 수정 쿼리
    public int bulkAgePlus(int age) {
        return em.createQuery("update Member m set m.age = m.age + 1" +
                                      " where m.age >= :age")
                  .setParameter("age", age)
                  .executeUpdate();
    }
}
