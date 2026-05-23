package in.swiftcart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.swiftcart.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
