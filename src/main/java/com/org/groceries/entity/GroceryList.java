package com.org.groceries.entity;

import com.org.groceries.model.GroceryModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Table(name = "grocery_list", uniqueConstraints={@UniqueConstraint(name="uk_prodname", columnNames={"product_name"})})
@Entity
@Data
@NoArgsConstructor
public class GroceryList implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "product_name", nullable = false)
	private String productName;

	@Column(name = "product_quantity", nullable = false)
	private String productQuantity;

	@Column(name = "order_date", nullable = false)
	private LocalDate orderDate;

	@Column(name = "user_id", nullable = false)
	private String userId;

	public GroceryModel toGroceryModel()
	{
		return new GroceryModel (this.getId (), this.getProductName (),
				this.getProductQuantity (), this.getOrderDate (), this.getUserId ());
	}
}
