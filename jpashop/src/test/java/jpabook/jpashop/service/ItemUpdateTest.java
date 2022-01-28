package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ItemUpdateTest {

    @Autowired
    EntityManager em;

    @Test
    public void updateTest() throws Exception {
        //given
        //임의의 데이터를 DB에서 찾아오면
        Book book = em.find(Book.class, 1L);

        //Transaction
        //트랜잭션 안에서 JPA가 변경을 감지하면 Transaction commit 시점에 update한다. (== dirty checking)
        book.setName("임의의 이름");

        //문제는 영속성 컨텍스트가 더 이상 관리하지 않는 "준영속 엔티티"다.
        /****** 준영속성 엔티티 수정 전략 ******/
        //1. 변경 감지 (권장하는 방식)
//        @Transactional
//        public void updateItem(Long itemId, Book bookParam) {
//            Item findItem = itemRepository.findOne(itemId); //findItem()으로 찾아온 Item 객체는 영속 상태이다,
//            findItem.setPrice(bookParam.getPrice());
//            findItem.setName(bookParam.getName());
//            findItem.setStockQuantity(bookParam.getStockQuantity());
//            //param으로 받아온 값을 set만 해주면 @Transactional에 의해 영속 객체인 item의 변경된 내용을 감지하여 업데이트 명령을 따로 하지 않아도 업데이트 된다.
//        }
//
        //2. 병합(merge)
//        //준영속 상태의 엔티티를 수정할 때 사용하는 전략
//        @Transactional
//        public void updateItemByMerge(Book bookParam) { //파라미터로 넘어온 준 영속성 상태의 엔티티
//           Book mergeBook = em.merge(bookParam); //JPA.merge()를 실행하면 JPA가 자동으로 머지해준다.
//        }
    }

}
