package com.example.demo.modules.keycloack;

import com.example.demo.modules.keycloack.services.EmbeddedKeycloakRequestFilter;
import lombok.AllArgsConstructor;
import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.*;
import javax.naming.spi.NamingManager;
import javax.servlet.Filter;
import javax.sql.DataSource;

@Configuration
public class EmbeddedKeycloakConfig {

    @Bean
    ServletRegistrationBean keycloakJaxRsApplication(
            KeycloakServerProperties keycloakServerProperties, DataSource dataSource) throws Exception {

        mockJndiEnvironment(dataSource);
        EmbeddedKeycloakApplication.keycloakServerProperties = keycloakServerProperties;
        ServletRegistrationBean servlet = new ServletRegistrationBean<>(
                new HttpServlet30Dispatcher());
        servlet.addInitParameter("javax.ws.rs.Application",
                EmbeddedKeycloakApplication.class.getName());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
                keycloakServerProperties.getContextPath());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS,
                "true");
        servlet.addUrlMappings(keycloakServerProperties.getContextPath() + "/*");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        return servlet;
    }

    @Bean
    FilterRegistrationBean keycloakSessionManagement(
            KeycloakServerProperties keycloakServerProperties) {
        FilterRegistrationBean<Filter> filter = new FilterRegistrationBean<>();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new EmbeddedKeycloakRequestFilter());
        filter.addUrlPatterns(keycloakServerProperties.getContextPath() + "/*");

        return filter;
    }

    private void mockJndiEnvironment(DataSource dataSource) throws NamingException {
        NamingManager.setInitialContextFactoryBuilder(
                (env) -> (environment) -> new InitialContext() {
                    @Override
                    public Object lookup(Name name) {
                        return lookup(name.toString());
                    }

                    @Override
                    public Object lookup(String name) {
                        if ("spring/datasource".equals(name)) {
                            return dataSource;
                        }
                        return null;
                    }

                    @Override
                    public NameParser getNameParser(String name) {
                        return CompositeName::new;
                    }

                    @Override
                    public void close() {
                    }
                });
    }

}
