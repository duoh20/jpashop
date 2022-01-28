package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.service.MemberService;
import jpabook.jpashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String createOrderForm(Model model) {
        List<Member> members = memberService.findMembers();
        List<Item> items = itemService.findItems();
        model.addAttribute("members", members);
        model.addAttribute("items", items);
        return "orders/orderForm";
    }

    @PostMapping("/order")
    //@RequestParam은 form-submit하면 form에 있는 요소들의 name으로 value를 가져온다.
    public String createOrder(OrderForm orderForm, Model model) {
        System.out.println(orderForm.toString());
        orderService.order(orderForm.getMemberId(), orderForm.getItemId(), orderForm.getCount());
        //이 예제에서는 member, item의 식별자만 OrderService의 order()메소드로 넘겨줬다.
        //바깥에서 member, item 객체를 따로 찾아와서 넘길 수도 있는데 왜 이렇게 작성했을까?
        //Transaction이 있는 서비스 레이어에서 관련 객체를 찾아서 조작해야 변경 감지도 되고 더 명확한 로직을 작성할 수 있기 때문이다.

        return "redirect:/orders";
    }

    //단순 조회의 경우 컨틀롤러에서 레포지토리를 바로 불러도 괜찮다.
    //orderService.findOrders()를 보면 단순히 repository에 위임만하는데 이런 로직은 바로 컨트롤러에서 호출해도 괜찮다.
    @GetMapping("/orders")
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model)  {
        //ModelAttribute가 담기면 자동으로 model에 포함된다.
        //즉, 아래 구문과 같은 내용이 설정되어 있다고 보면 된다.
        //model.addAttribute("orderSearch", orderSearch);
        model.addAttribute("orders", orderService.findOrders(orderSearch));
        return "orders/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
