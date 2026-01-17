package be.iii.jdbclabo.model;

import java.util.Date;
import java.util.List;

public interface IOrderWithoutDetails {
    String getComments();

    void setComments(String comments);

    int getCustomerNumber();

    void setCustomerNumber(int customerNumber);

    int getOrderNumber();

    void setOrderNumber(int number);

    Date getOrderDate();

    void setOrderDate(Date ordered);

    Date getRequiredDate();

    void setRequiredDate(Date required);

    Date getShippedDate();

    void setShippedDate(Date shipped);

    String getStatus();

    void setStatus(String status);
}
