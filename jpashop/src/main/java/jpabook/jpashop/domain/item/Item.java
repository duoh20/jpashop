package jpabook.jpashop.domain.item;

import jpabook.jpashop.domain.Category;
import jpabook.jpashop.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)//상속관계일 경우, 부모에 전략을 설정해주어야 한다.
@DiscriminatorColumn(name="dTyoe") //상속받은 child들을 구분할 컬럼
public abstract class Item { //상속관계 매핑을 위해 추상 클래스로 선언
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;

    @ManyToMany(mappedBy = "items", fetch = FetchType.LAZY)
    private List<Category> categories = new ArrayList<>();

    //==비즈니스 로직==//
    //데이터를 가지고있는 쪽에 핵심 비즈니스 로직을 두어야 응집도가 높다.
    //재고 수량은 setter, getter로 가져와서 바깥에서 변경하지 않고, 엔티티 안에서 관련 로직을 구현하는 편이 객체지향적인 설게이다.
    /**
     * stock 증가
     */
    public void addStock(int quantity) {
        this.stockQuantity += quantity;
    }

    /**
     * stock 감소
     */
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("stock is not enough.");
        }
        this.stockQuantity = restStock;
    }
}
