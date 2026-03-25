package com.randomuser.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI randomUserOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8083");
        localServer.setDescription("Local Development Server");

        Contact contact = new Contact();
        contact.setName("RandomUser API Support");
        contact.setEmail("support@randomuser-app.com");
        contact.setUrl("http://localhost:8083");

        License license = new License()
                .name("Apache 2.0")
                .url("http://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("RandomUser API")
                .version("1.0.0")
                .description(
                    "Spring Boot REST API that integrates with RandomUser.me (https://randomuser.me) " +
                    "to generate random user profiles and provides full CRUD operations on an in-memory store. " +
                    "Fetch users by count or nationality, search by country, and manage stored users via " +
                    "POST / PUT / DELETE endpoints."
                )
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
