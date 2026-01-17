using System.Data.Common;
using Winkel;

// Hier worden de klassen DataStorageMetReader
// en DataStorageMetDataTable getest.


// Register provider
DbProviderFactories.RegisterFactory("Microsoft.Data.SqlClient", Microsoft.Data.SqlClient.SqlClientFactory.Instance);


DataStorageMetReader storage = new();
WriteCustomers(storage);

//Klant toevoegen
AddCustomer(storage);

WriteCustomers(storage);

//Bestelling toevoegen;
AddOrder(storage);

DataStorageMetDataTable storageDataTable = new();

//Zes customers toevoegen,
// nl.  drie met nummer 888xxxx
//   en drie met nummer 999xxxx
int nrLaatsteCustomer = LastCustomerNumber(storageDataTable);
WriteCustomersInDataTable(storageDataTable);

//Customers met nummers 888xxxx weghalen
DeleteCustomersAbove(storageDataTable);
WriteCustomersInDataTable(storageDataTable);

//Customer aanpassen 
UpdateCustomers(storageDataTable, nrLaatsteCustomer);
WriteCustomersInDataTable(storageDataTable);

static void WriteCustomers(DataStorageMetReader storage)
{
    List<Customer> customers = storage.GetCustomers();

    Console.WriteLine("lijst heeft lengte " + customers.Count);

    foreach (Customer cust in customers)
    {
        Console.WriteLine(cust.ToString());
    }
    Console.WriteLine("EINDE DATABASELIJST");

}

static void AddCustomer(DataStorageMetReader storage)
{

    Customer c = new()
    {
        AddressLine1 = "EEN !",
        AddressLine2 = "TWEE !",
        City = "city",
        ContactFirstName = "voornaampje",
        ContactLastName = "achternaam contactpersoon",
        Country = "BE",
        CreditLimit = 50.00,
        CustomerName = "naam van klant",
        CustomerNumber = 789789789,
        Phone = "003212456",
        PostalCode = "XM4545",
        SalesRepEmployeeNumber = 10101010,
        State = "West-Vlaanderen"
    };


    storage.AddCustomer(c);

}

static void AddOrder(DataStorageMetReader storage)
{
    Random random = new();
    Order order = new()
    {
        Comments = "dit order heeft geen comments",
        CustomerNumber = 99999,
        Number = 110000 + random.Next(1, 10000),
        Ordered = DateTime.ParseExact("01/12/2018", "dd/MM/yyyy", System.Globalization.CultureInfo.InvariantCulture),
        Required = DateTime.ParseExact("25/12/2018", "dd/MM/yyyy", System.Globalization.CultureInfo.InvariantCulture),
        Shipped = DateTime.ParseExact("13/12/2018", "dd/MM/yyyy", System.Globalization.CultureInfo.InvariantCulture),
        Status = "ok"
    };

    for (int i = 0; i < 5; i++)
    {
        OrderDetail detail = new()
        {
            OrderNumber = order.Number,
            OrderLineNumber = 1 + i,
            Price = 10.0 * (1 + i),
            ProductCode = "" + ((1 + i) * 111),
            Quantity = 100 * (1 + i)
        };
        order.Details.Add(detail);
    }

    storage.AddOrder(order);
}

static void WriteCustomersInDataTable(DataStorageMetDataTable storageDataTable)
{
    List<Customer> customers = storageDataTable.GetCustomersFromDataTable_NotCertainTheyAreInDataBase();

    Console.WriteLine("\n***\n***\n***lijst in DataTABLE heeft lengte " + customers.Count + "\n***\n");
    for (int i = 120; i < customers.Count; i++)
    {
        Console.WriteLine(customers[i].ToString());
    }
    Console.WriteLine("EINDE DATATABLELIJST");

    customers = storageDataTable.GetCustomersFromDataBase_WithoutDataTableUpdate();

    Console.WriteLine("\n***\n***\n***lijst in DataBASE (oude versie) heeft lengte " + customers.Count + "\n***\n");
    for (int i = 120; i < customers.Count; i++)
    {
        Console.WriteLine(customers[i].ToString());
    }
    Console.WriteLine("EINDE DATABASELIJST");
}

static Customer MaakCustomer(int nummer)
{
    Customer c = new()
    {
        AddressLine1 = "EEN",
        AddressLine2 = "TWEE",
        City = "city",
        ContactFirstName = "Jan",
        ContactLastName = "Jans",
        Country = "BE",
        CreditLimit = 50.00,
        CustomerName = "Firma Peeters",
        CustomerNumber = nummer,
        Phone = "003212456",
        PostalCode = "XM4545",
        SalesRepEmployeeNumber = 10101010,
        State = "West-Vlaanderen"
    };

    return c;
}

static int LastCustomerNumber(DataStorageMetDataTable storageDataTable)
{

    Random random = new();
    int nummerVanLaatstToegevoegdeCustomer = 9990000;
    Console.WriteLine("\nEr worden 6 customers toegevoegd:");
    Console.WriteLine("3 met nr 888xxxx, 3 met nr 999xxxx, allen met naam 'Jan Jans'.");
    for (int i = 0; i < 3; i++)
    {
        storageDataTable.AddCustomer(MaakCustomer(8880000 + random.Next(1, 10000)));
        nummerVanLaatstToegevoegdeCustomer = 9990000 + random.Next(1, 10000);
        storageDataTable.AddCustomer(MaakCustomer(nummerVanLaatstToegevoegdeCustomer));
    }
    return nummerVanLaatstToegevoegdeCustomer;
}

static void DeleteCustomersAbove(DataStorageMetDataTable storageDataTable)
{
    Console.WriteLine("\nAlle customers met nummer 888xxxx worden verwijderd.");
    for (int i = 0; i < 10000; i++)
    {
        storageDataTable.DeleteCustomer("" + (8880000 + i));
    }
}

 static void UpdateCustomers(DataStorageMetDataTable storageDataTable, int nummerVanLaatstToegevoegdeCustomer)
{
    Console.WriteLine("\nDe laatst toegevoegde customer (999xxxx) krijgt contactName 'ABC' \nen city 'DEF' een nieuw " +
        "telefoonnr met veel nullen.");
    string nieuweContactFirstName = "ABC";
    string nieuweCity = "DEF";

    string velden = "contactFirstName;city;phone";
    string waarden = nieuweContactFirstName + ";" + nieuweCity + ";xx32922000000";

    storageDataTable.UpdateCustomer(velden, waarden, "" + nummerVanLaatstToegevoegdeCustomer);
}