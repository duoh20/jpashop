package jpabook.jpashop.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookForm {

    //Item의 공통 속성
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;

    //Book의 속성
    private String author;
    private String isbn;
}
