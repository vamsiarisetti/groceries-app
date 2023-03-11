package com.org.groceries.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GroceryResponse
{
	private List<GroceryModel> data;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String             message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String             errorMessage;
}
