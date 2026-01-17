/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.iii.jdbclabo.data;


import be.iii.jdbclabo.model.IOrder;
import be.iii.jdbclabo.model.IOrderDetail;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Order extends OrderWithoutDetails implements IOrder {


    private List<IOrderDetail> details;

    public Order(int orderNumber, Date orderDate, Date requiredDate, Date shippedDate, String status, String comments, int customerNumber, List<IOrderDetail> details) {
        super(orderNumber,orderDate,requiredDate,shippedDate,status,comments,customerNumber);
        this.details = details;
    }

    @Override
    public List<IOrderDetail> getDetails() {
        return details;
    }

    @Override
    public void setDetails(List<IOrderDetail> details) {
        this.details = details;
    }

}
