package com.org.groceries.service;

import net.javacrumbs.shedlock.core.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;

@Service
public class GroceryTaskSchedulerService
{
	@Autowired
	GroceryService groceryService;
	// At 12:00:00pm, on the 31st day, every month
	//@Scheduled(cron = "0 0 12 31 * ?")
	// Every minute at second :00 between 00am and 01am
//	@Scheduled(cron = "*/1 * * * *")
//	@SchedulerLock(name = "TaskScheduler_scheduledTask",
//			lockAtLeastForString = "PT5M", lockAtMostForString = "PT14M")
	public void scheduleTask()
	{
		System.out.println ("In scheduler......");
	}

	/*@Scheduled(cron = "0 0/1 * * * *")
	@SchedulerLock(name = "groceryTask", lockAtMostForString = "PT40S", lockAtLeastForString = "PT20S")
	public void scheduledTask() throws TelegramApiException, FileNotFoundException
	{
		System.out.println ("In scheduler......");
		groceryService.sendGroceryList();
		System.out.println ("completed scheduler process....");
	}*/
}
