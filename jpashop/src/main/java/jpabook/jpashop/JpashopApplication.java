package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {
	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	//Lazy 로딩으로 인한 참조 에러 해결을 위한 모듈
	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hm = new Hibernate5Module();
		//만약 지연로딩한 객체를 null이 아니라 로딩하여 보여주고 싶다면?
		//	hm.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hm;
	}
}
