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

}
