package ru.edu.pr04;

public class OrderItem {
    // вариант 4: максимальное количество в одной позиции 25 (было 100)
    public static final int MAX_QUANTITY = 25;

    private final Product product;
    private int quantity;

    public OrderItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Позиция: товар не указан");
        }
        this.product = product;
        setQuantity(quantity);
    }

    private void setQuantity(int quantity) {
        if (quantity < 1 || quantity > MAX_QUANTITY) {
            throw new IllegalArgumentException("Количество должно быть от 1 до " + MAX_QUANTITY + ", получено " + quantity);
        }
        this.quantity = quantity;
    }

    // увеличить количество при повторном добавлении того же товара
    void increase(int delta) {
        if (delta < 1) {
            throw new IllegalArgumentException("Добавляемое количество должно быть больше 0, получено " + delta);
        }
        setQuantity(quantity + delta);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getLineTotal() {
        return product.getPrice() * quantity;
    }
}
