/******************************************************************************
 * Copyright (c) 2005 Actuate Corporation.
 * All rights reserved. This file and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial implementation
 *
 * Classic Models Inc. sample database developed as part of the
 * Eclipse BIRT Project. For more information, see http:\\www.eclipse.org\birt
 *
 *******************************************************************************/

/* Recommended DATABASE name is classicmodels. */

CREATE DATABASE iii;
\c iii;

/* DROP the existing tables. Comment this out if it is not needed. */

-- DROP TABLE Customers;
-- DROP TABLE Employees;
-- DROP TABLE Offices;
-- DROP TABLE OrderDetails;
-- DROP TABLE Orders;
-- DROP TABLE Payments;
-- DROP TABLE Products;
-- DROP TABLE ProductLines;

/* Create the full set of Classic Models Tables */

CREATE TABLE Offices (
  officeCode VARCHAR(10) NOT NULL,
  city VARCHAR(50) NOT NULL,
  phone VARCHAR(50) NOT NULL,
  addressLine1 VARCHAR(50) NOT NULL,
  addressLine2 VARCHAR(50) NULL,
  state VARCHAR(50) NULL,
  country VARCHAR(50) NOT NULL,
  postalCode VARCHAR(15) NOT NULL,
  territory VARCHAR(10) NOT NULL,
  PRIMARY KEY (officeCode)
);

CREATE TABLE Employees (
  employeeNumber INTEGER NOT NULL,
  lastName VARCHAR(50) NOT NULL,
  firstName VARCHAR(50) NOT NULL,
  extension VARCHAR(10) NOT NULL,
  email VARCHAR(100) NOT NULL,
  officeCode VARCHAR(10) NOT NULL,
  reportsTo INTEGER NULL,
  jobTitle VARCHAR(50) NOT NULL,
  PRIMARY KEY (employeeNumber),
  CONSTRAINT fk_office FOREIGN KEY(officeCode) REFERENCES offices(officeCode)
);

CREATE TABLE Customers (
  customerNumber INTEGER NOT NULL,
  customerName VARCHAR(50) NOT NULL,
  contactLastName VARCHAR(50) NOT NULL,
  contactFirstName VARCHAR(50) NOT NULL,
  phone VARCHAR(50) NOT NULL,
  addressLine1 VARCHAR(50) NOT NULL,
  addressLine2 VARCHAR(50) NULL,
  city VARCHAR(50) NOT NULL,
  state VARCHAR(50) NULL,
  postalCode VARCHAR(15) NULL,
  country VARCHAR(50) NOT NULL,
  salesRepEmployeeNumber INTEGER NULL,
  creditLimit NUMERIC NULL,
  PRIMARY KEY (customerNumber),
  CONSTRAINT fk_sales FOREIGN KEY(salesRepEmployeeNumber) REFERENCES employees(employeeNumber)
);

CREATE TABLE Orders (
  orderNumber INTEGER NOT NULL,
  orderDate TIMESTAMP NOT NULL,
  requiredDate TIMESTAMP NOT NULL,
  shippedDate TIMESTAMP NULL,
  status VARCHAR(15) NOT NULL,
  comments TEXT NULL,
  customerNumber INTEGER NOT NULL,
  PRIMARY KEY (orderNumber),
  CONSTRAINT fk_customer FOREIGN KEY(customerNumber) REFERENCES customers(customerNumber)
);

CREATE TABLE Payments (
  customerNumber INTEGER NOT NULL,  
  checkNumber VARCHAR(50) NOT NULL,
  paymentDate TIMESTAMP NOT NULL,
  amount NUMERIC NOT NULL,
  PRIMARY KEY (customerNumber, checkNumber),
  CONSTRAINT fk_customer_payment FOREIGN KEY(customerNumber) REFERENCES customers(customerNumber)
);

CREATE TABLE Products (
  productCode VARCHAR(15) NOT NULL,
  productName VARCHAR(70) NOT NULL,
  productLine VARCHAR(50) NOT NULL,
  productScale VARCHAR(10) NOT NULL,
  productVendor VARCHAR(50) NOT NULL,
  productDescription TEXT NOT NULL,
  quantityInStock SMALLINT NOT NULL,
  buyPrice NUMERIC NOT NULL,
  MSRP NUMERIC NOT NULL,
  PRIMARY KEY (productCode)
);

CREATE TABLE OrderDetails (
  orderNumber INTEGER NOT NULL,
  productCode VARCHAR(15) NOT NULL,
  quantityOrdered INTEGER NOT NULL,
  priceEach NUMERIC NOT NULL,
  orderLineNumber SMALLINT NOT NULL,
  PRIMARY KEY (orderNumber, productCode),
  CONSTRAINT fk_order FOREIGN KEY(orderNumber) REFERENCES orders(orderNumber),
  CONSTRAINT fk_product FOREIGN KEY(productCode) REFERENCES products(productCode)
);

CREATE TABLE ProductLines(
  productLine VARCHAR(50) NOT NULL,
  textDescription VARCHAR(4000) NULL,
  PRIMARY KEY (productLine)
);




