package baiTapTongHop;
import java.util.*;


class DuplicateIdException extends Exception {
    public DuplicateIdException(String m) { super(m); }
}

class InvalidPriceException extends Exception {
    public InvalidPriceException(String m) { super(m); }
}

class NonRefundableException extends Exception {
    public NonRefundableException(String m) { super(m); }
}

interface Deliverable {
    void deliver();
}

interface Refundable {
    void refund() throws NonRefundableException;
}

abstract class Product implements Deliverable, Refundable {
    protected String id;
    protected String name;
    protected double price;

    public Product(String id, String name, double price) throws InvalidPriceException {
        if (price < 0) throw new InvalidPriceException("Price < 0: " + id);
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }

    public void deliver() {
        System.out.println(name + " delivered");
    }

    public void refund() throws NonRefundableException {
        System.out.println(name + " refunded");
    }

    public String toString() {
        return id + " " + name + "  " + price;
    }
}

class Laptop extends Product {
    public Laptop(String id, String name, double price) throws InvalidPriceException {
        super(id, name, price);
    }

    public void refund() throws NonRefundableException {
        throw new NonRefundableException("Laptop is not refundable: " + id);
    }
}

class Book extends Product {
    public Book(String id, String name, double price) throws InvalidPriceException {
        super(id, name, price);
    }
}

class Customer {
    String id;
    String name;
    public Customer(String id, String name) { this.id = id; this.name = name; }
    public String getId() { return id; }
}

interface Payment {
    void pay(double amount);
}

class CreditCardPayment implements Payment {
    public void pay(double amount) { System.out.println("Trả tiền bằng thẻ tín dụng: " + amount); }
}

class PaypalPayment implements Payment {
    public void pay(double amount) { System.out.println("Trả tiền bằng PayPal: " + amount); }
}

class CashPayment implements Payment {
    public void pay(double amount) { System.out.println("Trả tiền mặt: " + amount); }
}

class MoMoPayment implements Payment {
    public void pay(double amount) { System.out.println("Trả tiền bằng MoMo: " + amount); }
}

class Order {
    String id;
    Customer customer;
    List<Product> items = new ArrayList<>();

    public Order(String id, Customer c) {
        this.id = id;
        this.customer = c;
    }

    public void addProduct(Product p) { items.add(p); }

    public double total() {
        return items.stream().mapToDouble(i -> i.price).sum();
    }

    public void pay(Payment method) {
        method.pay(total());
    }
}

interface Repository<T> {
    void add(T item) throws DuplicateIdException;
    void update(T item) throws NotFoundException;
    void delete(String id) throws NotFoundException;
    List<T> findAll();
}

class NotFoundException extends Exception {
    public NotFoundException(String m) { super(m); }
}

abstract class InMemoryRepository<T> implements Repository<T> {
    protected Map<String, T> map = new HashMap<>();

    protected abstract String getId(T item);

    public void add(T item) throws DuplicateIdException {
        String id = getId(item);
        if (map.containsKey(id)) throw new DuplicateIdException(id);
        map.put(id, item);
    }

    public void update(T item) throws NotFoundException {
        String id = getId(item);
        if (!map.containsKey(id)) throw new NotFoundException(id);
        map.put(id, item);
    }

    public void delete(String id) throws NotFoundException {
        if (!map.containsKey(id)) throw new NotFoundException(id);
        map.remove(id);
    }

    public List<T> findAll() {
        return new ArrayList<>(map.values());
    }
}

class ProductRepository extends InMemoryRepository<Product> {
    protected String getId(Product p) { return p.getId(); }
}

class CustomerRepository extends InMemoryRepository<Customer> {
    protected String getId(Customer c) { return c.getId(); }
}

class OrderRepository extends InMemoryRepository<Order> {
    protected String getId(Order o) { return o.id; }
}

public class BaiTapTongHop {
    public static void main(String[] args) {
        try {
            ProductRepository pr = new ProductRepository();
            CustomerRepository cr = new CustomerRepository();
            OrderRepository or = new OrderRepository();

            Product p1 = new Book("B1", "Hướng dẫn lập trình Java", 100);
            Product p2 = new Laptop("L1", "MacBook", 2000);

            pr.add(p1);
            pr.add(p2);

            Customer c = new Customer("C1", "Alice");
            cr.add(c);

            System.out.println("Product list:");
            for (Product p : pr.findAll()) System.out.println(p);

            Order o = new Order("O1", c);
            o.addProduct(p1);
            o.addProduct(p2);
            or.add(o);

            System.out.println("\nDeliver:");
            p1.deliver();
            p2.deliver();

            System.out.println("\nRefund:");
            try { p1.refund(); } catch (Exception e) { System.out.println(e.getMessage()); }
            try { p2.refund(); } catch (Exception e) { System.out.println(e.getMessage()); }

            System.out.println("\nPayment test:");
            o.pay(new CreditCardPayment());
            o.pay(new PaypalPayment());
            o.pay(new CashPayment());
            o.pay(new MoMoPayment());

            System.out.println("\nDuplicate test:");
            try { pr.add(p1); } catch (Exception e) { System.out.println(e.getMessage()); }

            System.out.println("\nInvalid price test:");
            try { new Book("Book", " Book 2", -10); } catch (Exception e) { System.out.println(e.getMessage()); }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
