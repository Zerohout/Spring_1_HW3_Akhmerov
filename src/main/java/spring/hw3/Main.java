package spring.hw3;

import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emFactory = new Configuration()
                .configure("hibernate.cfg.xml")
                .buildSessionFactory();

        EntityManager em = emFactory.createEntityManager();
        em.getTransaction().begin();

        do {
            List<Customer> customers = em.createQuery("from Customer", Customer.class).getResultList();
            List<Product> products = em.createQuery("from Product", Product.class).getResultList();

            String title;
            boolean shortFlag;

            if(customers.size() == 0 && products.size() == 0){
                title = "Выберите действие:\n1) Заполнить БД данными.\n2) Удалить БД и выйти из приложения\n3) Выйти из приложения";
                shortFlag = true;
            }else {
                title = "Выберите действие:\n1) Показать список покупателей.\n2) Удалить покупателя с указанным id (через пробел)\n" +
                        "3) Показать список продуктов.\n4) Удалить товар с указанным id (через пробел)\n" +
                        "5) Удалить БД и выйти из приложения\n6) Выйти из приложения";
                shortFlag = false;
            }
            System.out.println(title);
            Scanner scanner = new Scanner(System.in);
            if(scanner.hasNextInt()){
                var userValue = scanner.nextInt();
                if(shortFlag){
                    if(userValue == 1){
                        fillDB(em);
                    }
                    if(userValue == 2){
                        dropDB(em);
                        return;
                    }
                    if(userValue == 3){
                        return;
                    }
                }else{
                    if(userValue == 1){
                        customers.forEach(customer -> {
                            System.out.println(customer.toString());
                            customer.getProducts().forEach(product -> {
                                System.out.println("\t" + product.toString());
                            });
                        });
                    }
                    if(userValue == 2){
                        Customer customer = em.find(Customer.class, (long)scanner.nextInt());
                        if(customer == null) {
                            System.out.println("Покупатель не найден");
                        }
                        else {
                            em.remove(customer);
                            em.getTransaction().commit();
                            System.out.println("Покупатель успешно удален");
                        }
                    }
                    if(userValue == 3){
                        products.forEach(product -> {
                            System.out.println(product.toString());
                            product.getCustomers().forEach(customer -> {
                                System.out.println("\t" + customer.toString());
                            });
                        });
                    }
                    if(userValue == 4){
                        Product product = em.find(Product.class, (long)scanner.nextInt());
                        if(product == null) {
                            System.out.println("Продукт не найден");
                        }else{
                            em.remove(product);
                            em.getTransaction().commit();
                            System.out.println("Продукт успешно удален");
                        }
                    }
                    if(userValue == 5){
                        dropDB(em);
                        return;
                    }
                    if(userValue == 6){
                        return;
                    }
                }
            }
        } while (true);
    }

    private static void dropDB(EntityManager em) {
        em.createNativeQuery("drop database spring_hw3").executeUpdate();
    }
    private static void fillDB(EntityManager em) {
        Product thingProduct = new Product(null, "Thing", new BigDecimal(1_000));
        Product nothingProduct = new Product(null, "Nothing", new BigDecimal(2_000));
        Product objectProduct = new Product(null, "Object", new BigDecimal(3_000));

        Customer testCustomer = new Customer(null, "Test");
        testCustomer.addProduct(thingProduct);
        testCustomer.addProduct(nothingProduct);
        em.persist(testCustomer);

        Customer manCustomer = new Customer(null, "Man");
        manCustomer.addProduct(nothingProduct);
        manCustomer.addProduct(objectProduct);
        em.persist(manCustomer);

        Customer womanCustomer = new Customer(null, "Woman");
        womanCustomer.addProduct(thingProduct);
        womanCustomer.addProduct(objectProduct);
        em.persist(womanCustomer);

        em.getTransaction().commit();
    }
}
