package study.datajpa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.datajpa.entity.Member;
import study.datajpa.repository.MemberRepository;

import javax.annotation.PostConstruct;

@RestController
@RequiredArgsConstructor
public class MembeController {

    private final MemberRepository memberRepository;

    @GetMapping("/members/{id}")
    public String findMember(@PathVariable("id") Long id) {
        Member member = memberRepository.findById(id).get();
        return member.getName();
    }

    //도메인 클래스 컨버터
    @GetMapping("/members2/{id}") //스프링이 맴버로 컨버팅해줌
    public String findMember2(@PathVariable("id") Member member) {
        return member.getName();
    }

    @PostConstruct //테스트를 위해 데이터 생성
    public void init() {
        memberRepository.save(new Member("userA"));
    }
}
