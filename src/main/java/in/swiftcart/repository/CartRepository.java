package in.swiftcart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.swiftcart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{
	
	public Optional<Cart> findByUserId(Long userId);
	
	public boolean existsByUserId(Long userId);
	
	void deleteByUserId(Long userId);
	
	@Query("Select c From Cart c "+
			"LEFT JOIN FETCH c.cartItems ci "+
			"LEFT JOIN FETCH ci.product "+
			"Where c.user.id = :userId "
			)
	Optional<Cart> findByUserIdWithItems(@Param("userId") Long userId);

}
