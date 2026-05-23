package in.swiftcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.swiftcart.entity.Order;
import in.swiftcart.entity.Product;

public interface OrderRepository extends JpaRepository<Order, Long> {

	// Returns List of Order of a Specific user in desc order of Date
	List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

	// Return Order of a Specific User Based on Pagination
	Page<Order> findByUserId(Long userId, Pageable pageable);

	// List of orders based on status
	List<Order> findByStatus(String status);

	// Return Order with Item on the basis of orderId
	@Query("Select o From Order o " +
			"LEFT JOIN FETCH o.orderItems oi " +
			"LEFT JOIN FETCH oi.product "+
			"Where o.id = :orderId ")
	Optional<Order> findByIdWithItems(@Param("orderId") Long orderId);

	// Return Order with Item on the basis of orderNumber
	@Query("Select o From Order o " +
			"LEFT JOIN FETCH o.orderItems oi " +
			"LEFT JOIN FETCH oi.product "+ 
			"Where o.orderNumber = :orderNumber ")
	Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);
	
	@Query(" Select o from Order o WHERE o.orderNumber LIKE %:keyword% "+
			"OR o.user.fullName LIKE %:keyword% "+
			"OR o.user.email LIKE %:keyword% ")
	public Page<Order> searchOrders(@Param("keyword") String keyword,Pageable pageable);

}
