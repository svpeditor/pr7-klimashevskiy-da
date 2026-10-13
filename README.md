# Практическая работа №7. Первое полноценное ООП-приложение. Корпоративный заказ оборудования

Климашевский Даниил Александрович, группа ЭФБО-07-25, вариант 4.

Консольное приложение формирует заказ оборудования для сотрудника: товары, позиции
с количеством, объединение повторного товара, итог и подтверждение заказа. По варианту 4
в одной позиции можно заказать не больше 25 штук.

## Сборка и запуск

Нужна Java 21 или новее.

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out ru.edu.pr04.SelfCheck
```

Или скриптом: `./run_selfcheck.sh` (macOS/Linux), `run_selfcheck.bat` (Windows).

Сначала выполняются проверки, потом собирается заказ из `data/sample.csv`
(формат `productId;name;price;quantity`, первая строка - заголовок). Ошибочные строки
пропускаются с сообщением. Можно передать свой файл:

```bash
java -cp out ru.edu.pr04.SelfCheck data/my_order.csv
```

Пакет называется `ru.edu.pr04`, как в исходном проекте.

## Что где лежит

- `src/ru/edu/pr04/Product.java`, `OrderItem.java`, `Order.java`, `Employee.java`, `OrderStatus.java` - предметные классы
- `src/ru/edu/pr04/OrderService.java` - создание и подтверждение заказа
- `src/ru/edu/pr04/SelfCheck.java` - проверки и заказ из файла
- `diagrams/` - activity-диаграмма и диаграмма классов (dot и png)
- `DECISIONS.md` - вариант, два решения, граничные проверки, ответы на вопросы
- `REPORT.docx` - отчет
