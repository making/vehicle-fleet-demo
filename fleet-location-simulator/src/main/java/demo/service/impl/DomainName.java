package demo.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class DomainName {
    @Value("${vcap.application.application_uris[0]:}")
    String applicationUri;
    @Value("${vcap.application.name:}")
    String applicationName;
    String domainName = "";

    @PostConstruct
    void init() {
        this.domainName = applicationUri.replace(applicationName, "");
    }


    public String value() {
        return this.domainName;
    }
}
