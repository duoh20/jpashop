package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "study.datajpa.repository") //스프링 부트를 쓰면 이 어노테이션은 생략 가능
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		//Spring Security나 HttpSession에서 유저 아이디를 가져와 리턴해준다.
		//이 예제는 간단히 UUID를 리턴하도록 한다.
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
