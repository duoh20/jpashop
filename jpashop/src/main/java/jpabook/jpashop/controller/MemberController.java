package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public  String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        //memberForm이라는 빈 껍데기를 들고 createMemberForm.html로 이동한다.
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result) {
        //thymleaf는 spring과 통합이 잘 되어있다.
        //파라미터 앞에 @Valid 애노테이션을 사용하면 클래스에 정의해둔 유효성을 검사한다.

        if(result.hasErrors()) {//validate한 것에 오류가 있으면 BindingResult를 돌려줌
            return "members/createMemberForm";
            //thymeleaf-spring의 도움으로 WhiteError 페이지가 아니라 다시 입력 화면으로 돌아가 밸라데이션에 사용된다.
            //${#fields.hasErrors('name')}와 같이 에러를 판단하거나, th:errors="*{name}"에서 에러 메세지를 가져와 화면에 표시할 수 있다.
        }
        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());
        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/"; //회원가입 성공 시 home으로 리다이렉트
    }

    @GetMapping("/members")
    public String list(Model model) {
        model.addAttribute("members", memberService.findMembers());
        //화면에 엔티티를 뿌릴 때 모두 사용할 수 있기 때문에 Member로 보냈지만, 가능하다면 화면에 필요한 것만 추려서 DTO로 넘기는 것이 좋다.
        //특히 API를 만들 때에는 절대 엔티티를 웹으로 반환하면 안된다.
        //엔티티 변경으로 인해 중요한 정보가 추가될 경우 함께 넘어갈 수도 있고, API 스펙이 변경되므로 절대 엔티티를 보내면 안된다.
        return "members/memberList";
    }
}
