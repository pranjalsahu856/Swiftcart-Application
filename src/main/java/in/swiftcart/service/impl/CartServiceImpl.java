package in.swiftcart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.swiftcart.dto.request.AddToCartRequestDTO;
import in.swiftcart.dto.request.UpdateCartItemRequestDTO;
import in.swiftcart.dto.response.CartItemResponseDTO;
import in.swiftcart.dto.response.CartResponseDTO;
import in.swiftcart.entity.Cart;
import in.swiftcart.entity.CartItem;
import in.swiftcart.entity.Product;
import in.swiftcart.exception.InsufficientStockException;
import in.swiftcart.exception.ResourceNotFoundException;
import in.swiftcart.repository.CartItemRepository;
import in.swiftcart.repository.CartRepository;
import in.swiftcart.repository.ProductRepository;
import in.swiftcart.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService {
	
	private CartRepository cartRepo;
	private CartItemRepository cartItemRepo;
	private ProductRepository prodRepo;
	
	@Autowired
	public CartServiceImpl(CartRepository cartRepo, CartItemRepository cartItemRepo, ProductRepository prodRepo) {
		this.cartRepo = cartRepo;
		this.cartItemRepo = cartItemRepo;
		this.prodRepo = prodRepo;
	}
	
	private Cart getCart(Long userId) {
		Optional<Cart> opt = cartRepo.findByUserId(userId);
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new ResourceNotFoundException("Cart", "userId", userId);
	}
	
	private Product findProductById(Long productId) {
		Optional<Product> opt = prodRepo.findById(productId);
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new ResourceNotFoundException("Product", "productId", productId);
	}
	
	private CartItem findCartItemById(Long cartItemId) {
		Optional<CartItem> opt = cartItemRepo.findById(cartItemId);
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new ResourceNotFoundException("CartItem", "cartItemId", cartItemId);
	}

	private void validateProductAvailability(Product product, int requestQty) {
		if(!product.getIsAvailable()) {
			throw new InsufficientStockException(product.getName()+" is not Available"); 
		}
		if(product.getStockQuantity()<requestQty) {
			throw new InsufficientStockException("Insufficient stock for "+product.getName()+
					". Available :"+product.getStockQuantity()+
					", Requested :"+requestQty);
		}
	}
	
	private CartResponseDTO mapToCartResponseDTO(Cart cart) {
		List<CartItemResponseDTO> items = new ArrayList<>();
		for(CartItem item : cart.getCartItems()) {
			items.add(mapToCartItemResponseDTO(item));
		}
		return CartResponseDTO.builder()
					.id(cart.getId())
					.userName(cart.getUser().getFullName())
					.userId(cart.getUser().getId())
					.items(items)
					.totalItems(cart.getTotalItems())
					.totalAmount(cart.getTotalAmount())
					.createdAt(cart.getCreatedAt())
					.updatedAt(cart.getUpdatedAt())
					.build();
	}

	private CartItemResponseDTO mapToCartItemResponseDTO(CartItem item) {
		return CartItemResponseDTO.builder()
				.id(item.getId())
				.productId(item.getProduct().getId())
				.productName(item.getProduct().getName())
				.productImage(item.getProduct().getImageURL())
				.productSku(item.getProduct().getSku())
				.unitPrice(item.getProduct().getPrice())
				.quantity(item.getQuantity())
				.subTotal(item.getSubTotal())
				.availableStock(item.getProduct().getStockQuantity())
				.addedAt(item.getAddedAt())
				.build();
	}

	@Override
	public CartResponseDTO getCartByUserId(Long userId) {
		Cart cart = getCart(userId);
		return mapToCartResponseDTO(cart);
	}

	@Override
	public CartResponseDTO addItemToCart(Long userId, AddToCartRequestDTO dto) {
		//Get Cart
		Cart cart = getCart(userId);
		//Fetch the Product
		Product product = findProductById(dto.getProductId());
		//Validate the Qty
		validateProductAvailability(product, dto.getQuantity());
		
		//checking Item is present or not in Cart
		Optional<CartItem> opt = cartItemRepo.findByCartIdAndProductId(cart.getId(), product.getId());
		if(opt.isPresent()) {
			CartItem cartItem = opt.get();
			int newQty = cartItem.getQuantity()+dto.getQuantity();
			validateProductAvailability(product, newQty);
			cartItem.setQuantity(newQty);
			cartItem.calculateSubTotal();
			cartItemRepo.save(cartItem);
		}else {
			CartItem cartItem = CartItem.builder()
									.product(product)
									.quantity(dto.getQuantity())
									.unitPrice(product.getPrice())
									.build();
			cartItem.calculateSubTotal();
			cart.addCartItem(cartItem);
		}
		cartRepo.save(cart);		
		return mapToCartResponseDTO(cart);
	}

	@Override
	public CartResponseDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequestDTO dto) {
		Cart cart =getCart(userId);
		CartItem cartItem = findCartItemById(cartItemId);
		if(!cartItem.getCart().getId().equals(cart.getId())) {
			throw new ResourceNotFoundException("CartItem", "id", cartItem);
		}
		validateProductAvailability(cartItem.getProduct(), dto.getQuantity());
		cartItem.setQuantity(dto.getQuantity());
		cartItem.calculateSubTotal();
		cart.recalculateTotals();
		cartRepo.save(cart);
		return mapToCartResponseDTO(cart);
	}

	@Override
	public CartResponseDTO removeItemFromCart(Long userId, Long cartItemId) {
		Cart cart =getCart(userId);
		CartItem cartItem = findCartItemById(cartItemId);
		if(!cartItem.getCart().getId().equals(cart.getId())) {
			throw new ResourceNotFoundException("CartItem", "id", cartItem);
		}
		cart.removeCartItem(cartItem);
		cartRepo.save(cart);
		return mapToCartResponseDTO(cart);
	}

	@Override
	public CartResponseDTO clearCart(Long userId) {
		Cart cart = getCart(userId);
		cart.clearCart();
		cartRepo.save(cart);
		return mapToCartResponseDTO(cart);
	}

	@Override
	public CartItemResponseDTO getCartItem(Long userId, Long cartItemId) {
		Cart cart = getCart(userId);
		CartItem cartItem = findCartItemById(cartItemId);
		if(!cartItem.getCart().getId().equals(cart.getId())) {
			throw new ResourceNotFoundException("CartItem", "id", cartItem);
		}
		return mapToCartItemResponseDTO(cartItem);		
	}

	@Override
	public boolean isProductInCart(Long userId, Long productId) {
		Optional<Cart> opt = cartRepo.findByUserId(userId);
		if(!opt.isPresent()) {
			return false;
		}
		Cart cart = opt.get();
		return cartItemRepo.existsByCartIdAndProductId(cart.getId(), productId);
	}

	@Override
	public CartResponseDTO incrementItemQuantity(Long userId, Long productId, int quantity) {
		if(quantity<=0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		Cart cart = getCart(userId);
		Optional<CartItem> opt = cartItemRepo.findByCartIdAndProductId(cart.getId(), productId);
		if(!opt.isPresent()) {
			throw new ResourceNotFoundException("CartItem", "productId", productId);
		}
		CartItem cartItem = opt.get();
		int newQty = cartItem.getQuantity()+quantity;
		validateProductAvailability(cartItem.getProduct(), newQty);
		cartItem.setQuantity(newQty);
		cartItem.calculateSubTotal();
		cart.recalculateTotals();
		cartRepo.save(cart);
		return mapToCartResponseDTO(cart);
	}

	@Override
	public CartResponseDTO decrementItemQuantity(Long userId, Long productId, int quantity) {
		if(quantity<=0) {
			throw new IllegalArgumentException("Quantity must be positive");
		}
		Cart cart = getCart(userId);
		Optional<CartItem> opt = cartItemRepo.findByCartIdAndProductId(cart.getId(), productId);
		if(!opt.isPresent()) {
			throw new ResourceNotFoundException("CartItem", "productId", productId);
		}
		CartItem cartItem = opt.get();
		int newQty = cartItem.getQuantity()-quantity;
		if(newQty<=0) {
			cart.removeCartItem(cartItem);
		}else {
			cartItem.setQuantity(newQty);
			cartItem.calculateSubTotal();
		}
		cart.recalculateTotals();
		cartRepo.save(cart);
		return mapToCartResponseDTO(cart);
	}

}
