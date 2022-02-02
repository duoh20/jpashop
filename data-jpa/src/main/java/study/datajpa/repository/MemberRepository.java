package study.datajpa.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

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

    //컬렉션 파라미터 바인딩
    //in절에 컬렉션 타입의 파라미터를 넣고 싶을 때,
    @Query("select m from Member m where m.name in :names")
    List<Member> findByNames(@Param("names") List<String> names);

    //다양한 반환 타입 설정 가능
    List<Member> findMemberListByName(String name); //컬랙션
    Member findMemberByName(String name); //단건
    Optional<Member> findOptionalMemberByName(String name); //단건 Optional

    //페이징
    @Query(value = "select m from Member m join m.team t",
           countQuery = "select count(m) from Member m") //select쿼리와 count 쿼리를 구분해 작성해줄 수 있다.
    Page<Member> findPageMemberByAge(int age, Pageable pageable);
    Slice<Member> findSliceMemberByAge(int age, Pageable pageable);

    //벌크성 수정 쿼리
    @Modifying(clearAutomatically = true) //JPA가 수정 쿼리임을 알 수 있게 작성해야함 (executeUpdate()와 같은 역할)
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);
}