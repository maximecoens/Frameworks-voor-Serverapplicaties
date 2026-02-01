package be.ugent.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hoofdklasse van de Spring Boot applicatie.
 * 
 * @SpringBootApplication is een combinatie van:
 * - @Configuration: markeert de klasse als bron van bean definities
 * - @EnableAutoConfiguration: schakelt Spring Boot's auto-configuratie in
 * - @ComponentScan: scant het package naar componenten, services, controllers, etc.
 */
@SpringBootApplication
public class BlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApplication.class, args);
    }
}
