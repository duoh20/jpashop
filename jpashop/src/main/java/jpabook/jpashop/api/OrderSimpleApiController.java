package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.OrderSimpleQueryDto;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * XToOne 관계에서 성능 최적화
 * (OneToOne, ManyToOne)
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        for(Order order: orders) {
            order.getMember().getName(); //강제로 LazyLoading 초기화
            order.getDelivery().getId(); //강제로 LazyLoading 초기화
        }
        return orders;
    }
    
    @GetMapping("/api/v2/simple-orders") //최악의 경우 N+1 문제가 있음
    public List<SimpleOrderDto> orderV2() {

        return orderRepository.findAllByString(new OrderSearch())
                              .stream()
                              .map(o -> new SimpleOrderDto(o)) //또는 map(SimpleOrderDto(o)::new) : 람다식 메서드 참조 => 클래스명::메서드명 / 참조변수::메서드명
                .collect(Collectors.toList());
    }
    
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) { //DTO는 엔티티를 파라미터로 받는 것은 크게 문제가 되지 않는다.
            orderId = order.getId();
            name = order.getMember().getName(); //강제 Lazy 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); //강제 Lazy 초기화
        }
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        return orderRepository.findAllWithMemberRepository()
                              .stream()
                              .map(SimpleOrderDto::new)
                              .collect(Collectors.toList());
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderRepository.findOrderDtos();
    }
}
