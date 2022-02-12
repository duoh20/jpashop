package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import study.datajpa.entity.Item;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired ItemRepository itemRepository;

    @Test
    public void save() {

        /*
        * 1. Id가 @GeneratedValue일 전략을 사용하는 경우
        * @GeneratedValue는 JPA가 persist할 때 Id가 생성된다.
        * 이렇게 item 객체에 id를 set하지 않은 상태에서 save를 하면
        * em.persist(item)을 실행한다.
        * */
//        Item item = new Item();
//        itemRepository.save(item);


        /*
         * 1. Id를 직접 셋팅하는 경우
         * @GeneratedValue는 JPA가 persist하면 Id가 생성되는 것이다.
         * 이렇게 item 객체에 id를 set하지 않은 상태에서 save를 하면
         * em.persist(item)을 실행한다.
         * */
        itemRepository.save(new Item("PK0001"));
    }
}