package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByNameAndAgeGreaterThan(String name, int age);

    @Query(name = "Member.findByName") //Member에서 findByName이라는 이름의 NamedQuery를 찾아 온다.
    List<Member> findByName(@Param("name") String name); //@Param으로 NamedQuery에 넣은 name 파라미터를 매핑해준다.

    //실무에서 많이 사용됨
    @Query("select m from Member m where m.name = :name and m.age = :age")
    List<Member> findUser(@Param("name") String naem, @Param("age") int age);

    //값 조회: 유저명 조회
    @Query("select m.name from Member m")
    List<String> findUserNameList();

    //DTO 조회
    //생성자로 new하듯 DTO 명을 작성하여 생성자 매칭되도록 작성해주어야한다. (new operation)
    @Query("select new study.datajpa.dto.MemberDto(m.id, m.name, t.name)" +
            " from Member m" +
            " join m.team t")
    List<MemberDto> findMemberDto();
}