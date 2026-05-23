package in.swiftcart.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class UpdateCartItemRequestDTO {
	
	@NotNull(message="Quantity is required")
	@Min(value=1,message="Minimum quantity must be 1")
	@Max(value=100,message="Maximum quantity can be 100")
	private Integer quantity;
	
}
