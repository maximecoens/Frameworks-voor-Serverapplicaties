/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.iii.jdbclabo.data;

import be.iii.jdbclabo.model.IProduct;

public class Product implements IProduct {

    private String productCode;
    private String productName;
    private String productLine;
    private String productScale;
    private String productVendor;
    private String productDescription;
    private int quantityInStock;
    private double buyPrice;
    private double msrp;

    public Product(String productCode, String productName, String productLine,
            String productScale, String productVendor, String productDescription,
            int quantityInStock, double buyPrice, double msrp) {
        this.productCode = productCode;
        this.productName = productName;
        this.productLine = productLine;
        this.productScale = productScale;
        this.productVendor = productVendor;
        this.productDescription = productDescription;
        this.quantityInStock = quantityInStock;
        this.buyPrice = buyPrice;
        this.msrp = msrp;
    }

    @Override
    public double getMsrp() {
        return msrp;
    }

    @Override
    public void setMsrp(double msrp) {
        this.msrp = msrp;
    }

    @Override
    public double getBuyPrice() {
        return buyPrice;
    }

    @Override
    public void setBuyPrice(double price) {
        this.buyPrice = price;
    }

    @Override
    public String getProductCode() {
        return productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public String getProductDescription() {
        return productDescription;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    @Override
    public String getProductLine() {
        return productLine;
    }

    @Override
    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }

    @Override
    public String getProductName() {
        return productName;
    }

    @Override
    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String getProductScale() {
        return productScale;
    }

    @Override
    public void setProductScale(String productScale) {
        this.productScale = productScale;
    }

    @Override
    public String getProductVendor() {
        return productVendor;
    }

    @Override
    public void setProductVendor(String productVendor) {
        this.productVendor = productVendor;
    }

    @Override
    public int getQuantityInStock() {
        return quantityInStock;
    }

    @Override
    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

}
