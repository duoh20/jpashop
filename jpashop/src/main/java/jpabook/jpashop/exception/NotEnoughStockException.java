package jpabook.jpashop.exception;

public class NotEnoughStockException extends RuntimeException {

    //예외 클래스 생성 후 RuntimeException 오버라이드
    public NotEnoughStockException() {
        super();
    }

    public NotEnoughStockException(String message) {
        super(message);
    }

    public NotEnoughStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotEnoughStockException(Throwable cause) {
        super(cause);
    }
}