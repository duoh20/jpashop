package study.datajpa.dto;

import lombok.Data;

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
}
