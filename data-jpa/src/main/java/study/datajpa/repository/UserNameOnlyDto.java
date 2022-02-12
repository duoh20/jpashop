package study.datajpa.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNameOnlyDto {

    private final String name;

    // !! 클래스 Projection 사용 시 파라미터의 이름과 필드가 매칭되어야한다.
}
