package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter  @Setter
public class Category {

    @Id
    @GeneratedValue
    @Column(name="category_id")
    private Long id;

    private String name;

    @ManyToMany //ManyToMany는 조인할 Table이 필요하다. 컬랙션 관계를 양쪽에 가질 수 없기 때문에 일대다 다대일 관계로 풀어내는 중간 테이블이 필요하다.
    @JoinTable(name = "category_item", //중간 테이블 이름
        joinColumns = @JoinColumn(name = "category_id"), //중간 테이블의 카테고리 id
        inverseJoinColumns = @JoinColumn(name = "item_id")) //중간 테이블의 item으로 들어가는 id
    private List<Item> items = new ArrayList<>();
    //단, 실무에서는 중간 테이블로 연관 관계를 만들진 않는다, 필드를 추가하기 어렵기 때문이다. 예시로 보자.

    /***** 같은 엔티티 셀프 양방향 관계를 설정함 ****/
    //Q: 카테고리 구조를 어떻게 연결할 것인가?
    //A: 셀프양방향 관계를 설정해서 해결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_id")
    private Category parent; //내 부모니까 내 타입 넣어줌

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>(); //자식은 여러개를 가질 수 있다.

    //==연관관계 메서드==//
    public void addChildCategory(Category children) {
        this.children.add(children);
        children.setParent(this);
    }
}
