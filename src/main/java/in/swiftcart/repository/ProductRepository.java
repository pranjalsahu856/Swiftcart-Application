package in.swiftcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.swiftcart.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	public Optional<Product> findBySku(String sku);

	public boolean existsBySku(String sku);

	public List<Product> findByIsAvailableTrue();

	public List<Product> findByCategoryIgnoreCase(String category);

	public List<Product> findByPriceBetween(double minPrice, double maxPrice);

	@Query("Select p from Product p Where (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword ,'%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword ,'%')) AND p.isAvailable=true)")
	public Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

	@Modifying
	@Query("Update Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
	public int increaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

	@Modifying
	@Query("Update Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId AND p.stockQuantity >= :quantity")
	public int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

	@Query("Select p from Product p Where p.stockQuantity<=:threshold AND p.isAvailable=true")
	public List<Product> findLowStockProducts(@Param("threshold") Integer threshold);

}
