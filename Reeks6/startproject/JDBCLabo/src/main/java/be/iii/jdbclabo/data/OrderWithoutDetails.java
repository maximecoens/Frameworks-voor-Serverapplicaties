package be.iii.jdbclabo.data;

import be.iii.jdbclabo.model.IOrderWithoutDetails;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrderWithoutDetails implements IOrderWithoutDetails {
    protected int orderNumber;
    protected Date orderDate;
    protected Date requiredDate;
    protected Date shippedDate;
    protected String status;
    protected String comments;
    protected int customerNumber;

    public OrderWithoutDetails(int orderNumber, Date orderDate, Date requiredDate, Date shippedDate, String status, String comments, int customerNumber) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.requiredDate = requiredDate;
        this.shippedDate = shippedDate;
        this.status = status;
        this.comments = comments;
        this.customerNumber = customerNumber;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public int getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int number) {
        this.orderNumber = number;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date ordered) {
        this.orderDate = ordered;
    }

    public Date getRequiredDate() {
        return requiredDate;
    }

    public void setRequiredDate(Date required) {
        this.requiredDate = required;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shipped) {
        this.shippedDate = shipped;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        List<Class<?>> interfaces = Arrays.asList(o.getClass().getInterfaces());
        if (!interfaces.contains(IOrderWithoutDetails.class)) return false;
        OrderWithoutDetails order = (OrderWithoutDetails) o;
        return orderNumber == order.orderNumber &&
                customerNumber == order.customerNumber &&
                orderDate.equals(order.orderDate) &&
                requiredDate.equals(order.requiredDate) &&
                Objects.equals(shippedDate, order.shippedDate) &&
                status.equals(order.status) &&
                Objects.equals(comments, order.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, orderDate, requiredDate, shippedDate, status, comments, customerNumber);
    }
}
