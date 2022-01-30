package study.datajpa.repository;

import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TeamJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Team save (Team team) {
        em.persist(team);
        return team;
    }
    //update (변경) 메소드가 없는 이유?
    //JPA는 변경 감지 기능을 통해 엔티티를 변경한다.
    //java의 컬랙션처럼 값을 변경(setter)하면 값이 바뀌듯 JPA도 같은 방식으로 객체를 업데이트한다.

    public void delete (Team team) {
        em.remove(team);
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class).getResultList();
    }

    public Long count() {
        return em.createQuery("select count(t) from Team t", Long.class).getSingleResult();
    }
}
