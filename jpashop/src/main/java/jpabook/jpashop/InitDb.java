package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


/**
 * 테스트 데이터 생성 클래스
 */
@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct //애플리케이션 로딩 시점에 호출
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
        //서버를 띄우면 ComponentScan에 의해 스프링 빈이 추가된 후, @PostConstruct를 호출한다.
        //dbInit()메소드를 따로 만드는 이유: 별도의 빈으로 등록해야 트랜잭션 처리가 가능하다.
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {

        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("userA", new Address("서울", "1", "10054"));
            em.persist(member);

            Book book1 = createBook("JPA1 Book", 10000, 100, "E1204");
            em.persist(book1);

            Book book2 = createBook("JPA2 Book", 20000, 100, "E5043");
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1); //OrderItem의 생성 메소드 이용하여 객체 생성
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", new Address("여수", "2", "54301"));
            em.persist(member);

            Book book1 = createBook("SPRING1", 30000, 300, "E5503");
            em.persist(book1);

            Book book2 = createBook("SPRING2", 40000, 400, "E5504");
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1); //OrderItem의 생성 메소드 이용하여 객체 생성
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Member createMember(String name, Address address) {
            Member member = new Member();
            member.setName (name);
            member.setAddress(address);
            return member;
        }

        private Book createBook(String name, int price, int stockQuantity, String isbn) {
            Book book = new Book();
            book.setName(name);
            book.setPrice(price);
            book.setStockQuantity(stockQuantity);
            book.setIsbn(isbn);
            return book;
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }
}
