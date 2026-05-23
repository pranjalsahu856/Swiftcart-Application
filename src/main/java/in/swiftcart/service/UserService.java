package in.swiftcart.service;

import java.util.List;

import in.swiftcart.dto.request.UpdateUserRequestDTO;
import in.swiftcart.dto.request.UserRequestDTO;
import in.swiftcart.dto.response.UserResponseDTO;

public interface UserService {

	UserResponseDTO createUser(UserRequestDTO userRequest);

	UserResponseDTO getUserById(Long id);

	UserResponseDTO getUserByEmail(String email);

	List<UserResponseDTO> getAllUsers();

	List<UserResponseDTO> getActiveUsers();

	UserResponseDTO updateUser(Long id, UpdateUserRequestDTO userRequest);

	void activateUser(Long id);

	void deActivateUser(Long id);

	List<UserResponseDTO> searchUser(String keyword);

	boolean existsByEmail(String email);

}
