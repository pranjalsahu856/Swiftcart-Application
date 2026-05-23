package in.swiftcart.dto.request;

import jakarta.validation.constraints.NotNull;
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
public class PlaceOrderRequestDTO {
	
	@NotNull(message="User ID is required")
	private Long userId;
	
	@Size(max=500,message="Notes must not exceed 500 Characters")
	private String note;

}
