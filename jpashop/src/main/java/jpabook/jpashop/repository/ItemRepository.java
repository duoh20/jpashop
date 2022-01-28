package jpabook.jpashop.repository;

import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        //item은 jpa에 저장하기 전까지 아이디 값이 없다. == 새로 생성한 객체하는 뜻
        //따라서 null이면 존재하지 않는 것이므로 신규로 등록하고 null이 아니면 업데이트한다.
        if(item.getId() == null) {
            em.persist(item);
        } else {
            em.merge(item);
        }

        /************ [매우 중요]변경 감지와 병합(mgerge) ************/
        //준영속성 컨텍스트는 JPA가 상태를 트래킹하고 있지 않아 다른 수정 전략이 필요하다.
        // 변경 감지 기능을 사용하거나, 병합(merge)를 사용하자.
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);

    }

    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class).getResultList();
    }
}
