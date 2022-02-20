package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(value = false)
class QuerydslApplicationTests {

	//@PersistenceContext //JAVA 표준 스펙, 스프링 최신 버전에서는 @Autowired를 사용하면 된다. 다른 컨테이너를 사용할 가능성이 있다면 이 어노테이션을 사용해야한다.
	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {

		//given
		Hello hello = new Hello();
		em.persist(hello);

		//Querydsl 최신 버전에서는 JPAQueryFactory 사용하기를 권장한다.
		JPAQueryFactory query = new JPAQueryFactory(em);

		//Q클래스 객체 생성
		QHello qHello = new QHello("h");

		//id로 단건 조회 구현
		Hello result = query.selectFrom(qHello)
						 .fetchOne();

		//QueryDsl로 조회한 객체가 잘 조회되었는지 확인
		assertThat(result).isEqualTo(hello);
		assertThat(result.getId()).isEqualTo(hello.getId());
	}
}
