package in.swiftcart.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.swiftcart.dto.request.UpdateUserRequestDTO;
import in.swiftcart.dto.request.UserRequestDTO;
import in.swiftcart.dto.response.ApiResponseDTO;
import in.swiftcart.dto.response.UserResponseDTO;
import in.swiftcart.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
	
	private UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponseDTO<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO userRequest){
		UserResponseDTO dto = userService.createUser(userRequest);
		ApiResponseDTO<UserResponseDTO> obj = ApiResponseDTO.success("User Created Successfully",dto);
		return new ResponseEntity(obj,HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id){
		UserResponseDTO dto = userService.getUserById(id);
		ApiResponseDTO<UserResponseDTO> obj = ApiResponseDTO.success(dto);
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping("/email/{email}")
	public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserByEmail(@PathVariable String email){
		UserResponseDTO dto = userService.getUserByEmail(email);
		ApiResponseDTO<UserResponseDTO> obj = ApiResponseDTO.success(dto);
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping
	public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers(){
		List<UserResponseDTO> dtoList = userService.getAllUsers();
		ApiResponseDTO<List<UserResponseDTO>> obj = ApiResponseDTO.success(dtoList);
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping("/active")
	public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getActiveUsers(){
		List<UserResponseDTO> dtoList = userService.getActiveUsers();
		ApiResponseDTO<List<UserResponseDTO>> obj = ApiResponseDTO.success(dtoList);
		return ResponseEntity.ok(obj);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequestDTO userRequest){
		UserResponseDTO dto = userService.updateUser(id,userRequest);
		ApiResponseDTO<UserResponseDTO> obj = ApiResponseDTO.success("User Updated Successfully",dto);
		return ResponseEntity.ok(obj);
	}
	
	@PatchMapping("/{id}/activate")
	public ResponseEntity<ApiResponseDTO<Void>> activateUser(@PathVariable Long id){
		userService.activateUser(id);
		return ResponseEntity.ok(ApiResponseDTO.success("User Activated Successfully"));
	}
	
	@PatchMapping("/{id}/deactivate")
	public ResponseEntity<ApiResponseDTO<Void>> deActivateUser(@PathVariable Long id){
		userService.deActivateUser(id);
		return ResponseEntity.ok(ApiResponseDTO.success("User De-Activated Successfully"));
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> searchUsers(@RequestParam String keyword){
		List<UserResponseDTO> dtoList = userService.searchUser(keyword);
		ApiResponseDTO<List<UserResponseDTO>> obj = ApiResponseDTO.success(dtoList);
		return ResponseEntity.ok(obj);
	}
	
	@GetMapping("/check-email")
	public ResponseEntity<ApiResponseDTO<Boolean>> checkEmailExists(@RequestParam String email){
		Boolean res = userService.existsByEmail(email);
		ApiResponseDTO<Boolean> obj = ApiResponseDTO.success(res?"Email already exists":"Email is available for use");
		return ResponseEntity.ok(obj);
	}
	
}
