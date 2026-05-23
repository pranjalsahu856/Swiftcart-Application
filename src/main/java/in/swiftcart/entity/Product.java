package in.swiftcart.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false, length = 200)
	private String name;
	
	@Column(length = 500)
	private String description;
	
	@Column(nullable = false)
	private double price;
	
	@Column(nullable = false)
	@Builder.Default
	private Integer stockQuantity = 0;
	
	@Column(length = 100)
	private String category;
	
	@Column(length = 100)
	private String brand;
	
	@Column(length = 500)
	private String imageURL;
	
	@Column(unique = true, length = 50)
	private String sku;
	
	@Column(nullable = false)
	@Builder.Default
	private Boolean isAvailable = true;
	
}
