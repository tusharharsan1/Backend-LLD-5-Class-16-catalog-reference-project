package com.scaler.productcatalogservice.repositories;

import com.scaler.productcatalogservice.models.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CategoryRepo extends JpaRepository<Category, UUID> {

    Optional<Category> findByName(String name);

    @Query("select c from Category c left join fetch c.products")
    List<Category> findAllWithProducts();
}
