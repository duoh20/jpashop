package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name="orders")
public class Order {

    @Id @GeneratedValue
    @Column(name="order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //MEMBER 테이블과 관계를 설정해줌
    @JoinColumn(name = "member_id") //어떤 키(FK)로 조인할 것인지 적어주어 연관관계의 주인임을 명시한다.
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL) //cascade 옵션이 걸려있어서 orderItems 컬렉션에 연관된 것들이 영향받는다
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL) //일대일관계는 FK를 어떤 방향의 테이블에 되는데, 주로 엑세스를 많이 하는 곳에 KF를 둔다. 여기서는 주문으로 배송을 찾으므로 FK를 Order에 두었다.
    @JoinColumn(name="delivery_id")
    private Delivery delivery; //cascade 옵션이 있어 order를 저장하면 delivery도 저장된다.

    private LocalDateTime orderDate; //Java8에서는 LocalDateTime을 쓰면 하이버네이트가 알아서 처리해줌

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //enum 타입의 상태, 주문과 취소 상태가 있다.

    //==연관관계 메서드==//
    //양방향 관계일 때, 연관관계를 미리 정의하는 메서드를 말한다
    //연관관계 메서드 정의 위치는 연관관계를 가진 엔티티 사이에서 핵심적으로 컨트롤하는 쪽에서 이 메서드를 들고 있는 것이 좋다.
    public void setMember(Member member) {
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        //order와 orderItems도 연관관계이므로 편하게 묶어준다.
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public static void main(String[] args) {
        Member member = new Member();
        Order order = new Order();

        member.getOrders().add(order);
        order.setMember(member);
    }

    //==생성 메서드==//
    //주문을 생성할 때 여러가지 연관 관계가 발생하여 복잡하다.
    //이럴때 생성 메서드를 정의해두면 필수 정보 누락없이 생성 가능하며 생성 메서드 로직 변경이 쉽다.
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems) {
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); //주문 상태 설정
        order.setOrderDate(LocalDateTime.now()); //주문 시간 설정
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     * 배송 완료 상태일 경우 주문을 취소할 수 없다.
     */
    public void cancel() {
        if(delivery.getStatus() == DeliveryStatus.COMP) {
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(OrderStatus.CANCEL);
        for(OrderItem orderItem : orderItems) orderItem.cancel();
    }

    //==조회 로직==//
    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice() {
        int totalPrice = 0;
        for (OrderItem orderItem : orderItems) totalPrice += orderItem.getTotalPrice();
        return totalPrice;
    }
}
