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

import in.swiftcart.dto.request.ProductRequestDTO;
import in.swiftcart.dto.request.UpdateProductRequestDTO;
import in.swiftcart.dto.response.ApiResponseDTO;
import in.swiftcart.dto.response.PageResponseDTO;
import in.swiftcart.dto.response.ProductResponseDTO;
import in.swiftcart.service.ProductService;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/products")
public class ProductController {

	private ProductService productServ;

	@Autowired
	public ProductController(ProductService productServ) {
		this.productServ = productServ;
	}
	
	@PostMapping
	public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> createProduct(@Valid @RequestBody ProductRequestDTO requestDto){
		ProductResponseDTO responseDTO = productServ.createProduct(requestDto);
		return new ResponseEntity(ApiResponseDTO.success("Product created successfully",responseDTO),HttpStatus.CREATED);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductById(@PathVariable Long id){
		ProductResponseDTO responseDTO = productServ.getProductById(id);
		return ResponseEntity.ok(ApiResponseDTO.success(responseDTO));
	}
	
	@GetMapping("/sku/{sku}")
	public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductBySku(@PathVariable String sku){
		ProductResponseDTO responseDTO = productServ.getProductBySku(sku);
		return ResponseEntity.ok(ApiResponseDTO.success(responseDTO));
	}

	@GetMapping("/getAll")
	public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAllProducts(){
		List<ProductResponseDTO> productList = productServ.getAllProducts();
		return ResponseEntity.ok(ApiResponseDTO.success("Fetched "+productList.size()+" products",productList));
	}
	
	@GetMapping
	public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>> getAllProductsPaginated(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, 
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "asc") String sortDir) {
		PageResponseDTO<ProductResponseDTO> productsList = productServ.getAllProductsPaginated(page, size, sortBy, sortDir);
		return ResponseEntity.ok(ApiResponseDTO.success(productsList));
	}
	
	@GetMapping("/available")
	public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAvailableProducts() {
		List<ProductResponseDTO> productList = productServ.getAvailableProducts();
		return ResponseEntity.ok(ApiResponseDTO.success(productList));
	}
	
	@GetMapping("/category/{category}")
	public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getProductsByCategory(@PathVariable String category) {
		List<ProductResponseDTO> productList = productServ.getProductsByCategory(category);
		return ResponseEntity.ok(ApiResponseDTO.success(productList));
	}
	
	@GetMapping("/price-range")
	public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getProductsByPriceRange(@RequestParam double min, @RequestParam double max){
		List<ProductResponseDTO> productList = productServ.getProductsByPriceRange(min, max);
		return ResponseEntity.ok(ApiResponseDTO.success(productList));
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>> searchProducts(@RequestParam String keyword, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		PageResponseDTO<ProductResponseDTO> productsList = productServ.searchProducts(keyword, page, size);
		return ResponseEntity.ok(ApiResponseDTO.success(productsList));
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> updateProduct(@PathVariable Long id,@Valid @RequestBody UpdateProductRequestDTO requestDTO){
		ProductResponseDTO updatedProduct = productServ.updateProduct(id, requestDTO);
		return ResponseEntity.ok(ApiResponseDTO.success("Product Updated Successfully",updatedProduct));
	}
	
	@PatchMapping("/{id}/stock")
	public ResponseEntity<ApiResponseDTO<Void>> updateStock(@PathVariable Long id,@RequestParam Integer quantity){
		productServ.updateStock(id, quantity);
		return ResponseEntity.ok(ApiResponseDTO.success("Stock Updated Successfully"));
	}
	
	@GetMapping("/low-stock")
	public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getLowStockProducts(@RequestParam(defaultValue = "10") Integer threshold){
		List<ProductResponseDTO> productList = productServ.getLowStockProducts(threshold);
		return ResponseEntity.ok(ApiResponseDTO.success(productList));
	}
	
	@GetMapping("/check-sku/{sku}")
	public ResponseEntity<ApiResponseDTO<Boolean>> checkSkuExists(@PathVariable String sku){
		boolean exists = productServ.existsBySku(sku);
		return ResponseEntity.ok(ApiResponseDTO.success(exists?"SKU already exists":"SKU is available"));
	}
	
	
}
