package in.swiftcart.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class OrderResponseDTO {

	private Long id;
	private String orderNumber;
	private Long userId;
	private String userName;
	private String userEmail;
	private List<OrderItemResponseDTO> items;
	private Integer totalItems;
	private double totalAmount;
	private String status;
	private String note;
	private LocalDateTime orderAt;

}
