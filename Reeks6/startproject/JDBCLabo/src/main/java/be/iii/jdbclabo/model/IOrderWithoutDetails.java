package be.iii.jdbclabo.model;

import java.util.Date;

public interface IOrderWithoutDetails {
    String getComments();

    int getCustomerNumber();

    int getOrderNumber();

    Date getOrderDate();

    Date getRequiredDate();

    Date getShippedDate();

    String getStatus();

    void setComments(String comments);

    void setCustomerNumber(int customerNumber);

    void setOrderNumber(int number);

    void setOrderDate(Date ordered);

    void setRequiredDate(Date required);

    void setShippedDate(Date shipped);

    void setStatus(String status);
}
