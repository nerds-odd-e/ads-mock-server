package com.odde.adsmockserver;

import io.cucumber.java.en.Then;
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
}
