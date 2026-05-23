package in.swiftcart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.swiftcart.dto.request.ProductRequestDTO;
import in.swiftcart.dto.request.UpdateProductRequestDTO;
import in.swiftcart.dto.response.PageResponseDTO;
import in.swiftcart.dto.response.ProductResponseDTO;
import in.swiftcart.entity.Product;
import in.swiftcart.exception.DuplicateResourceException;
import in.swiftcart.exception.GlobalExceptionHandler;
import in.swiftcart.exception.ResourceNotFoundException;
import in.swiftcart.repository.ProductRepository;
import in.swiftcart.service.ProductService;

@Service
@Transactional
public class ProductServiceImpl implements ProductService{

    private final GlobalExceptionHandler globalExceptionHandler;

	private ProductRepository productRepo;
	
	@Autowired
	public ProductServiceImpl(ProductRepository productRepo, GlobalExceptionHandler globalExceptionHandler) {
		this.productRepo = productRepo;
		this.globalExceptionHandler = globalExceptionHandler;
	}
	
	private Product findProductById(Long id) {
		Optional<Product> opt = productRepo.findById(id);
		if(opt.isPresent()) {
			return opt.get();
		}
		throw new ResourceNotFoundException("Product","id",id);
	}
	
	private Pageable createPageable(int page,int size,String sortBy,String sortDir) {
		Sort sort ;
		if(sortDir.equalsIgnoreCase("desc"))
			sort = Sort.by(sortBy).descending();
		else 
			sort = Sort.by(sortBy).ascending();
		return PageRequest.of(page, size, sort);
	}
	
	private ProductResponseDTO mapToProductResponseDTO(Product product) {
		return ProductResponseDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.stockQuantity(product.getStockQuantity())
				.category(product.getCategory())
				.brand(product.getBrand())
				.imageUrl(product.getImageURL())
				.sku(product.getSku())
				.isAvailable(product.getIsAvailable())
				.inStock(product.getStockQuantity()>0)
				.build();
	}
	
	private PageResponseDTO<ProductResponseDTO> mapToPageResponse(Page<Product> productPage) {
		List<ProductResponseDTO> products = new ArrayList<>();
		for(Product product : productPage.getContent()) {
			products.add(mapToProductResponseDTO(product));
		}		
		return PageResponseDTO.<ProductResponseDTO>builder()
				.content(products)
				.pageNumber(productPage.getNumber())
				.pageSize(productPage.getSize())
				.totalElements(productPage.getTotalElements())
				.totalPages(productPage.getTotalPages())
				.first(productPage.isFirst())
				.last(productPage.isLast())
				.hasNext(productPage.hasNext())
				.hasPrevious(productPage.hasPrevious())
				.build();
	}
	
	@Override
	public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
		if(productRequestDTO.getSku()!=null && productRepo.existsBySku(productRequestDTO.getSku())) {
			throw new DuplicateResourceException("Product", "sku", productRequestDTO.getSku());
		}
		Product product = Product.builder()
							.name(productRequestDTO.getName())
							.description(productRequestDTO.getDescription())
							.price(productRequestDTO.getPrice())
							.stockQuantity(productRequestDTO.getStockQuantity())
							.category(productRequestDTO.getCategory())
							.brand(productRequestDTO.getBrand())
							.imageURL(productRequestDTO.getImageUrl())
							.sku(productRequestDTO.getSku())
							.isAvailable(productRequestDTO.getIsAvailable()!=null?productRequestDTO.getIsAvailable():true)
							.build();
		Product savedProduct =productRepo.save(product);
		return mapToProductResponseDTO(savedProduct);
	}

	@Override
	public ProductResponseDTO getProductById(Long id) {
		Product product = findProductById(id);
		return mapToProductResponseDTO(product);
	}

	@Override
	public ProductResponseDTO getProductBySku(String sku) {
		Optional<Product> opt = productRepo.findBySku(sku);
		if(opt.isPresent()) {
			return mapToProductResponseDTO(opt.get());
		}
		throw new ResourceNotFoundException("Product", "sku", sku);
	}

	@Override
	public List<ProductResponseDTO> getAllProducts() {
		List<Product> products = productRepo.findAll();
		List<ProductResponseDTO> responseList = new ArrayList<>();
		for(Product product : products) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public PageResponseDTO<ProductResponseDTO> getAllProductsPaginated(int page, int size, String sortBy,
			String sortDir) {
		Pageable pageable = createPageable(page, size, sortBy, sortDir);
		Page<Product> productPage = productRepo.findAll(pageable);
		return mapToPageResponse(productPage);
	}

	@Override
	public List<ProductResponseDTO> getAvailableProducts() {
		List<Product> productList = productRepo.findByIsAvailableTrue();
		List<ProductResponseDTO> responseList = new ArrayList<>();
		for(Product product : productList) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public List<ProductResponseDTO> getProductsByCategory(String category) {
		List<Product> productList = productRepo.findByCategoryIgnoreCase(category);
		List<ProductResponseDTO> responseList = new ArrayList<>();
		for(Product product : productList) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public List<ProductResponseDTO> getProductsByPriceRange(double minPrice, double maxPrice) {
		List<Product> productList = productRepo.findByPriceBetween(minPrice, maxPrice);
		List<ProductResponseDTO> responseList = new ArrayList<>();
		for(Product product : productList) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public PageResponseDTO<ProductResponseDTO> searchProducts(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Product> productPage = productRepo.searchProducts(keyword, pageable);
		return mapToPageResponse(productPage);
	}

	@Override
	public ProductResponseDTO updateProduct(Long id, UpdateProductRequestDTO productRequestDTO) {
		Product product = findProductById(id);
		if(productRequestDTO.getName()==null
				&& productRequestDTO.getDescription()==null
				&& productRequestDTO.getPrice()==null
				&& productRequestDTO.getStockQuantity()==null
				&& productRequestDTO.getCategory()==null
				&& productRequestDTO.getBrand()==null
				&& productRequestDTO.getImageUrl()==null
				&& productRequestDTO.getSku()==null
				&& productRequestDTO.getIsAvailable()==null) {
			throw new IllegalArgumentException("At least one field should be present for updation");
		}
		if(productRequestDTO.getName()!=null) {
			if(productRequestDTO.getName().isBlank()) {
				throw new IllegalArgumentException("Product Name cannot be left Blank");
			}
			product.setName(productRequestDTO.getName().trim());
		}
		if(productRequestDTO.getDescription()!=null) {
			if(productRequestDTO.getDescription().isBlank()) {
				throw new IllegalArgumentException("Product Description cannot be left Blank");
			}
			product.setDescription(productRequestDTO.getDescription().trim());
		}
		if(productRequestDTO.getPrice()!=null) {
			product.setPrice(productRequestDTO.getPrice());
		}
		if(productRequestDTO.getStockQuantity()!=null) {
			product.setStockQuantity(productRequestDTO.getStockQuantity());
		}
		if(productRequestDTO.getCategory()!=null) {
			if(productRequestDTO.getCategory().isBlank()) {
				throw new IllegalArgumentException("Product Category cannot be Negative");
			}
			product.setCategory(productRequestDTO.getCategory().trim());
		}
		if(productRequestDTO.getBrand()!=null) {
			if(productRequestDTO.getBrand().isBlank()) {
				throw new IllegalArgumentException("Product Brand cannot be Negative");
			}
			product.setBrand(productRequestDTO.getBrand().trim());
		}
		if(productRequestDTO.getImageUrl()!=null) {
			if(productRequestDTO.getImageUrl().isBlank()) {
				throw new IllegalArgumentException("Product ImageURL cannot be Negative");
			}
			product.setImageURL(productRequestDTO.getImageUrl().trim());
		}
		if(productRequestDTO.getSku()!=null) {
			if(productRequestDTO.getSku().isBlank()) {
				throw new IllegalArgumentException("Product Sku cannot be Negative");
			}
			String sku =productRequestDTO.getSku();
			if(!sku.equals(product.getSku()) && productRepo.existsBySku(sku)) {
				throw new DuplicateResourceException("Product","sku",sku);
			}
			product.setSku(sku);
		}
		if(productRequestDTO.getIsAvailable()!=null) {
			product.setIsAvailable(productRequestDTO.getIsAvailable());
		}
		Product updatedProduct = productRepo.save(product);
		return mapToProductResponseDTO(updatedProduct);
	}

	@Override
	public void updateStock(Long productId, Integer quantity) {
		Product product = findProductById(productId);
		int newQty = product.getStockQuantity()+quantity;
		if(newQty<0) {
			throw new IllegalArgumentException("Stock quantity cannot be negative, Current Quantity : "+ product.getStockQuantity());
		}
		product.setStockQuantity(newQty);
		productRepo.save(product);
	}

	@Override
	public List<ProductResponseDTO> getLowStockProducts(Integer threshold) {
		if(threshold<0) {
			throw new IllegalArgumentException("Threshold cannot be Negative : "+threshold);
		}
		List<Product> productList = productRepo.findLowStockProducts(threshold);
		List<ProductResponseDTO> responseList = new ArrayList<>();
		for(Product product : productList) {
			responseList.add(mapToProductResponseDTO(product));
		}
		return responseList;
	}

	@Override
	public boolean existsBySku(String sku) {
		
		return productRepo.existsBySku(sku);
	}
	
	

}
