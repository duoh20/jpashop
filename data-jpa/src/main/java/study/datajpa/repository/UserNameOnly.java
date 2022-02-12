package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly { //1.interface projections

    @Value("#{target.name + ' ' + target.age}") //2.openProjections
    String getName(); // getter로 가져올 속성을 필드로 잡는다.

}
