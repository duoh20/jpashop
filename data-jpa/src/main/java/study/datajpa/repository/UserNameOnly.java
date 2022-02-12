package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

public interface UserNameOnly {

    @Value("#{target.username + '' + tartget.age}")
    String getName(); // getter로 가져올 속성을 필드로 잡는다.

}
