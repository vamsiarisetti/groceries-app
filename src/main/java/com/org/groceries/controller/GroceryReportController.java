package com.org.groceries.controller;

import com.itextpdf.text.DocumentException;
import com.org.groceries.model.GroceryResponse;
import com.org.groceries.service.GroceryService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

@RestController
public class GroceryReportController
{
	@Autowired
	GroceryService groceryService;

	@GetMapping(value = "/sendGroceryList/{userId}")
	@ApiOperation(value = "Send Grocery List")
	public void sendGroceryList(@PathVariable String userId) throws DocumentException, IOException, URISyntaxException
	{
//		GroceryResponse groceryResponse = groceryService.fetchAllGroceries ();
		GroceryResponse groceryResponse = groceryService.fetchGroceriesForUser (userId);
		groceryService.prepareGroceryListReport (groceryResponse, userId);
	}

	@GetMapping(value = "/download/{fileName}", produces = MediaType.APPLICATION_PDF_VALUE)
	@ApiOperation(value = "download file")
	public ResponseEntity<byte[]> downloadFroceryList(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(name = "fileName") String fileName) throws IOException
	{

		HttpHeaders headers = new HttpHeaders();

		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		//		String filename = "pdf1.pdf";
		String filename = "grocery_lists/" + fileName;
		File   file     = new File (filename);
		byte[] bytes    = FileUtils.readFileToByteArray (file);

//		headers.add("content-disposition", "inline;filename=" + filename);

		headers.setContentDispositionFormData(filename, filename);
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		return new ResponseEntity<byte[]>(bytes, headers, HttpStatus.OK);
	}

	@GetMapping(value = "/prepareAndSendGroceryList")
	@ApiOperation (value = "prepares grocery list for a user and send to respective user")
	public void prepareAndSendGroceryListToUser() throws TelegramApiException, FileNotFoundException
	{
		groceryService.sendGroceryList();
	}
}
