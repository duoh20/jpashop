package study.datajpa.dto;

import lombok.Data;
import study.datajpa.entity.Member;

@Data //Getter, Setter 다 포함되어있으니 엔티티에는 주면 안됨!
public class MemberDto {

    private Long id;
    private String name;
    private String teamName;

    public MemberDto(Long id, String name, String teamName) {
        this.id = id;
        this.name = name;
        this.teamName = teamName;
    }

    //엔티티로 받아와서 파라미터를 줄일 수도 있다.
    public MemberDto(Member member) {
        this.id = member.getId();
        this.name = member.getName();
    }
}
