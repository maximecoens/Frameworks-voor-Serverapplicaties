/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package be.iii.jdbclabo.model;

public interface IProduct {

    double getMsrp();

    double getBuyPrice();

    String getProductCode();

    String getProductDescription();

    String getProductLine();

    String getProductName();

    String getProductScale();

    String getProductVendor();
    
    int getQuantityInStock();

    void setMsrp(double msrp);

    void setBuyPrice(double price);

    void setProductCode(String productCode);

    void setProductDescription(String productDescription);

    void setProductLine(String productLine);

    void setProductName(String productName);

    void setProductScale(String productScale);

    void setProductVendor(String productVendor);
    
    void setQuantityInStock(int quantityInStock);

}
