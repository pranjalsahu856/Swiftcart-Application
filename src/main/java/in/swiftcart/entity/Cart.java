package in.swiftcart.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;
	
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,orphanRemoval = true)
	@Builder.Default
	private List<CartItem> cartItems = new ArrayList<>();
	
	@Column
	@Builder.Default
	private double totalAmount = 0.0;
	
	@Column(nullable = false)
	@Builder.Default
	private Integer totalItems = 0;
	
	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
	@UpdateTimestamp
	private LocalDateTime updatedAt;

	public void addCartItem(CartItem item) {
		cartItems.add(item);
		item.setCart(this);
		recalculateTotals();
	}
	
	public void removeCartItem(CartItem item) {
		cartItems.remove(item);
		item.setCart(null);
		recalculateTotals();
	}
	
	public void recalculateTotals() {
		//For Total Items
		int totalItemCount = 0;
		for(CartItem item : cartItems) {
			totalItemCount+=item.getQuantity();
		}
		this.totalItems=totalItemCount;
		
		//For Total Amount
		double total=0.0;
		for(CartItem item : cartItems) {
			total+=item.getSubTotal();
		}
		this.totalAmount=total;		
	}
	
	public void clearCart() {
		for(CartItem item : new ArrayList<>(cartItems)) {
			item.setCart(null);
		}
		cartItems.clear();
		this.totalAmount=0.0;
		this.totalItems=0;
	}
}


