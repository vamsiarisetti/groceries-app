package com.org.groceries.telegram;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TelegramBot extends TelegramLongPollingBot
{
	public Long sarala_telegram_id  = 861760458L;
	//	public Long pinni_telegram_id = 2005698367L;
//	public Long ramesh_telegram_id = 1663430855L;

	public Long TELEGRAM_CHANNEL_AR = -1001582642320L;
	public Long TELEGRAM_CHANNEL_YE = -1001858558063L;
	public Long TELEGRAM_CHANNEL_VA = -1001586387382L;

	@Override
	public String getBotUsername() {
		return "groceryHelper_bot";
	}

	@Override
	public String getBotToken() {
		return "5904082410:AAEq2CKlcHXESHOx7UTGXOcAs_sCqTwIoLM";
	}

	@Override
	public void onUpdateReceived(Update update) {
		Message message = update.getMessage ();
//		User    user    = message.getFrom ();
		Long chatId = Optional.ofNullable (message).map (msg ->
			msg.getFrom ().getId ()
		).orElse (update.getChannelPost ().getChat ().getId ());
		System.out.println ("chat Id : "+chatId);
		/*Long chatId = Optional.ofNullable (update.getChannelPost ()).map (channelMsg -> {
			Long   channelId      = -1001586387382L;
			Long   id             = channelMsg.getChat ().getId ();
			String channelMsgText = channelMsg.getText ();
			return id;
		}).get ();*/
		//		var msg = update.getMessage();
//		var user = msg.getFrom();
//		Long id = user.getId ();

//		System.out.println(user.getFirstName() + " wrote " + message.getText());
	}

	public void sendText(Long who, String what){
		SendMessage sm = SendMessage.builder()
				.chatId(who.toString()) //Who are we sending a message to
				.text(what).build();    //Message content
		try {
			execute(sm);                        //Actually sending the message
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);      //Any error will be printed here
		}
	}

	/*public void sendGroceryListToTelegram()
	{
		AsyncHttpClient client = new DefaultAsyncHttpClient();
		client.prepare("POST", "https://api.telegram.org/bot5904082410%3AAAEq2CKlcHXESHOx7UTGXOcAs_sCqTwIoLM/sendDocument")
				.setHeader("accept", "application/json")
				.setHeader("content-type", "application/json")
				.setBody("{\"document\":\"Required\",\"caption\":\"Optional\",\"disable_notification\":false,\"reply_to_message_id\":null,\"chat_id\":\"2005698367\"}")
				.execute("")
				.toCompletableFuture()
				.thenAccept(System.out::println)
				.join();

		client.close();
	}*/

	/*private static async Task<string> Upload(string actionUrl,string chat_id,string fileParamName, byte[] paramFileStream, string filename)
	{
		var formContent = new MultipartFormDataContent
		{
			{new StringContent(chat_id),"chat_id"},
			{new StreamContent(new MemoryStream(paramFileStream)),fileParamName,filename}
		};
		var myHttpClient = new HttpClient();
		var response = await myHttpClient.PostAsync(actionUrl.ToString(), formContent);
		string stringContent = await response.Content.ReadAsStringAsync();

		return stringContent;
	}*/
	public static void main(String[] args) throws TelegramApiException, IOException
	{
		TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
		TelegramBot     bot     = new TelegramBot ();
		botsApi.registerBot(bot);
//		bot.sendText(bot.sarala_telegram_id, "Yeah.....");
		SendDocument sendDocument = new SendDocument ();
//		sendDocument.setChatId (bot.sarala_telegram_id);
		sendDocument.setChatId (bot.TELEGRAM_CHANNEL_VA);
		InputFile document = new InputFile ();
		File      file     = new File ("/Users/vamsiarisetti/MyDisk/MyCode/groceries-app/grocery_lists/groceryList_2023-02-11.pdf");
		String    fileName     = file.getAbsoluteFile ().getName ();
		InputStream inputStream = new FileInputStream (file);
		document.setMedia (inputStream, fileName);
		sendDocument.setCaption ("Monthly Grocery List");
		sendDocument.setDocument (document);
//		sendDocument.setDocument (new InputFile ("https://localhost/download/groceryList_2023-02-11.pdf"));
//		sendDocument.setDocument (new InputFile ("file:///Users/vamsiarisetti/MyDisk/MyCode/groceries-app/grocery_lists/groceryList_2023-02-11.pdf"));
//		sendDocument.setDocument (new InputFile ("https://www.africau.edu/images/default/sample.pdf"));
//		sendDocument.setDocument (new InputFile ("https://localhost/download/sample.pdf"));
		bot.execute (sendDocument);
//		bot.executeAsync (sendDocument);
	}
}
