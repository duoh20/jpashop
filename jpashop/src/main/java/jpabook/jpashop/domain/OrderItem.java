package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//생성자 접근 레벨을 protected로 설정하여 다른 곳에서 임의로 생성을 방지한다.
//protected OrderItem() {}으로 작성해도 되지만 롬북의 어노테이션을 이용하자.
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_itme_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문 가격
    private int count; //주문 수량

    //==생성 로직==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        //orderItem 생성
        OrderItem orderItem  = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        //Item 재고 감소
        item.removeStock(count);
        return orderItem;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     * 취소되면 재고가 취소된 수량 만큼 재고가 증가되어야한다.
     * */
    public void cancel() {
        getItem().addStock(count);
    }

    /**
     * 주문 상품 가격 합계
     */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
