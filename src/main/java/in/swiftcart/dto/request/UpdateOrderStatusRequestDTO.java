package in.swiftcart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UpdateOrderStatusRequestDTO {

	@NotBlank(message="Order status is required")
	private String orderStatus;
	
	@Size(max=500,message="Notes must not exceed 500 Characters")
	private String note;
}
