package in.swiftcart.dto.response;

import java.time.LocalDateTime;

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
public class CartItemResponseDTO {
	
	private Long id;
	private Long productId;
	private String productName;
	private String productImage;
	private String productSku;
	private double unitPrice;
	private Integer quantity;
	private double subTotal;
	private Integer availableStock;
	private LocalDateTime addedAt;
	

}
