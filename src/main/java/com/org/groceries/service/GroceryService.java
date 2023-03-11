package com.org.groceries.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.org.groceries.entity.GroceryList;
import com.org.groceries.model.GroceryModel;
import com.org.groceries.model.GroceryRequest;
import com.org.groceries.model.GroceryResponse;
import com.org.groceries.repository.GroceryRepository;
import com.org.groceries.telegram.TelegramBot;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class GroceryService
{
	@Autowired
	GroceryRepository groceryRepository;

	Map<String, Long> userChannelMap = Map.of (
			"861760458", -1001582642320L,
			"2005698367", -1001858558063L,
			"1663430855", -1001586387382L
	);


	/**
	 * This method is used to insert data into GroceryList Table
	 *
	 * @param groceryRequest
	 * @return GroceryList
	 */
	public GroceryResponse saveGroceryList(GroceryRequest groceryRequest)
	{
		GroceryList groceryList = groceryRepository.save (groceryRequest.toGroceryList ());

		GroceryResponse groceryResponse = new GroceryResponse ();
		groceryResponse.setData (Collections.singletonList (groceryList.toGroceryModel ()));
		return groceryResponse;
	}

	public List<GroceryList> fetchGroceryListWithOrderDate(LocalDate orderDate)
	{
		return groceryRepository.findByOrderDate (orderDate);
	}

	public GroceryList fetchGroceryListByName(String productName)
	{
		return groceryRepository.findByProductName (productName);
	}

	public GroceryResponse fetchAllGroceries()
	{
		List<GroceryList> groceryLists = groceryRepository.findAll ();
		List<GroceryModel> groceryModelList = groceryLists.stream ()
				.map (grocery -> new GroceryModel (grocery.getId (), grocery.getProductName (),
						grocery.getProductQuantity (), grocery.getOrderDate (), grocery.getUserId ()))
				.collect (Collectors.toList ());
		GroceryResponse groceryResponse = new GroceryResponse ();
		groceryResponse.setData (groceryModelList);
		return groceryResponse;
	}

	public GroceryResponse fetchGroceriesForUser(String userId)
	{
		List<GroceryList> groceryLists = groceryRepository.findByUserId (userId);
		List<GroceryModel> groceryModelList = groceryLists.stream ()
				.map (grocery -> new GroceryModel (grocery.getId (), grocery.getProductName (),
						grocery.getProductQuantity (), grocery.getOrderDate (), grocery.getUserId ()))
				.collect (Collectors.toList ());
		GroceryResponse groceryResponse = new GroceryResponse ();
		groceryResponse.setData (groceryModelList);
		return groceryResponse;
	}

	/**
	 * 1. Fetches Distinct userIds
	 * 2. Prepares grocery List for each User
	 * 3. Send grocery list file to user in Telegram
	 *
	 */
	@Scheduled(cron = "0 0/1 * * * *")
//	0 0/1 * * * *
//	0 0 8 1 * ? *
	@SchedulerLock(name = "groceryTask", lockAtMostForString = "PT40S", lockAtLeastForString = "PT20S")
	public void sendGroceryList()
	{
		// Fetch Distinct UserIds
		List<String> userIds = groceryRepository.getDistinctUserIds ();
		List<Object> groceryListFiles = userIds.stream ()
				.filter (userId -> !(userId.equalsIgnoreCase ("common")))
				.map (userId -> {
					// Fetch GroceryList for userId and common
					GroceryResponse groceryResponse = fetchGroceriesForUser (userId);
					String          fileName;
					try
					{
						// Prepare PDF File with grocery List
						fileName = prepareGroceryListReport (groceryResponse, userId);
						// fetch telegram channelId from for user
						Long telegramChannelId = userChannelMap.getOrDefault (userId, 0L);
						// Send Grocery List PDF to user
						sendGroceryListToTelegram (fileName, telegramChannelId);
					} catch (IOException | DocumentException | URISyntaxException | TelegramApiException e)
					{
						throw new RuntimeException (e);
					}
					return fileName;
				}).collect (Collectors.toList ());

		System.out.println ("grocery Lists : " + groceryListFiles);
		System.exit (0);
	}

	public GroceryList updateQuantity(GroceryRequest groceryRequest)
	{
		return groceryRepository.updateQuantity (groceryRequest.getProductQuantity (),
				groceryRequest.getProductName ());
	}

	public void deleteGrocery(Long id)
	{
		groceryRepository.deleteById (id);
	}

	public String prepareGroceryListReport(GroceryResponse groceryResponse, String userId)
			throws IOException, DocumentException, URISyntaxException
	{
		Document document = new Document ();
		String   fileName = "grocery_lists/groceryList_" + userId + "_" + LocalDate.now () + ".pdf";
		PdfWriter.getInstance (document, new FileOutputStream (fileName));

		document.open ();

//		LocalDate orderDate = groceryResponse.getData ().stream ().findFirst ().get ().getOrderDate ();

		Font font = FontFactory.getFont (FontFactory.COURIER, 16, BaseColor.BLACK);
		String localDate = LocalDate.now ().format (DateTimeFormatter.ofLocalizedDate (FormatStyle.FULL));
		Chunk chunk = new Chunk (localDate, font);
		document.add (new Paragraph ("\n"));

		document.add (chunk);

		PdfPTable table = new PdfPTable (2);
		addTableHeader (table);
		addRows (table, groceryResponse);
		//		addCustomRows(table);

		document.add (table);
		document.close ();

		return fileName;
	}

	private void addTableHeader(PdfPTable table)
	{
		Stream.of ("Grocery Name", "Quantity").forEach (columnTitle -> {
			PdfPCell header = new PdfPCell ();
//			header.setBackgroundColor (BaseColor.LIGHT_GRAY);
			header.setBackgroundColor (BaseColor.ORANGE);
			header.setBorderWidth (2);
			header.setPhrase (new Phrase (columnTitle));
			table.addCell (header);
		});
	}

	private void addRows(PdfPTable table, GroceryResponse groceryResponse)
	{
		groceryResponse.getData ().forEach (grocery -> {
			table.addCell (grocery.getProductName ());
			table.addCell (grocery.getProductQuantity ());
		});
		/*table.addCell("row 1, col 1");
		table.addCell("row 1, col 2");
		table.addCell("row 1, col 3");*/
	}

	private void sendGroceryListToTelegram(String filePath, Long telegramChannelId) throws TelegramApiException, FileNotFoundException
	{
		TelegramBotsApi botsApi = new TelegramBotsApi (DefaultBotSession.class);
		TelegramBot     bot     = new TelegramBot ();
//		botsApi.registerBot (bot);
		//		bot.sendText(bot.sarala_telegram_id, "Yeah.....");
		SendDocument sendDocument = new SendDocument ();
//		sendDocument.setChatId (bot.sarala_telegram_id);
		sendDocument.setChatId (telegramChannelId);
		InputFile   document    = new InputFile ();
		File        file        = new File ("/Users/vamsiarisetti/MyDisk/MyCode/groceries-app/" + filePath);
		String      fileName    = file.getAbsoluteFile ().getName ();
		InputStream inputStream = new FileInputStream (file);
		document.setMedia (inputStream, fileName);
		sendDocument.setCaption ("Monthly Grocery List");
		sendDocument.setDocument (document);
//		bot.execute (sendDocument);
		bot.executeAsync (sendDocument);
		bot.onClosing ();
		bot.clearWebhook ();
	}

	public static void main(String[] args) throws DocumentException, IOException, URISyntaxException
	{
//		GroceryModel groceryModel = new GroceryModel (1L, "Test Product", "Test Quantity", LocalDate.now (), "testUser");
//		GroceryResponse groceryResponse = new GroceryResponse (List.of (groceryModel), "", "");
//		prepareGroceryListReport(groceryResponse, "testUser");
	}
}
