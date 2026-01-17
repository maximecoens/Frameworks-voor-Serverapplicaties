/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package be.iii.jdbclabo.model;

public interface IOrderDetail {

    int getOrderLineNumber();

    double getPrice();

    String getProductCode();

    int getQuantity();

    int getOrderNumber();
    
    void setOrderLineNumber(int orderLineNumber);

    void setPrice(double price);

    void setProductCode(String productCode);

    void setQuantity(int quantity);
    
    void setOrderNumber(int orderNumber);

}
