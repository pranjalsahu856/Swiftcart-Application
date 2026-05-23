package in.swiftcart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.swiftcart.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findByIsActiveTrue();

	@Query("Select u from User u Where (LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword ,'%'))) OR (LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword ,'%')))")
	List<User> searchByNameOrEmail(@Param("keyword") String keyword);

}
