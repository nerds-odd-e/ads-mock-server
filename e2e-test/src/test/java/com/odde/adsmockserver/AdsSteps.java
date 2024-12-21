package com.odde.adsmockserver;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import static com.github.leeonky.dal.Assertions.expect;

public class AdsSteps {

    @Autowired
    private AdsOperations adsOperations;

    @Then("ads operation should:")
    public void adsOperationShould(String expression) {
        expect(adsOperations).should(expression);
    }

    @Then("ads read LREAL array symbol by name {string} and size {int} should:")
    public void adsReadArraySymbolByNameAndSizeShould(String name, int size, String expression) {
        expect(adsOperations.readLRealArraySymbolByName(name, size)).should(expression);
    }

    @Then("ads get device info should be:")
    public void adsGetDeviceInfoShouldBe(String expression) {
       expect(adsOperations.readDeviceInfo()).should(expression);
    }

    @When("write INT symbol {string} with new value {string}")
    public void writeINTSymbolWithNewValue(String name, String value) {
        adsOperations.writeIntSymbolByName(name, Short.parseShort(value));
    }

    @When("write BOOL symbol {string} with new value {string}")
    public void writeBOOLSymbolWithNewValue(String name, String value) {
        adsOperations.writeBoolSymbolByName(name, Boolean.parseBoolean(value));
    }

    @When("write LREAL symbol {string} with new value {string}")
    public void writeLREALSymbolsWithNewValue(String name, String value) {
        adsOperations.writeLRealSymbolByName(name, Double.parseDouble(value));
    }

    @When("write REAL symbol {string} with new value {string}")
    public void writeREALSymbolsWithNewValue(String name, String value) {
        adsOperations.writeRealSymbolByName(name, Float.parseFloat(value));
    }

    @When("write DINT symbol {string} with new value {string}")
    public void writeDINTSymbolWithNewValue(String name, String value) {
        adsOperations.writeDIntSymbolByName(name, Integer.parseInt(value));
    }

    @When("write LREAL array symbol {string} with new value:")
    public void writeLREALArraySymbolWithNewValue(String name, DataTable table) {
        adsOperations.writeLRealArraySymbolByName(name, table.asList().stream().map(Double::parseDouble).toArray(Double[]::new));
    }
}
