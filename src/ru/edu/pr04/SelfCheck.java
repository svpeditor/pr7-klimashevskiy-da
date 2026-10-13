package ru.edu.pr04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SelfCheck {
    private static int passed = 0;
    private static int count = 0;

    public static void main(String[] args) {
        checkProduct();
        checkOrderItem();
        checkOrder();
        checkConfirm();
        System.out.println("Unit: " + passed + "/" + count + " passed");
        // можно передать свой файл: java -cp out ru.edu.pr04.SelfCheck data/my.csv
        runFile(args.length > 0 ? args[0] : "data/sample.csv");
    }

    private static void checkProduct() {
        // Product: корректный товар
        check("product ok", new Product("P1", "Mouse", 1000).getPrice() == 1000);
        // Product: цена 0 и отрицательная - ошибка
        check("product price 0", throwsIae("P2", "Mouse", 0));
        check("product price -100", throwsIae("X01", "Broken", -100));
        // Product: пустой id и пустое наименование
        check("product empty id", throwsIae("", "Mouse", 1000));
        check("product null name", throwsIae("P3", null, 1000));
    }

    private static void checkOrderItem() {
        Product mouse = new Product("P1", "Mouse", 1000);
        // нижняя граница: 1 можно, 0 нельзя
        check("quantity 1", new OrderItem(mouse, 1).getQuantity() == 1);
        check("quantity 0", throwsIae(mouse, 0));
        // верхняя граница: MAX_QUANTITY можно, на 1 больше нельзя
        check("quantity max", new OrderItem(mouse, OrderItem.MAX_QUANTITY).getQuantity() == OrderItem.MAX_QUANTITY);
        check("quantity max + 1", throwsIae(mouse, OrderItem.MAX_QUANTITY + 1));
        // стоимость строки = цена * количество
        check("line total", new OrderItem(mouse, 3).getLineTotal() == 3000);
    }

    private static void checkOrder() {
        Order order = newOrder();
        Product monitor = new Product("M01", "Monitor 27", 32990);
        order.addProduct(monitor, 2);
        order.addProduct(new Product("K01", "Keyboard", 4490), 3);
        order.addProduct(monitor, 1);
        // повтор товара объединяется в одну строку
        check("merge repeated product", order.getItems().size() == 2 && order.getItems().get(0).getQuantity() == 3);
        // итог считается из позиций: 3 * 32990 + 3 * 4490
        check("total", order.total() == 112440);
        // удаление позиции и пересчет итога
        order.removeProduct("K01");
        check("remove and total", order.getItems().size() == 1 && order.total() == 98970);
        check("remove missing", throwsOnRemove(order, "NOPE"));
        // объединение сверх лимита - ошибка, количество не меняется
        check("merge over max", throwsOnAdd(order, monitor, OrderItem.MAX_QUANTITY));
        check("quantity unchanged", order.getItems().get(0).getQuantity() == 3);
        // getItems отдает копию, ее нельзя изменить
        try {
            order.getItems().clear();
            check("items copy", false);
        } catch (UnsupportedOperationException e) {
            check("items copy", order.getItems().size() == 1);
        }
    }

    private static void checkConfirm() {
        OrderService service = new OrderService();
        Order empty = newOrder();
        // пустой заказ подтвердить нельзя
        check("confirm empty", throwsOnConfirm(service, empty));
        Order order = newOrder();
        order.addProduct(new Product("K01", "Keyboard", 4490), 1);
        service.confirm(order);
        check("status confirmed", order.getStatus() == OrderStatus.CONFIRMED);
        // после подтверждения заказ менять нельзя
        check("add after confirm", throwsOnAdd(order, new Product("P1", "Mouse", 1000), 1));
        check("remove after confirm", throwsOnRemove(order, "K01"));
        check("confirm twice", throwsOnConfirm(service, order));
    }

    // ---------- вспомогательные методы ----------

    private static Order newOrder() {
        return new OrderService().createOrder(new Employee("E1", "Иванов Иван"));
    }

    private static boolean throwsIae(String id, String name, double price) {
        try {
            new Product(id, name, price);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static boolean throwsIae(Product product, int quantity) {
        try {
            new OrderItem(product, quantity);
            return false;
        } catch (IllegalArgumentException e) {
            return true;
        }
    }

    private static boolean throwsOnAdd(Order order, Product product, int quantity) {
        try {
            order.addProduct(product, quantity);
            return false;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return true;
        }
    }

    private static boolean throwsOnRemove(Order order, String productId) {
        try {
            order.removeProduct(productId);
            return false;
        } catch (IllegalArgumentException | IllegalStateException e) {
            return true;
        }
    }

    private static boolean throwsOnConfirm(OrderService service, Order order) {
        try {
            service.confirm(order);
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    private static void check(String name, boolean ok) {
        count++;
        if (ok) {
            passed++;
            System.out.println("PASS " + name);
        } else {
            System.out.println("FAIL " + name);
        }
    }

    // ---------- заказ из файла productId;name;price;quantity ----------

    private static void runFile(String fileName) {
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать " + fileName);
            return;
        }
        System.out.println();
        System.out.println("Заказ из файла " + fileName + ":");
        Order order = newOrder();
        for (int i = 1; i < lines.size(); i++) {   // первая строка - заголовок
            if (!lines.get(i).isEmpty()) {
                addLine(order, lines.get(i), i + 1);
            }
        }
        printOrder(order);
    }

    private static void addLine(Order order, String line, int lineNumber) {
        String[] parts = line.split(";", -1);
        try {
            if (parts.length != 4) {
                throw new IllegalArgumentException("нужно 4 значения через ;, получено " + parts.length);
            }
            Product product = new Product(parts[0], parts[1], Double.parseDouble(parts[2]));
            order.addProduct(product, Integer.parseInt(parts[3]));
        } catch (IllegalArgumentException | IllegalStateException e) {
            // NumberFormatException тоже наследник IllegalArgumentException
            System.out.println("Строка " + lineNumber + " пропущена: " + e.getMessage());
        }
    }

    private static void printOrder(Order order) {
        for (OrderItem item : order.getItems()) {
            System.out.printf("%s %s x %d = %.2f%n", item.getProduct().getId(), item.getProduct().getName(),
                    item.getQuantity(), item.getLineTotal());
        }
        System.out.printf("Итого: %.2f%n", order.total());
    }
}
