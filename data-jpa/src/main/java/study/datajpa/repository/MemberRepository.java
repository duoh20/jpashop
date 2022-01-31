package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByNameAndAgeGreaterThan(String name, int age);

    @Query(name = "Member.findByName") //Member에서 findByName이라는 이름의 NamedQuery를 찾아 온다.
    List<Member> findByName(@Param("name") String name); //@Param으로 NamedQuery에 넣은 name 파라미터를 매핑해준다.
}