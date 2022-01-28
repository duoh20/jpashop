package jpabook.jpashop.repository;


import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class, id);
    }

    public List<Order> findAll_Example(OrderSearch orderSearch){

        String jpql = "select * " +
                      "  from order o " +
                      "  join o.member m " +
                      " where o.satus = :status " +
                      "   and m.name like :name";

        return em.createQuery(jpql, Order.class)
                 .setParameter("status", orderSearch.getOrderStatus())
                 .setParameter("name", orderSearch.getMemberName())
                 .setMaxResults(1000) //최대 1000건
                 .getResultList();
        //위 코드는 status나 name 파라미터를 넘기지 않으면 에러가 난다.
        //jpql을 if문을 사용하여 파랑미터 유무에 따라 빌드하는 방식도 있지만, 라이브러리를 사용하여 해결해보자
    }

    /**
     * JPA Criteria
     * JPA에서 제공하는 동적 쿼리 표준 스펙인 Criteria
     * 어떤 쿼리인지 한 눈에 보기 어려운 단점이 있어 유지보수가 어렵다.
     */
    public List<Order> findAll(OrderSearch orderSearch) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        //회원 이름 검색
        if(orderSearch.getMemberName() != null && StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        System.out.println("test=================>");
        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    public List<Order> findAllWithMemberRepository() {
        //한 방 쿼리로 Order에 속한 Member, Delivery 다 가져옴
        //fetch join은 JPA에만 있는 문법임
        return em.createQuery("select o from Order o " +
                        "join fetch o.member m " +
                        "join fetch o.delivery d", Order.class)
                  .getResultList();
    }

    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address) " +
                                "from Order o " +
                                "join o.member m " +
                                "join o.delivery d", OrderSimpleQueryDto.class)
                  .getResultList();

    }

    public List<Order> findAllWithItem(OrderSearch orderSearch) {
        return em.createQuery(
                        //"select o from Order o " +
                        //[문제점] 하나의 Order 안에 OrderItem-> Item이 여러개일때
                        //DB 입장에서 left join의 효과로 Item의 갯수만큼 결과 row를 돌려준다.
                        //(ex. 하나의 주문에 3개 상품이 담겨있으면 주문이 3개로 부풀려진다. 이는 바라는 결과가 아니다.)
                            "select distinct o from Order o " +
                            "join fetch o.member m " +
                            "join fetch o.delivery d " +
                            "join fetch o.orderItem oi " +
                            "join fetch oi.item i", Order.class)
                 .getResultList();
                //distinct를 주면 JPA에서 자체적으로 Order를 가져올 떄, id가 같으면 중복을 제거하고 하나만 되돌려준다.
                //JPA가 생성한 DB 쿼리에도 distinct가 붙어서 가는 것을 확인할 수 있는데,
                //DB 스펙상 모든 컬럼이 일치해야 distinct되기 때문에 DB상 조회 결과는 여전히 바라는 결과가 아니다.
                //JPA가 매핑할 때 중복을 제거해주기 떄문에 원하는 결과를 얻을 수 있는 것이다.
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        //member와 delivery를 패치 조인으로 조회
        return em.createQuery("select o " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    /**
     * QueryDsl
     * 실무에서는 조건에 쿼리가 달라지는 동적 쿼리를 많이 사용한다.
     * QueryDsl을 사용하면 더욱 가독성이 좋게 Criteria를 사용한 것 과 같은 효과를 낼 수 있다.
     */
/*  public List<Order> findAllByQueryDsl(OrderSearch orderSearch) {

        QOrder order = QOrder.order;
        QMember member = QMember.member;

        return query
                .select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch.getMemberName()))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if(statusCond == null) {
            return null;
        }
        return order.status.eq(statusCond);
    }


    private BooleanExpression nameLike(String nameCond) {
        if(!StringUtils.hasText(nameCond)) {
            return null;
        }
        return member.name.like(nameCond);
    }
 */
}
