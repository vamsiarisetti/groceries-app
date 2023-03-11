package com.org.groceries.controller;

import com.org.groceries.entity.GroceryList;
import com.org.groceries.model.GroceryRequest;
import com.org.groceries.model.GroceryResponse;
import com.org.groceries.service.GroceryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
public class GroceryController
{
	@Autowired
	GroceryService groceryService;

	@GetMapping(value = "/groceries", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Fetch All Groceries", response = GroceryList.class)
	public GroceryResponse fetchAllGroceries()
	{
		return groceryService.fetchAllGroceries ();
	}

	@PutMapping(value = "/groceries", produces = APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Save Groceries", response = GroceryResponse.class)
	public GroceryResponse insertGrocery(@Validated @RequestBody GroceryRequest groceryRequest)
	{
		return groceryService.saveGroceryList (groceryRequest);
	}
}
