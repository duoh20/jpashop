package study.querydsl.dto;

import lombok.Data;

@Data
public class MemberDto {

    private String name;
    private  int age;

    public MemberDto() {}

    public MemberDto(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
