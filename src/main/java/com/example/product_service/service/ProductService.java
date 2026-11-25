package com.example.product_service.service;

import com.example.product_service.config.AppConstant;
import com.example.product_service.dto.ProductRequest;
import com.example.product_service.dto.ProductResponse;
import com.example.product_service.entity.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

    private  ProductRepository productRepository;
    private  KafkaTemplate<String, ProductResponse> kafkaTemplate;

    public ProductService(ProductRepository productRepository,
                          KafkaTemplate<String, ProductResponse> kafkaTemplate) {
        this.productRepository = productRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 1. Create product and Send Event using Kafka
    @Transactional
    public ProductResponse create(ProductRequest request){
        Product product = mapToEntity(request);
        Product savedProduct = productRepository.save(product);
        ProductResponse response = mapToResponse(savedProduct, "Product Added Successfully");
        kafkaTemplate.send(AppConstant.PRODUCT_CREATED, response);
        return response;
    }

    @Transactional
    public Product getProductById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Transactional
    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }


    protected Product mapToEntity(ProductRequest request){
        return new Product(
                request.name(),
                request.price(),
                request.quantity()
        );
    }

    protected ProductResponse mapToResponse(Product product, String message){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                message
        );
    }

}
