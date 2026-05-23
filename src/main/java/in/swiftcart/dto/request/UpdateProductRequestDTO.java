package in.swiftcart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProductRequestDTO {

	@Size(min = 2, max = 200, message = "Name must be between 2-200 Characters")
	private String name;

	@Size(max = 1000, message = "Description must no exceed 1000 Characters")
	private String description;

	@Min(value = 0, message = "Price must be greater than or equal to 0")
	private Double price;

	@Min(value = 0, message = "Quantity must be greater than or equal to 0")
	private Integer stockQuantity;

	@Size(max = 100, message = "Category must no exceed 100 Characters")
	private String category;

	@Size(max = 100, message = "Brand must no exceed 100 Characters")
	private String brand;

	@Size(max = 500, message = "ImageUrl must no exceed 500 Characters")
	private String imageUrl;

	@Size(max = 50, message = "Sku must no exceed 50 Characters")
	private String sku;

	private Boolean isAvailable;

}
