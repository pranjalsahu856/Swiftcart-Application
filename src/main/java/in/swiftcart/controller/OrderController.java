package in.swiftcart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.swiftcart.dto.request.PlaceOrderRequestDTO;
import in.swiftcart.dto.request.UpdateOrderStatusRequestDTO;
import in.swiftcart.dto.response.ApiResponseDTO;
import in.swiftcart.dto.response.OrderResponseDTO;
import in.swiftcart.dto.response.PageResponseDTO;
import in.swiftcart.service.OrderService;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/orders")
public class OrderController {
	
	private OrderService orderServ;

	@Autowired
	public OrderController(OrderService orderServ) {
		this.orderServ = orderServ;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> placeOrder(@Valid @RequestBody PlaceOrderRequestDTO placeOrderRequestDTO){
		OrderResponseDTO order = orderServ.placeOrder(placeOrderRequestDTO);
		return new ResponseEntity<>(ApiResponseDTO.success("Order placed successfully",order),HttpStatus.CREATED);
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderById(@PathVariable Long orderId){
		OrderResponseDTO order = orderServ.getOrderById(orderId);
		return ResponseEntity.ok(ApiResponseDTO.success(order));
	}
	
	@GetMapping("/number/{orderNumber}")
	public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderByOrderNumber(@PathVariable String orderNumber){
		OrderResponseDTO order = orderServ.getOrderByOrderNumber(orderNumber);
		return ResponseEntity.ok(ApiResponseDTO.success(order));
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrderByUserId(@PathVariable Long userId){
		List<OrderResponseDTO> orders = orderServ.getOrdersByUserId(userId);
		return ResponseEntity.ok(ApiResponseDTO.success("Fetched "+orders.size()+" orders",orders));
	}
	
	@GetMapping
	public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> getAllOrdersPaginated(
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "orderDate") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir ){
		PageResponseDTO<OrderResponseDTO> orders = orderServ.getAllOrdersPaginated(page, size, sortBy, sortDir);
		return ResponseEntity.ok(ApiResponseDTO.success(orders));
	}
	
	@GetMapping("/status/{status}")
	public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByStatus(@PathVariable String status){
		List<OrderResponseDTO> orders = orderServ.getOrdersByStatus(status);
		return ResponseEntity.ok(ApiResponseDTO.success(orders));
	}
	
	@PatchMapping("/{orderId}/status")
	public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> updateOrderStatus(@PathVariable Long orderId,
			@Valid @RequestBody UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO){
		OrderResponseDTO order = orderServ.updateOrderStatus(orderId,updateOrderStatusRequestDTO);
		return ResponseEntity.ok(ApiResponseDTO.success("Order Status updated successfully",order));
	}
	
	@PostMapping("/{orderId}/cancel")
	public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> cancelOrder(
			@PathVariable Long orderId,
			@RequestParam(required = false,defaultValue="Customer Requested Cancellation") String reason ){
		OrderResponseDTO order = orderServ.cancelOrder(orderId, reason);
		return ResponseEntity.ok(ApiResponseDTO.success("Order cancelled successfully",order));
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> searchOrders(
			@RequestParam String keyword,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam (defaultValue = "10") int size){
		PageResponseDTO<OrderResponseDTO> orders = orderServ.searchOrders(keyword, page, size);
		return ResponseEntity.ok(ApiResponseDTO.success(orders));		
	}
	
}

