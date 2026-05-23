package in.swiftcart.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

	@NotBlank(message = "FullName is Required")
	@Size(min = 2, max = 100, message = "FullName must be between 2-100 Characters")
	private String fullName;

	@NotBlank(message = "Email is Required")
	@Email(message = "Please provide a valid Email address")
	@Size(max = 150, message = "Email must not Exceed 150 Characters")
	private String email;

	@NotBlank(message = "Password is Required")
	@Size(min = 6, max = 100, message = "Password must be between 6-100 Characters")
	private String password;

	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must of exact 10 digits only")
	private String phone;

	@Size(max = 255, message = "Address must not Exceed 255 Characters")
	private String address;
}
