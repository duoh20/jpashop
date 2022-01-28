package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

//@Controller + @ResponseBody = @RestController
@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * 회원 추가 */
    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
                                            //@Valid 유효성 검사(javax.validation.Valid), 엔티티에 @NotEmpty가 걸려있는 필드에 null 들어오면 익셉션 발생
                                            //@RequestBody Json으로 받아온 데이터를 Member애 매핑
        return new CreateMemberResponse(memberService.join(member));

        //서비스가 커짐에 따라 엔티티 스펙이 변경될 가능성이 많다. 함부로 엔티티를 변경하면 장애가 발생할 수 있다.
        //엔티티에 외부에 노출 시켜 바로 파라미터로 노출시켜 바인딩 받지 말고 Dto를 만들어서 사용하자.
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        return new CreateMemberResponse(memberService.join(member));
    }
    //V1의 장단점
    //클래스를 따로 만들지 않아도 된다는 장점 밖에 없다.
    //엔티티 스펙 변경에 대처하기 어렵고, API 스펙이 불분명하다.
    //V2의 장점
    //!!!!반드시 DTO를 사용하자!!!
    // 1.기존 API 스펙에 영향을 주지 않고 엔티티 변경 가능
    // 2.엔티티 스펙을 바꾸게 되면 컴파일 에러가 나서 미리 에러 파악 가능
    // 3.API 파라미터 스펙이 파악에 도움됨(요청/응답 파라미터가 분명해짐)

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class CreateMemberResponse {
        private Long id;
    }


    /**
     * 회원 수정 */
    @PutMapping("/api/v1/members/{id}")
    public UpdateMemberResponse updateMemberResponse(@PathVariable("id") Long id,
                                                     @RequestBody @Valid UpdateMemberRequest request) {
        memberService.update(id, request.getName());
        return new UpdateMemberResponse(memberService.findOne(id).getId());

        //command와 query는 분리하자.
        //개발 스타일에 따라 다르지만,
        //update()는 수정을 위한 command이므로 변경 후 return하지 않고,
        //member를 따로 조회하여 돌려주자.
    }


    @Data
    static class UpdateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
    }

    /**
     * 회원 목록 */
    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberDto> collect = findMembers.stream()
                                             .map(m -> new MemberDto(m.getName()))
                                             .collect(Collectors.toList());
        return new Result(collect);
        //List나 Collection으로 감싸면 json 배열 타입으로 바인딩되어 유연성이 떨어진다.
        //따라서 Result라는 Object 타입의 껍데기를 감싸게 하여 json 객체 타입으로 반환한다.
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }
}
