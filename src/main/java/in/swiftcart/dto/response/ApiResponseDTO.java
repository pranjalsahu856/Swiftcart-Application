package in.swiftcart.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponseDTO<T> {

	private boolean success;
	private String message;
	private LocalDateTime timeStamp;
	private T data;
	
	public static <T> ApiResponseDTO<T> success(String message , T data){
		return ApiResponseDTO.<T>builder()
				.success(true)
				.message(message)
				.timeStamp(LocalDateTime.now())
				.data(data)
				.build();
	}
	
	public static <T> ApiResponseDTO<T> success(String message){
		return ApiResponseDTO.<T>builder()
				.success(true)
				.message(message)
				.timeStamp(LocalDateTime.now())
				.build();
	}
	
	public static <T> ApiResponseDTO<T> success(T data){
		return ApiResponseDTO.<T>builder()
				.success(true)
				.timeStamp(LocalDateTime.now())
				.data(data)
				.build();
	}
}
