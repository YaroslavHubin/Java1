import java.util.concurrent.Semaphore;

public class TheChess {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(1); //Задаємо обмеження для використання лише одного потоку
        int totalMoves = 5; // Кількість ходів, яку зробить кожен гравець (для імітації гри у шахи)

        Runnable runnable1 = () -> {
            for (int i = 1; i <= totalMoves; i++) {
                try {
                    semaphore.acquire();// Захоплення семафора перед початком ходу
                    System.out.printf("Гравець %s розпочав свій %d хід \n", Thread.currentThread().getName(), i);// Після захоплення семафора гравець починає свій хід
                    synchronizedMethod(); // Синхронізований метод, в якому гравець думає над ходом

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    System.out.printf("Гравець %s завершив свій %d хід \n", Thread.currentThread().getName(), i);
                    semaphore.release(); // Звільнення семафора після завершення ходу
                }

                // Імітація паузи між ходами
                try {
                    Thread.sleep(500); // Пауза перед наступним ходом
                    if (i <= totalMoves-1){
                        System.out.printf("Гравець %s очікує на свій хід \n", Thread.currentThread().getName()); //Виводимо повідомлення допоки гравці грають, якщо буде останній хід, то відповідно наступному гравцю не треба очікувати хід
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread myThread1 = new Thread(runnable1, "Білих"); //Перші зазвичай ходять білі, тому перший потік іменуємо білими
        Thread myThread2 = new Thread(runnable1, "Чорних"); // Другими зазвичай ходять чорні, тому другий потік іменуємо чорними

        myThread1.start(); //Запускаємо потоки
        myThread2.start();
    }
    //Метод для імітації обдумування ходів гравцями
    private static void synchronizedMethod() {
        try {
            System.out.printf("Гравець %s думає над своїм ходом \n", Thread.currentThread().getName());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e); //Ловимо помилку, якщо така иникає
        }
    }

}
