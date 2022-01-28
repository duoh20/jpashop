package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class OrderForm {
    private Long orderId;
    private Long itemId;
    private Long memberId;
    private int count;
}
