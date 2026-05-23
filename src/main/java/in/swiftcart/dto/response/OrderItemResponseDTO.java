package in.swiftcart.dto.response;

import in.swiftcart.dto.request.PlaceOrderRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponseDTO {
	
	private Long id;
	private Long productId;
	private String productName;
	private String productSku;
	private String productImage;
	private Integer quantity;
	private double unitPrice;
	private double subTotal;
	
}
