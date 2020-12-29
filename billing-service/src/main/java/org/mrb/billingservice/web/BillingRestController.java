package org.mrb.billingservice.web;

import org.mrb.billingservice.entities.Bill;
import org.mrb.billingservice.feign.CustomerRestClient;
import org.mrb.billingservice.feign.ProductItemRestClient;
import org.mrb.billingservice.model.Customer;
import org.mrb.billingservice.model.Product;
import org.mrb.billingservice.repositories.BillRepository;
import org.mrb.billingservice.repositories.ProductItemRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingRestController {
    private BillRepository billRepository;
    private ProductItemRepository productItemRepository;
    private CustomerRestClient customerRestClient;
    private ProductItemRestClient productItemRestClient;
//injection de dépendance via le constructeur
    public BillingRestController(BillRepository billRepository, ProductItemRepository productItemRepository, CustomerRestClient customerRestClient, ProductItemRestClient productItemRestClient) {
        this.billRepository = billRepository;
        this.productItemRepository = productItemRepository;
        this.customerRestClient = customerRestClient;
        this.productItemRestClient = productItemRestClient;
    }


    @GetMapping(path = "/fullBill/{id}")
    public Bill getBill(@PathVariable Long id){
        Bill bill=billRepository.findById(id).get();
        Customer customer=customerRestClient.getCustomerById(bill.getCustomerID());
        bill.setCustomer(customer);
        //pour chaque productItem on veut les détails du produit
        bill.getProductItems().forEach(pi -> {
            Product product=productItemRestClient.getProductById(pi.getProductID());
           // pi.setProduct(product);
            pi.setProductName(product.getName());
        });

        return bill;
    }
}
