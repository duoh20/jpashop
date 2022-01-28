package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("item", new Book());
        return "items/createItemForm";
    }

    @PostMapping("/items/new")
    public String create(BookForm form) {
        Book item = new Book();
        item.setName(form.getName());
        item.setPrice(form.getPrice());
        item.setStockQuantity(form.getStockQuantity());
        item.setAuthor(form.getAuthor());
        item.setIsbn(form.getIsbn());
        // 위 예제는 편하게 setter를 사용했지만,
        // Order의 createOrder()처럼 setter를 가져다 쓰는 것보다는, 생성 메소드를 엔티티에 추가하여 사용하는 것이 좋은 설계이다.

        itemService.saveItem(item);
        return "redirect:/items"; //책 추가 후 저장된 책 목록으로 리다이렉트
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    /* 매우 중요한 부분!! JPA에서 데이터 수정은 어떻게 설계하면 좋을끼? */
    @GetMapping("/items/{itemId}/edit")
    public String updateItemFrom(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);
        //Item 타입이라 Item으로 받아야하지만, 간단한 예제이므로 Book으로 캐스팅하여 사용함

        BookForm form = new BookForm(); //엔티티가 아니라 Form 전송 객체를 따로 만들어 사용하자.
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")
    public String updateItem(@ModelAttribute("form") BookForm bookForm, Model model) {
        //PathVariable의 itemId를 임의로 바꾸어 form을 제출하는 경우 다른 사람의 정보가 변경되는 취약점이 있다.
        //따라서 서버단에서 유효한 권한을 가진 사용자의 요청이 맞는지 한 번 더 검증하는 로직이 필요하다.
        //업데이트할 객체를 세션에 담아 검증하는 방식도 있는데 최근에는 잘 사용되진 않는다.
        Book book = new Book();
        book.setId(bookForm.getId());
        book.setName(bookForm.getName());
        book.setPrice(bookForm.getPrice());
        book.setStockQuantity(bookForm.getStockQuantity());
        book.setAuthor(bookForm.getAuthor());
        book.setIsbn(bookForm.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }
}
