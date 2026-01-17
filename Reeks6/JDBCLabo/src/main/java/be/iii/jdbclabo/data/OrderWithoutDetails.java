/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.iii.jdbclabo.data;


import be.iii.jdbclabo.model.IOrder;
import be.iii.jdbclabo.model.IOrderDetail;
import be.iii.jdbclabo.model.IOrderWithoutDetails;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrderWithoutDetails implements IOrderWithoutDetails {

    private int orderNumber;
    private Date ordered;
    private Date required;
    private Date shipped;
    private String status;
    private String comments;
    private int customerNumber;

    public OrderWithoutDetails(int orderNumber, Date orderDate, Date requiredDate, Date shippedDate, String status, String comments, int customerNumber) {
        this.orderNumber = orderNumber;
        this.ordered = orderDate;
        this.required = requiredDate;
        this.shipped = shippedDate;
        this.status = status;
        this.comments = comments;
        this.customerNumber = customerNumber;
    }

    @Override
    public String getComments() {
        return comments;
    }

    @Override
    public void setComments(String comments) {
        this.comments = comments;
    }

    @Override
    public int getCustomerNumber() {
        return customerNumber;
    }

    @Override
    public void setCustomerNumber(int customerNumber) {
        this.customerNumber = customerNumber;
    }

    @Override
    public int getOrderNumber() {
        return orderNumber;
    }

    @Override
    public void setOrderNumber(int number) {
        this.orderNumber = number;
    }

    @Override
    public Date getOrderDate() {
        return ordered;
    }

    @Override
    public void setOrderDate(Date ordered) {
        this.ordered = ordered;
    }

    @Override
    public Date getRequiredDate() {
        return required;
    }

    @Override
    public void setRequiredDate(Date required) {
        this.required = required;
    }

    @Override
    public Date getShippedDate() {
        return shipped;
    }

    @Override
    public void setShippedDate(Date shipped) {
        this.shipped = shipped;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
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
                ordered.equals(order.ordered) &&
                required.equals(order.required) &&
                Objects.equals(shipped, order.shipped) &&
                status.equals(order.status) &&
                Objects.equals(comments, order.comments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderNumber, ordered, required, shipped, status, comments, customerNumber);
    }
}
