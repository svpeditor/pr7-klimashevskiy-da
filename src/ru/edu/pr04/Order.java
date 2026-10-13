package ru.edu.pr04;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {
    private final Employee employee;
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(Employee employee) {
        this.employee = Objects.requireNonNull(employee, "Заказ: сотрудник не указан");
    }

    // повторный товар не добавляется второй строкой, а увеличивает количество
    public void addProduct(Product product, int quantity) {
        checkDraft();
        if (product == null) {
            throw new IllegalArgumentException("Заказ: товар не указан");
        }
        OrderItem existing = findItem(product.getId());
        if (existing != null) {
            existing.increase(quantity);
        } else {
            items.add(new OrderItem(product, quantity));
        }
    }

    public void removeProduct(String productId) {
        checkDraft();
        OrderItem item = findItem(productId);
        if (item == null) {
            throw new IllegalArgumentException("Заказ: товара " + productId + " нет в заказе");
        }
        items.remove(item);
    }

    // итог каждый раз считается из текущих позиций, отдельного поля total нет
    public double total() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.getLineTotal();
        }
        return sum;
    }

    public void confirm() {
        checkDraft();
        if (items.isEmpty()) {
            throw new IllegalStateException("Заказ: нельзя подтвердить пустой заказ");
        }
        status = OrderStatus.CONFIRMED;
    }

    private OrderItem findItem(String productId) {
        for (OrderItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                return item;
            }
        }
        return null;
    }

    private void checkDraft() {
        if (status == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Заказ подтвержден, изменять его нельзя");
        }
    }

    // наружу отдается копия, чтобы список позиций нельзя было поменять в обход addProduct
    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public Employee getEmployee() {
        return employee;
    }

    public OrderStatus getStatus() {
        return status;
    }
}
