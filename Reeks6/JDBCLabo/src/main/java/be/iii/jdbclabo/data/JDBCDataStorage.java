package be.iii.jdbclabo.data;

import be.iii.jdbclabo.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
@PropertySource("classpath:sqlopdrachten.properties")
public class JDBCDataStorage implements IDataStorage {

    // kolommen producten
    /*@Value("${prod_code}")
    private String prodCode;
    @Value("${prod_name}")
    private String prodName;
    @Value("${prod_line}")
    private String prodLine;
    @Value("${prod_scale}")
    private String prodScalee;
    @Value("${prod_vendor}")
    private String prodVendor;
    @Value("${prod_description}")
    private String prodDescript;
    @Value("${prod_stock}")
    private String prodStock;
    @Value("${prod_price}")
    private String prodPrice;
    @Value("${prod_msrp}")
    private String prodMsrp;*/

    // kolommen customer
    @Value("${klant_number}")
    private String klantNumber;
    @Value("${klant_name}")
    private String klantName;
    @Value("${klant_lname}")
    private String klantLName;
    @Value("${klant_fname}")
    private String klantFName;
    @Value("${klant_phone}")
    private String klantPhone;
    @Value("${klant_address1}")
    private String klantAddress1;
    @Value("${klant_address2}")
    private String klantAddress2;
    @Value("${klant_city}")
    private String klantCity;
    @Value("${klant_state}")
    private String klantState;
    @Value("${klant_pcode}")
    private String klantPCode;
    @Value("${klant_country}")
    private String klantCountry;
    @Value("${klant_repnumber}")
    private String klantRepNumber;
    @Value("${klant_climit}")
    private String klantCLimit;

    // kolommen orders
    /*@Value("${order_number}")
    private String orderNumber;
    @Value("${order_date}")
    private String orderDate;
    @Value("${order_rdate}")
    private String orderRDate;
    @Value("${order_sdate}")
    private String orderSDate;
    @Value("${order_status}")
    private String orderStatus;
    @Value("${order_comments}")
    private String orderComments;
    @Value("${order_cnumber}")
    private String orderCNumber;*/

    // SQL-opdrachten
    @Value("${insert_order}")
    private String insertOrder;
    @Value("${insert_orderdetail}")
    private String insertOrderDetail;
    @Value("${select_products}")
    private String selectProducts;
    @Value("${select_customers}")
    private String selectCustomers;
    @Value("${select_orders_klant}")
    private String selectOrdersKlant;
    @Value("${select_max_customer_number}")
    private String selectMaxCustomerNumber;
    @Value("${select_max_order_number}")
    private String selectMaxOrderNumber;
    @Value("${insert_customer}")
    private String insertCustomer;
    @Value(("${update_customer}"))
    private String updateCustomer;
    @Value("${delete_customer}")
    private String deleteCustomer;
    @Value("${procedure_get_total}")
    private String procGetTotal;

    // fouten
    /*@Value("${fout_products}")
    private String foutProducts;*/
    @Value("${fout_customers}")
    private String foutCustomers;
    /*@Value("${fout_orders}")
    private String foutOrders;*/
    @Value("${fout_customer_number")
    private String foutCustomerNumber;
   /* @Value("${fout_order_number}")
    private String foutOrderNumber;*/
    @Value("${fout_add_order}")
    private String foutAddOrder;
    @Value("${fout_insert_customer}")
    private String foutInsertCustomer;
    /*@Value("${fout_update_customer}")
    private String foutUpdateCustomer;
    @Value("${fout_delete_customer}")
    private String foutDeleteCustomer;*/
    @Value("${fout_get_total}")
    private String foutGetTotal;

    private DataSource dataSource;
    private JdbcClient jdbcClient;

    public JDBCDataStorage(DataSource dataSource, JdbcClient jdbcClient) {
        this.dataSource = dataSource;
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<IProduct> getProducts() throws DataExceptie {
        List<IProduct> products = new ArrayList<>();
        products.addAll(jdbcClient.sql(selectProducts)
                .query(Product.class).list()
        );
        return products;
    }
    @Override
    public List<ICustomer> getCustomers() throws DataExceptie {
        List<ICustomer> customers;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectCustomers);
            customers = new ArrayList<>();
            while (rs.next()) {
                customers.add(createCustomer(rs));
            }
        } catch (SQLException ex) {
            throw new DataExceptie(foutCustomers);
        }

        return customers;
    }

    //Maak customer
    private ICustomer createCustomer(ResultSet rs) throws SQLException {

        return new Customer(rs.getInt(klantNumber),
                rs.getString(klantName),
                rs.getString(klantLName),
                rs.getString(klantFName),
                rs.getString(klantPhone),
                rs.getString(klantAddress1),
                rs.getString(klantAddress2),
                rs.getString(klantCity),
                rs.getString(klantState),
                rs.getString(klantPCode),
                rs.getString(klantCountry),
                rs.getInt(klantRepNumber),
                rs.getDouble(klantCLimit));
    }


    @Override
    public List<IOrderWithoutDetails> getOrders(int customerNumber) throws DataExceptie {
        List<IOrderWithoutDetails> orders = new ArrayList<>();

        orders.addAll(jdbcClient.sql(selectOrdersKlant)
                .param(customerNumber)
                .query(OrderWithoutDetails.class).list());
        return orders;

    }

    @Override
    public int maxCustomerNumber() throws DataExceptie {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(selectMaxCustomerNumber);
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new DataExceptie(foutCustomerNumber);
            }


        } catch (SQLException ex) {
            throw new DataExceptie(foutCustomers);
        }
    }

    @Override
    public int maxOrderNumber() throws DataExceptie {
        return jdbcClient.sql(selectMaxOrderNumber)
                .query(Integer.class).single();
    }


    @Override
    public void addOrder(IOrder order) throws DataExceptie {
        try (Connection conn = dataSource.getConnection()) {
            try {
                conn.setAutoCommit(false);
                addOrder(conn, order);

                for (IOrderDetail detail : order.getDetails()) {
                    addOrderDetail(conn, detail);
                }

                conn.commit();
            } catch (SQLException se) {
                conn.rollback();
                throw new DataExceptie(foutAddOrder);
            } finally {
                conn.setAutoCommit(true);

            }
        } catch (Exception ex) {
            throw new DataExceptie(foutAddOrder);
        }
    }

    private void addOrder(Connection conn, IOrder order) throws SQLException {
        try (PreparedStatement stmt =
                     conn.prepareStatement(insertOrder)) {
            stmt.setInt(1, order.getOrderNumber());
            stmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setDate(3, new java.sql.Date(order.getRequiredDate().getTime()));
            stmt.setNull(4, java.sql.Types.DATE); //Nog niet verzonden, dus geen ShippingDate
            stmt.setString(5, order.getStatus());
            if (order.getComments() != null && !order.getComments().equals("")) {
                stmt.setString(6, order.getComments());
            } else {
                stmt.setNull(6, java.sql.Types.VARCHAR);
            }
            stmt.setInt(7, order.getCustomerNumber());
            stmt.executeUpdate();
        }
    }

    private void addOrderDetail(Connection conn, IOrderDetail detail) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(insertOrderDetail)) {
            stmt.setInt(1, detail.getOrderNumber());
            stmt.setString(2, detail.getProductCode());
            stmt.setInt(3, detail.getQuantity());
            stmt.setDouble(4, detail.getPrice());
            stmt.setInt(5, detail.getOrderLineNumber());
            stmt.executeUpdate();
        }
    }

    @Override
    public void addCustomer(ICustomer customer) throws DataExceptie {


        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertCustomer)) {
            stmt.setInt(1, customer.getCustomerNumber());
            stmt.setString(2, customer.getCustomerName());
            stmt.setString(3, customer.getContactLastName());
            stmt.setString(4, customer.getContactFirstName());
            stmt.setString(5, customer.getPhone());
            stmt.setString(6, customer.getAddressLine1());
            if (customer.getAddressLine2() != null && !customer.getAddressLine2().equals("")) {
                stmt.setString(7, customer.getAddressLine2());
            } else {
                stmt.setNull(7, java.sql.Types.VARCHAR);
            }
            stmt.setString(8, customer.getCity());
            if (customer.getState() != null && !customer.getState().equals("")) {
                stmt.setString(9, customer.getState());
            } else {
                stmt.setNull(9, java.sql.Types.VARCHAR);
            }
            if (customer.getPostalCode() != null && !customer.getPostalCode().equals("")) {
                stmt.setString(10, customer.getPostalCode());
            } else {
                stmt.setNull(10, java.sql.Types.VARCHAR);
            }
            stmt.setString(11, customer.getCountry());
            if (customer.getSalesRepEmployeeNumber() != 0) {
                stmt.setInt(12, customer.getSalesRepEmployeeNumber());
            } else {
                stmt.setNull(12, java.sql.Types.INTEGER);
            }
            if (customer.getCreditLimit() != 0) {
                stmt.setDouble(13, customer.getCreditLimit());
            } else {
                stmt.setNull(13, java.sql.Types.DOUBLE);
            }
            stmt.executeUpdate();

        } catch (SQLException ex) {
            throw new DataExceptie(foutInsertCustomer);
        }

    }

    @Override
    public void modifyCustomer(ICustomer customer) throws DataExceptie {
        jdbcClient.sql(updateCustomer)
                .param(customer.getCustomerName())
                .param(customer.getContactLastName())
                .param(customer.getContactFirstName())
                .param(customer.getPhone())
                .param(customer.getAddressLine1())
                .param(customer.getAddressLine2())
                .param(customer.getCity())
                .param(customer.getState())
                .param(customer.getPostalCode())
                .param(customer.getCountry())
                .param(customer.getSalesRepEmployeeNumber())
                .param(customer.getCreditLimit())
                .param(customer.getCustomerNumber())
                .update();
    }


    @Override
    public void deleteCustomer(int customerNumber) throws DataExceptie {
        jdbcClient.sql(deleteCustomer).param(customerNumber).update();
    }


    @Override
    public double getTotal(int customerNumber) throws DataExceptie {
        double resultaat;

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(procGetTotal)) {
            stmt.setInt(2, customerNumber);
            stmt.registerOutParameter(1, Types.DOUBLE);
            stmt.executeUpdate();
            resultaat = stmt.getDouble(1);

        } catch (SQLException ex) {
            throw new DataExceptie(foutGetTotal);
        }

        return resultaat;
    }
}
