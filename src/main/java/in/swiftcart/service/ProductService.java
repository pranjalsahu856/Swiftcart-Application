package in.swiftcart.service;

import java.util.List;

import in.swiftcart.dto.request.ProductRequestDTO;
import in.swiftcart.dto.request.UpdateProductRequestDTO;
import in.swiftcart.dto.response.PageResponseDTO;
import in.swiftcart.dto.response.ProductResponseDTO;

public interface ProductService {
	
	ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

	ProductResponseDTO getProductById(Long id);

	ProductResponseDTO getProductBySku(String sku);

	List<ProductResponseDTO> getAllProducts();
	
	PageResponseDTO<ProductResponseDTO> getAllProductsPaginated(int page , int size, String sortBy,String sortDir);
	
	List<ProductResponseDTO> getAvailableProducts();

	List<ProductResponseDTO> getProductsByCategory(String category);

	List<ProductResponseDTO> getProductsByPriceRange(double minPrice, double maxPrice);

	PageResponseDTO<ProductResponseDTO> searchProducts(String keyword, int page, int size );

	ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO productRequestDTO);

	void updateStock(Long productId, Integer quantity);

	List<ProductResponseDTO> getLowStockProducts(Integer threshold);

	boolean existsBySku(String sku);
	

}
