package in.swiftcart.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.swiftcart.dto.request.PlaceOrderRequestDTO;
import in.swiftcart.dto.request.UpdateOrderStatusRequestDTO;
import in.swiftcart.dto.response.OrderItemResponseDTO;
import in.swiftcart.dto.response.OrderResponseDTO;
import in.swiftcart.dto.response.PageResponseDTO;
import in.swiftcart.entity.Cart;
import in.swiftcart.entity.CartItem;
import in.swiftcart.entity.Order;
import in.swiftcart.entity.OrderItem;
import in.swiftcart.entity.Product;
import in.swiftcart.entity.User;
import in.swiftcart.exception.EmptyCartException;
import in.swiftcart.exception.InsufficientStockException;
import in.swiftcart.exception.InvalidOperationException;
import in.swiftcart.exception.ResourceNotFoundException;
import in.swiftcart.repository.CartRepository;
import in.swiftcart.repository.OrderItemRepository;
import in.swiftcart.repository.OrderRepository;
import in.swiftcart.repository.ProductRepository;
import in.swiftcart.repository.UserRepository;
import in.swiftcart.service.OrderService;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

	private OrderRepository orderRepo;
	private OrderItemRepository orderItemRepo;
	private CartRepository cartRepo;
	private UserRepository userRepo;
	private ProductRepository productRepo;

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepo, OrderItemRepository orderItemRepo, CartRepository cartRepo,
			UserRepository userRepo, ProductRepository productRepo) {
		this.orderRepo = orderRepo;
		this.orderItemRepo = orderItemRepo;
		this.cartRepo = cartRepo;
		this.userRepo = userRepo;
		this.productRepo = productRepo;
	}

	private Order findOrderById(Long orderId) {
		Optional<Order> opt = orderRepo.findById(orderId);
		if(opt.isPresent()) {
			return opt.get();
		}else {
			throw new ResourceNotFoundException("Order", "id", orderId);
		}
	}
	
	private void validateStatusTransition(String currentStatus, String newStatus) {
		if(Order.STATUS_CONFIRMED.equals(currentStatus)) {
			if(!Order.STATUS_CANCELLED.equals(newStatus)) {
				throw new InvalidOperationException("Cannot transition from CONFIRMED to "+newStatus);
			}
		}else if(Order.STATUS_CANCELLED.equals(currentStatus)) {
			throw new InvalidOperationException("Cannot change Status of "+currentStatus+" order");	
		}
	}
	
	//Restore Stock when order is Cancelled
	private void restoreStock(Order order) {
		for(OrderItem orderItem : order.getOrderItems()) {
			productRepo.increaseStock(orderItem.getProduct().getId(), orderItem.getQuantity());
		}
	}
	
	private OrderResponseDTO mapToOrderResponseDTO(Order order) {
		List<OrderItemResponseDTO> items = new ArrayList<>();
		for(OrderItem item : order.getOrderItems()) {
			items.add(mapToOrderItemResponseDTO(item));
		}
		int totalItems = 0 ;
		for(OrderItem item : order.getOrderItems()) {
			totalItems = totalItems + item.getQuantity();
		}
		return OrderResponseDTO.builder()
				.id(order.getId())
				.orderNumber(order.getOrderNumber())
				.userId(order.getUser().getId())
				.userName(order.getUser().getFullName())
				.userEmail(order.getUser().getEmail())
				.items(items)
				.totalItems(totalItems)
				.totalAmount(order.getTotalAmount())
				.status(order.getStatus())
				.note(order.getNotes())
				.orderAt(order.getOrderDate())
				.build();
	}
	
	private OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem item) {
		return OrderItemResponseDTO.builder()
				.id(item.getId())
				.productId(item.getProduct().getId())
				.productName(item.getProductName())
				.productImage(item.getProduct().getImageURL())
				.productSku(item.getProductSku())
				.quantity(item.getQuantity())
				.subTotal(item.getSubTotal())
				.unitPrice(item.getUnitPrice())
				.build();
	}

	private PageResponseDTO<OrderResponseDTO> mapToPageResponse(Page<Order> orderPage){
		List<OrderResponseDTO> orders = new ArrayList<>();
		for(Order order : orderPage.getContent()) {
			orders.add(mapToOrderResponseDTO(order));
		}
		return PageResponseDTO.<OrderResponseDTO>builder()
				.content(orders)
				.pageNumber(orderPage.getNumber())
				.pageSize(orderPage.getSize())
				.totalElements(orderPage.getTotalElements())
				.totalPages(orderPage.getTotalPages())
				.first(orderPage.isFirst())
				.last(orderPage.isLast())
				.hasNext(orderPage.hasNext())
				.hasPrevious(orderPage.hasPrevious())
				.build();
	}
	
	@Override
	public OrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO) {
		// Fetch User
		Optional<User> userOptional = userRepo.findById(placeOrderRequestDTO.getUserId());
		if(!userOptional.isPresent()) {
			throw new ResourceNotFoundException("User", "id", placeOrderRequestDTO.getUserId());
		}
		User user = userOptional.get();
		
		//Fetch Cart with Item
		Optional<Cart> cartOptional = cartRepo.findByUserIdWithItems(placeOrderRequestDTO.getUserId());
		if(!cartOptional.isPresent()) {
			throw new ResourceNotFoundException("Cart", "userId", placeOrderRequestDTO.getUserId());
		}
		Cart cart = cartOptional.get();
		
		//Validate cart in no empty
		if(cart.getCartItems().isEmpty()) {
			throw new EmptyCartException("Cannot place order with an empty cart");
		}
		
		//Copy cart items to avoid concurrent modification exception
		List<CartItem> cartItemsCopy = new ArrayList<>(cart.getCartItems()); 
		
		//validate stock availability for all items 
		for(CartItem cartItem : cartItemsCopy) {
			Product product = cartItem.getProduct();
			if(!product.getIsAvailable()) {
				throw new InsufficientStockException(product.getName()+" is no longer available");
			}
			if(product.getStockQuantity()<cartItem.getQuantity()) {
				throw new InsufficientStockException(
						"Insufficient Stock for "+ product.getName() +
						". Available : "+product.getStockQuantity()+
						", Requested : "+cartItem.getQuantity()
						);
			}
		}
		
		//Calculate total amount as the sum of all cart item subtotals
		double totalAmount = 0;
		for(CartItem cartItem : cartItemsCopy) {
			totalAmount = totalAmount +cartItem.getSubTotal();
		}
		
		//Create order
		Order order = Order.builder()
				.user(user)
				.totalAmount(totalAmount)
				.notes(placeOrderRequestDTO.getNote())
				.status(Order.STATUS_CONFIRMED)
				.build();
		
		Order savedOrder = orderRepo.save(order);
		
		//Create order items from cart items
		List<OrderItem> orderItems = new ArrayList<>();
		for(CartItem cartItem : cartItemsCopy) {
			OrderItem orderItem = OrderItem.fromCartItems(cartItem);
			orderItem.setOrder(savedOrder);
			orderItems.add(orderItem);
			
			//Decrease product stock
			productRepo.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
		}
		orderItemRepo.saveAll(orderItems);
		savedOrder.setOrderItems(orderItems);
		
		//Clear the cart after order is placed
		cart.getCartItems().clear();
		cart.setTotalAmount(0.0);
		cart.setTotalItems(0);
		cartRepo.save(cart);
		
		return mapToOrderResponseDTO(savedOrder);		
	}

	@Override
	public OrderResponseDTO getOrderById(Long orderId) {
		Optional<Order> opt = orderRepo.findByIdWithItems(orderId);
		if(opt.isPresent()) {
			return mapToOrderResponseDTO(opt.get());
		}else {
			throw new ResourceNotFoundException("Order","orderId",orderId);
		}
	}

	@Override
	public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
		Optional<Order> opt = orderRepo.findByOrderNumberWithItems(orderNumber);
		if(opt.isPresent()) {
			return mapToOrderResponseDTO(opt.get());
		}else {
			throw new ResourceNotFoundException("Order","orderNumber",orderNumber);
		}
	}

	@Override
	public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
		List<Order> orders = orderRepo.findByUserIdOrderByOrderDateDesc(userId);
		List<OrderResponseDTO> responseList = new ArrayList<>();
		for(Order order : orders) {
			responseList.add(mapToOrderResponseDTO(order));
		}
		return responseList;
	}

	@Override
	public PageResponseDTO<OrderResponseDTO> getAllOrdersPaginated(int page, int size, String sortBy, String sortDir) {
		Sort sort ;
		if(sortDir.equalsIgnoreCase("desc")) {
			sort = Sort.by(sortBy).descending();
		}else {
			sort = Sort.by(sortBy).ascending();
		}
		Pageable pageable = PageRequest.of(page,size,sort);
		Page<Order> orderPage = orderRepo.findAll(pageable);
		return mapToPageResponse(orderPage);
	}

	@Override
	public List<OrderResponseDTO> getOrdersByStatus(String status) {
		List<Order> orders = orderRepo.findByStatus(status);
		List<OrderResponseDTO> responseList = new ArrayList<>();
		for(Order order : orders) {
			responseList.add(mapToOrderResponseDTO(order));
		}
		return responseList;
	}

	@Override
	public OrderResponseDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
		Order order = findOrderById(orderId);
		validateStatusTransition(order.getStatus(), updateOrderStatusRequestDTO.getOrderStatus());
		order.setStatus(updateOrderStatusRequestDTO.getOrderStatus());
		if(Order.STATUS_CANCELLED.equals(updateOrderStatusRequestDTO.getOrderStatus())) {
			restoreStock(order);
		}
		if(updateOrderStatusRequestDTO.getNote() != null && !updateOrderStatusRequestDTO.getNote().isEmpty()) {
			String existingNote = order.getNotes() !=null ?order.getNotes() + "\n" : "";
			order.setNotes(existingNote+"["+LocalDateTime.now()+"] "+updateOrderStatusRequestDTO.getNote());
		}
		Order updatedOrder = orderRepo.save(order);
		return mapToOrderResponseDTO(updatedOrder);
	}

	@Override
	public OrderResponseDTO cancelOrder(Long orderId, String reason) {
		Order order = findOrderById(orderId);
		if(!Order.STATUS_CONFIRMED.equals(order.getStatus())) {
			throw new InvalidOperationException("Cannot cancel order with status : "+order.getStatus());
		}
		order.setStatus(Order.STATUS_CANCELLED);
		String notes = order.getNotes()!=null?order.getNotes()+"\n":"";
		order.setNotes(notes+"["+LocalDateTime.now()+"] Cancelled : "+reason);
		restoreStock(order);
		Order cancelledOrder =orderRepo.save(order);
		return mapToOrderResponseDTO(cancelledOrder);
	}

	@Override
	public PageResponseDTO<OrderResponseDTO> searchOrders(String keyword, int page, int size) {
		Pageable pageable=PageRequest.of(page, size);
		Page<Order> orderPage = orderRepo.searchOrders(keyword, pageable);
		return mapToPageResponse(orderPage);
	}

}
