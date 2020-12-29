package org.mrb.customerservice;

import org.mrb.customerservice.entities.Customer;
import org.mrb.customerservice.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;

@SpringBootApplication
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner start(CustomerRepository customerRepository, RepositoryRestConfiguration restConfiguration){
        restConfiguration.exposeIdsFor(Customer.class);
        return args -> {
            customerRepository.save(new Customer(null,"samia","ettouzany@gmail.com"));
            customerRepository.save(new Customer(null,"ziad","ziad2020@gmail.com"));
            customerRepository.save(new Customer(null,"souad","souad@gmail.com"));
            customerRepository.save(new Customer(null,"chaimaa","chaimaa1997@gmail.com"));
            customerRepository.findAll().forEach(
                    customer -> {System.out.println(customer.toString());
                    });
        };
    }

}
