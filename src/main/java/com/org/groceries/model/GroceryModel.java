package com.org.groceries.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
@Setter
public class GroceryModel
{
	private Long      id;
	private String    productName;
	private String    productQuantity;
	private LocalDate orderDate;
	private String    userId;
}
