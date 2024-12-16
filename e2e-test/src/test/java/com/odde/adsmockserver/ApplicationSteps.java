package com.odde.adsmockserver;

import com.github.leeonky.cucumber.restful.RestfulStep;
import com.github.leeonky.jfactory.JFactory;
import io.cucumber.java.Before;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.URISyntaxException;

@ContextConfiguration(classes = {CucumberConfiguration.class}, loader = SpringBootContextLoader.class)
@CucumberContextConfiguration
public class ApplicationSteps {

    @Autowired
    private RestfulStep restfulStep;

    @Autowired
    private JFactory jFactory;

    @Before(order = 0)
    public void setupRestfulStep() {
        restfulStep.setJFactory(jFactory);
        restfulStep.setBaseUrl("http://localhost:5000");
    }

    @Before(order = 1)
    public void clearAllSymbols() throws IOException, URISyntaxException {
        restfulStep.delete("/symbols");
    }

}
