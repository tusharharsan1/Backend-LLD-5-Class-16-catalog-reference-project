package com.scaler.productcatalogservice.services;

import com.scaler.productcatalogservice.dtos.ProductDto;
import com.scaler.productcatalogservice.models.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface IProductService {

    Product getProductById(UUID id);

    List<Product> getAllProducts();

    Product createProduct(Product input);

    Product replaceProduct(Product input, UUID id);

    Product updateProduct(ProductDto input, UUID id);

    boolean deleteProduct(UUID id);

    Page<Product> searchProducts(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
}
