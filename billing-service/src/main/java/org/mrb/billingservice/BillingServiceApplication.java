package org.mrb.billingservice;

import org.mrb.billingservice.entities.Bill;
import org.mrb.billingservice.entities.ProductItem;
import org.mrb.billingservice.feign.CustomerRestClient;
import org.mrb.billingservice.feign.ProductItemRestClient;
import org.mrb.billingservice.model.Customer;
import org.mrb.billingservice.model.Product;
import org.mrb.billingservice.repositories.BillRepository;
import org.mrb.billingservice.repositories.ProductItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

@SpringBootApplication
@EnableFeignClients
public class BillingServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(BillingServiceApplication.class, args);
    }
@Bean
    CommandLineRunner start(
            BillRepository billRepository,
            ProductItemRepository productItemRepository,
            CustomerRestClient customerRestClient,
            ProductItemRestClient productItemRestClient
            ){
        return args -> {
            Customer customer=customerRestClient.getCustomerById(1L);//on récupère un client
            //ajout d'une nvelle facture
            Bill bill1=billRepository.save(new Bill(null,new Date(),null,customer.getId(),null));
            //permet de récupèrer ts les produits
            PagedModel<Product> productPagedModel=productItemRestClient.pageProduct();
            //ajouter des productItems pour chaque product
            productPagedModel.forEach(p -> {
                ProductItem productItem = new ProductItem();
                productItem.setPrice(p.getPrice());
                productItem.setQuantity(1+new Random().nextInt(100));
                productItem.setBill(bill1);
                productItem.setProductID(p.getId());
                productItemRepository.save(productItem);
            });


        };
    }

}
