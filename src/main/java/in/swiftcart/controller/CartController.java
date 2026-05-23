package in.swiftcart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.swiftcart.dto.request.AddToCartRequestDTO;
import in.swiftcart.dto.request.UpdateCartItemRequestDTO;
import in.swiftcart.dto.response.ApiResponseDTO;
import in.swiftcart.dto.response.CartItemResponseDTO;
import in.swiftcart.dto.response.CartResponseDTO;
import in.swiftcart.service.CartService;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/cart")
public class CartController {
	
	private CartService cartService;

	@Autowired
	public CartController(CartService cartService) {
		this.cartService = cartService;
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> getCart(@PathVariable Long userId){
		CartResponseDTO dto = cartService.getCartByUserId(userId);
		return ResponseEntity.ok(ApiResponseDTO.success(dto));
	}
	
	@PostMapping("/{userId}/items")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> addItemToCart(@PathVariable Long userId,@Valid @RequestBody AddToCartRequestDTO dto){
		CartResponseDTO cartDto = cartService.addItemToCart(userId, dto);
		return ResponseEntity.ok(ApiResponseDTO.success("Item Added successfully",cartDto));
	}
	
	@PutMapping("/{userId}/items/{cartItemId}")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> updateCartItem(@PathVariable Long userId,@PathVariable Long cartItemId,@Valid @RequestBody UpdateCartItemRequestDTO dto){
		CartResponseDTO cartDto = cartService.updateCartItem(userId, cartItemId, dto);
		return ResponseEntity.ok(ApiResponseDTO.success("Item updated successfully",cartDto));
	}
	
	@DeleteMapping("/{userId}/items/{cartItemId}")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> removeItemFromCart(@PathVariable Long userId,@PathVariable Long cartItemId){
		CartResponseDTO cartDto = cartService.removeItemFromCart(userId, cartItemId);
		return ResponseEntity.ok(ApiResponseDTO.success("Item removed successfully",cartDto));
	}
	
	@DeleteMapping("/{userId}/clear")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> clearCart(@PathVariable Long userId){
		CartResponseDTO cartDto = cartService.clearCart(userId);
		return ResponseEntity.ok(ApiResponseDTO.success("Cart cleared successfully",cartDto));
	}
	
	@GetMapping("/{userId}/items/{cartItemId}")
	public ResponseEntity<ApiResponseDTO<CartItemResponseDTO>> getCartItem(@PathVariable Long userId,@PathVariable Long cartItemId){
		CartItemResponseDTO dto = cartService.getCartItem(userId, cartItemId);
		return ResponseEntity.ok(ApiResponseDTO.success(dto));
	}
	
	@GetMapping("/{userId}/check-product/{productId}")
	public ResponseEntity<ApiResponseDTO<Boolean>> isProductInCart(@PathVariable Long userId,@PathVariable Long productId){
		boolean inCart = cartService.isProductInCart(userId, productId);
		return ResponseEntity.ok(ApiResponseDTO.success(inCart?"Product is in the Cart":"Product is not in the Cart",inCart));
	}
	
	@PatchMapping("/{userId}/products/{productId}/increment")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> incrementItemQuantity(@PathVariable Long userId,@PathVariable Long productId,@RequestParam(defaultValue="1") int quantity){
		CartResponseDTO cartDto = cartService.incrementItemQuantity(userId, productId, quantity);
		return ResponseEntity.ok(ApiResponseDTO.success("Quantity incremented successfully",cartDto));
	}
	
	@PatchMapping("/{userId}/products/{productId}/decrement")
	public ResponseEntity<ApiResponseDTO<CartResponseDTO>> decrementItemQuantity(@PathVariable Long userId,@PathVariable Long productId,@RequestParam(defaultValue="1") int quantity){
		CartResponseDTO cartDto = cartService.decrementItemQuantity(userId, productId, quantity);
		return ResponseEntity.ok(ApiResponseDTO.success("Quantity decremented successfully",cartDto));
	}

}
