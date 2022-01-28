package jpabook.jpashop.service.query;

import jpabook.jpashop.domain.OrderItem;
import lombok.Getter;

@Getter //@Data는 Getter, Setter, ToString 모두 들고 있으므로 프로젝트 컨밴션에 따라 @Getter를 쓸지 @Data를 쓸지 선택하자.
public class OrderItemDto {
    private Long itemId;
    private String name; //상품명
    private int orderPrice; //주문 가격
    private int count; //주문 수량

    public OrderItemDto(OrderItem orderItem) {
        this.itemId = orderItem.getId();
        this.name = orderItem.getItem().getName();
        this.orderPrice = orderItem.getOrderPrice();
        this.count = orderItem.getCount();
    }
}
