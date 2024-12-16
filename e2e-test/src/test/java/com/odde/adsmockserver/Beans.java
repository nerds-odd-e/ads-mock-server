package com.odde.adsmockserver;

import com.github.leeonky.jfactory.JFactory;
import com.github.leeonky.jfactory.Spec;
import com.github.leeonky.util.Classes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {

    @Bean
    public JFactory jFactory() {
        JFactory jFactory = new JFactory();
        Classes.subTypesOf(Spec.class, "com.odde.adsmockserver.spec").forEach(c -> jFactory.register((Class) c));
        return jFactory;
    }
}
