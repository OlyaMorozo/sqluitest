package org.sqluitest;

import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static org.sqluitest.Const.Column.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Selenide.*;

public class SqlUiTest {
    private static final SqlUiPage page = new SqlUiPage();

    @BeforeClass
    public static void openPage() {
        open("https://www.w3schools.com/sql/trysql.asp?filename=trysql_select_all");
    }

    @Before
    public void restoreDatabase() {
        page.restoreDataBase();
    }

    @Test
    public void checkUserAndAddress() {
        page.executeStatement("SELECT * FROM Customers;");
        SelenideElement row = page.getRowWhere(CONTACT_NAME, "Giovanni Rovelli").shouldBe(visible);
        page.getColumnFromRow(row, ADDRESS).shouldHave(text("Via Ludovico il Moro 22"));
    }

    @Test
    public void checkLondonersSize() {
        page.executeStatement("SELECT * FROM Customers WHERE City = 'London';");
        page.getRows().shouldHaveSize(6);
    }

    @Test
    public void checkNewRowCreated() {
        String insertStatement = "INSERT INTO Customers('CustomerName','ContactName','Address','City','PostalCode','Country')" +
                " VALUES ('Morozova Olga','Morozova Olga','Pushkina 5','Novosibirsk', '630132', 'Russia');";
        page.executeStatement(insertStatement);
        page.getModifyTableResult().shouldBe(visible);

        String selectStatement = "SELECT * FROM Customers"
                + " WHERE CustomerName = 'Morozova Olga' AND ContactName = 'Morozova Olga'"
                + " AND Address = 'Pushkina 5' AND City = 'Novosibirsk'"
                + " AND PostalCode = '630132' AND Country = 'Russia';";
        page.executeStatement(selectStatement);
        page.getRows().shouldHave(sizeGreaterThan(0));
    }

    @Test
    public void checkRowUpdated() {
        page.executeStatement("SELECT * FROM Customers;");
        int rowsSize = page.getRows().shouldHave(sizeGreaterThan(0)).size();
        int customerId =  new Random().nextInt(rowsSize) + 1;

        String updateStatement = String.format("UPDATE Customers"
                + " SET CustomerName = 'Morozova Olga', ContactName = 'Morozova Olga',"
                + " Address = 'Pushkina 5', City = 'Novosibirsk',"
                + " PostalCode = 630132, Country = 'Russia'"
                + " WHERE CustomerID = %s;", customerId);
        page.executeStatement(updateStatement);
        page.getModifyTableResult().shouldBe(visible);

        String selectStatement = String.format("SELECT * FROM Customers WHERE CustomerID = %s"
                + " AND CustomerName = 'Morozova Olga' AND ContactName = 'Morozova Olga'"
                + " AND Address = 'Pushkina 5' AND City = 'Novosibirsk'"
                + " AND PostalCode = '630132' AND Country = 'Russia';", customerId);
        page.executeStatement(selectStatement);
        page.getRows().shouldHave(sizeGreaterThan(0));
    }
}
