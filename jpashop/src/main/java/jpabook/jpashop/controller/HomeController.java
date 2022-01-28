package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
//slf4j는 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음이다.
//인터페이스를 사용해 로깅을 구현하는 것의 장점은 나중에 로깅 라이브러리를 변경하더라도 코드 레벨의 변경이 필요 없다는 점이다.
public class HomeController {

    //롬복이 제공하는 @Slf4j 애노테이션으로 처리할 수도 있지만,
    //org.slf4j 로거를 필드에서 구현하려면 아래와 같이 작성한다.
    //Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping("/")
    public String home() {
        log.info("Home Controller");
        return "home";
    }
}