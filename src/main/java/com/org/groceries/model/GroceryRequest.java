package com.org.groceries.model;

import com.org.groceries.entity.GroceryList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class GroceryRequest
{
	private String    productName;
	private String    productQuantity;
	private LocalDate orderDate;
	private String userId;

	public GroceryList toGroceryList()
	{
		GroceryList groceryList = new GroceryList ();
		groceryList.setProductName (this.productName);
		groceryList.setProductQuantity (this.productQuantity);
		groceryList.setOrderDate (this.orderDate);
		groceryList.setUserId (this.userId);

		return groceryList;
	}
}
