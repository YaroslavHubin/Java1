package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableScheduling
public class SpringBootIntellijCeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootIntellijCeApplication.class, args);
	}

	// Уникаємо конфлікту імені біна taskExecutor
	@Bean(name = "customTaskExecutor")
	public ScheduledExecutorService customTaskExecutor() {
		return Executors.newScheduledThreadPool(5);
	}
}

@Component
class ScheduledTasks {

	private int attempt = 1;

	// Задача з трьома спробами
	@Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
	public void executeWithRetries() {
		System.out.println("Початок виконання задачі з повторними спробами");
		while (attempt <= 3) {
			try {
				System.out.println("Спроба №" + attempt);
				performTask(); // Ваш метод, який може викликати помилку
				System.out.println("Задача виконана успішно на спробі №" + attempt);
				break; // Вихід із циклу після успішного виконання
			} catch (Exception e) {
				System.out.println("Помилка під час виконання на спробі №" + attempt);
				attempt++;
				try {
					TimeUnit.SECONDS.sleep(5); // Затримка між спробами
				} catch (InterruptedException interruptedException) {
					Thread.currentThread().interrupt();
					System.out.println("Перервано під час очікування");
				}
			}
		}
		if (attempt > 3) {
			System.out.println("Не вдалося виконати задачу після 3 спроб.");
		}
	}

	// Метод, який може викликати помилку
	private void performTask() throws Exception {
		if (Math.random() < 0.7) { // 70% шанс помилки
			throw new Exception("Випадкова помилка");
		}
	}

	// Задача, яка виконується через 15 секунд після запуску програми
	@Scheduled(initialDelay = 15000, fixedDelay = Long.MAX_VALUE)
	public void executeAfter15Seconds() {
		System.out.println("15 секунд від запуску програми");
	}
}
