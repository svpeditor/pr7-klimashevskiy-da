package ru.edu.pr04;

public class OrderService {
    public Order createOrder(Employee e) {
        return new Order(e);
    }

    // проверка перед подтверждением, сам заказ проверяет еще и то, что он не пустой
    public void validateForConfirm(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Заказ не передан");
        }
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Заказ уже подтвержден");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Заказ: нельзя подтвердить пустой заказ");
        }
    }

    public void confirm(Order order) {
        validateForConfirm(order);
        order.confirm();
    }
}
