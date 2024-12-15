package com.odde.adsmockserver;

import com.github.leeonky.cucumber.restful.RestfulStep;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {CucumberConfiguration.class}, loader = SpringBootContextLoader.class)
@CucumberContextConfiguration
public class ApplicationSteps {

    @Autowired
    private RestfulStep restfulStep;

    @Before
    public void setBaseUrl() {
        restfulStep.setBaseUrl("http://localhost:5000");
    }

}
