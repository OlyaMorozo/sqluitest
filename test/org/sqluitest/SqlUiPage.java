package org.sqluitest;

import com.codeborne.selenide.*;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Selectors.*;

public class SqlUiPage {

    public void executeStatement(String statement) {
        String jsCode = String.format("window.editor.getDoc().setValue(\"%s\");", statement)
                + "w3schoolsSQLSubmit();";
        executeJavaScript(jsCode);
    }

    public SelenideElement getModifyTableResult() {
        return $(byXpath("//div[@id='divResultSQL']/div[contains(text(), 'You have made changes to the database.')]"));
    }

    public ElementsCollection getRows() {
        return $$(byXpath("//div[@id='divResultSQL']//tbody/tr[not(descendant::th)]"));
    }

    public SelenideElement getRowWhere(String columnName, String value) {
        int columnIndex = getColumnIndexByName(columnName);
        String rowXpath = String.format(
                "//div[@id='divResultSQL']//tbody/tr[td[%s][contains(text(), '%s')]]", columnIndex + 1, value
        );
        return $(byXpath(rowXpath));
    }

    public SelenideElement getColumnFromRow(SelenideElement row, String columnName) {
        int columnIndex = getColumnIndexByName(columnName);
        String columnSelector = String.format("td:nth-child(%s)", columnIndex + 1);
        return row.find(columnSelector);
    }

    public void restoreDataBase() {
        $("#restoreDBBtn").shouldBe(visible).click();
        prompt();
    }

    private int getColumnIndexByName(String columnName) {
        ElementsCollection columns = $$("#divResultSQL tbody tr th")
                .shouldBe(sizeGreaterThan(0));
        SelenideElement column = columns.findBy(exactText(columnName));
        return columns.indexOf(column);
    }
}
