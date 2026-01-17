package be.iii.jdbclabo.data;

import be.iii.jdbclabo.model.*;

import java.util.List;

public class JDBCDataStorage implements IDataStorage {

    @Override
    public List<IProduct> getProducts() throws DataExceptie {
        return null;
    }


    @Override
    public List<ICustomer> getCustomers() throws DataExceptie {
        return null;
    }

    @Override
    public List<IOrderWithoutDetails> getOrders(int customerNumber) throws DataExceptie {
        return null;
    }

    @Override
    public int maxCustomerNumber() throws DataExceptie {
        return 0;
    }

    @Override
    public int maxOrderNumber() throws DataExceptie {
        return 0;
    }


    @Override
    public void addOrder(IOrder order) throws DataExceptie {

    }


    @Override
    public void addCustomer(ICustomer customer) throws DataExceptie {

    }


    @Override
    public void modifyCustomer(ICustomer customer) throws DataExceptie {
    }


    @Override
    public void deleteCustomer(int customerNumber) throws DataExceptie {
    }


    @Override
    public double getTotal(int customerNumber) throws DataExceptie {
        return 0;
    }
}
