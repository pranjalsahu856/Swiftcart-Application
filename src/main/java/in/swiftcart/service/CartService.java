package in.swiftcart.service;

import in.swiftcart.dto.request.AddToCartRequestDTO;
import in.swiftcart.dto.request.UpdateCartItemRequestDTO;
import in.swiftcart.dto.response.CartItemResponseDTO;
import in.swiftcart.dto.response.CartResponseDTO;

public interface CartService {

	CartResponseDTO getCartByUserId(Long userId);
	CartResponseDTO addItemToCart(Long userId, AddToCartRequestDTO dto);
	CartResponseDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequestDTO dto);
	CartResponseDTO removeItemFromCart(Long userId, Long cartItemId);
	CartResponseDTO clearCart(Long userId);
	CartItemResponseDTO getCartItem(Long userId, Long cartItemId);
	boolean isProductInCart(Long userId, Long productId);
	CartResponseDTO incrementItemQuantity(Long userId, Long productId, int quantity);
	CartResponseDTO decrementItemQuantity(Long userId, Long productId, int quantity);
	
}
