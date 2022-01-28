package jpabook.jpashop.repository.order.query;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@AllArgsConstructor
//핵심 비즈니스 처리를 위한 로직은 OrderRepository에,
//화면을 위한 로직은 OrderQueryRepository에 넣어서 관심사를 분리
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDto> findOrderQueryDtos() {
        List<OrderQueryDto> result = findOrders(); //먼저 orders를 조회한다.

        result.forEach(o -> { //각 order 객체에 orderItems를 직접 조회하여 Dto에 매핑하여 데이터를 추가한다.
            List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
            o.setOrderItems(orderItems);
        });

        return result;
    }

    public List<OrderQueryDto> findOrders() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d", OrderQueryDto.class)
                .getResultList();
    }

    private List<OrderItemQueryDto> findOrderItems(Long orderId) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId", OrderItemQueryDto.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDto> findAllByDto_optimization() {
        //필요한 컬랙션을 정보를 한 번에 받아와서 메모리에 두었다가 조작하는 방식으로 리팩토링해보자.

        //먼저 orders를 조회한다.
        List<OrderQueryDto> result = findOrders();

        //orderId들을 뽑아 리스트를 만든다.
        List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

        //orderId에 엮인 orderItem들을 IN절로 한 번에 불러온다.
        List<OrderItemQueryDto> orderItems = em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count) " +
                                                            "from OrderItem oi " +
                                                            "join oi.item i " +
                                                            "where oi.order.id in :orderIds", OrderItemQueryDto.class)
                                                .setParameter("orderIds", orderIds)
                                                .getResultList();

        //key:orderId, value:List<OrderItemQueryDto>인 map으로 변환하여 저장한다.
        Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
                                                            .collect(Collectors.groupingBy(orderItemQueryDto-> orderItemQueryDto.getOrderId()));

        //기존에 조회한 order에 미리 조회한다.
        result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

        return result;
    }

    public List<OrderFlatDto> findAllByDto_flat() {
        return em.createQuery("select" +
                                " new jpabook.jpashop.repository.order.query.OrderFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)" +
                                " from Order o" +
                                " join o.member m" +
                                " join o.delivery d" +
                                " join o.orderItems oi" +
                                " join oi.item i", OrderFlatDto.class)
                .getResultList();
    }
}
