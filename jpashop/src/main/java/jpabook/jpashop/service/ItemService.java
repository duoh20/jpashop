package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() { return itemRepository.findAll(); }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }


    /****** 준영속성 엔티티 수정 전략 ******/
    //1. 변경 감지
    @Transactional
    public void updateItem(Long itemId, Book bookParam) {
        Item findItem = itemRepository.findOne(itemId); //findItem()으로 찾아온 Item 객체는 영속 상태이다,
        findItem.setPrice(bookParam.getPrice());
        findItem.setName(bookParam.getName());
        findItem.setStockQuantity(bookParam.getStockQuantity());
        //param으로 받아온 값을 set만 해주면 @Transactional에 의해 영속 객체인 item의 변경된 내용을 감지하여 업데이트 명령을 따로 하지 않아도 업데이트 된다.
    }

    //2. 병합(merge)
    //준영속 상태의 엔티티를 수정할 때 사용하는 전략
    @Transactional
    public void updateItemByMerge(Long itemId, Book bookParam) {
        Item findItem = itemRepository.findOne(itemId); //findItem()으로 찾아온 Item 객체는 영속 상태이다,
    }
}
