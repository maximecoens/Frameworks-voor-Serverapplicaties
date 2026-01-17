package be.iii.jdbclabo;

import be.iii.jdbclabo.model.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConsoleApp implements CommandLineRunner {
    IDataStorage storage;

    public ConsoleApp(IDataStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run(String... args) throws Exception {

        // Lijst producten uitschrijven
        System.out.println("Lijst van producten");
        for (IProduct product : storage.getProducts()) {
            showProduct(product);
        }

        // Toon totaal aankopen klant
        int customerNr = 141;
        double totaal = storage.getTotal(customerNr);
        System.out.println("Totaal van klant nr. " + customerNr + ": " + totaal);


    }


    private void showProduct(IProduct product) {
        System.out.println("Code :" + product.getProductCode());
        System.out.println("Name :" + product.getProductName());
        System.out.println("Line :" + product.getProductLine());
        System.out.println("Scale :" + product.getProductScale());
        System.out.println("Vendor :" + product.getProductVendor());
        System.out.println("Description :" + product.getProductDescription());
        System.out.println("Stock :" + product.getQuantityInStock());
        System.out.println("Price :" + product.getBuyPrice());
        System.out.println("MSRP :" + product.getMsrp());
    }

    private void showCustomer(ICustomer customer) {
        System.out.println("Number :" + customer.getCustomerNumber());
        System.out.println("Name :" + customer.getCustomerName());
        System.out.println("Contact :" + customer.getContactFirstName() + " " + customer.getContactLastName());
        System.out.println("Phone :" + customer.getPhone());
        System.out.println("Address :" + customer.getAddressLine1() + ", " + customer.getAddressLine2());
        System.out.println("City/State :" + customer.getCity() + " / " + customer.getState());
        System.out.println("Postal Code / Country :" + customer.getPostalCode() + " / " + customer.getCountry());
    }

    private void showOrder(IOrderWithoutDetails order) {
        System.out.println("Number :" + order.getOrderNumber());
        System.out.println("Ordered :" + order.getOrderDate().toString());
        System.out.println("Required :" + order.getRequiredDate().toString());
        if (order.getShippedDate() != null) {
            System.out.println("Shipped :" + order.getShippedDate().toString());
        }
        System.out.println("Status :" + order.getStatus());
        System.out.println("Comments :" + order.getComments());
        System.out.println("Customer Nr. :" + order.getCustomerNumber());
    }

}
