package ru.training.qa;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationConcurrencyTest {

    private Integer register(String username, String password) {
        return given().contentType(ContentType.JSON).body(Map.of(
                        "username", username,
                        "password", password
                )).when()
                .post("/api/v1/auth/register")
                .getStatusCode();
    }

    @Test
    void shouldReturn201And409WhenSameUsernameIsRegisteredConcurrently() throws Exception {

        String username = "artem_" + UUID.randomUUID();

        CountDownLatch startGate = new CountDownLatch(1); // синхронизатор со счетчиком один
        CountDownLatch readyGate = new CountDownLatch(2); // синхронизатор со счетчиком два

        Callable<Integer> task = () -> {
            readyGate.countDown(); // задача говорит: «я готова»
            startGate.await(); // задача останавливается и ждёт общего старта

            return register(username, "c83HF@3mFcOB%H");
        }; // задача, которая выполняется в отдельном потоке и может вернуть результат

        ExecutorService executor = Executors.newFixedThreadPool(2); // создает менеджер с двумя потоками
        try {
            Future<Integer> firstResult = executor.submit(task); // это обещание будущего результата
            Future<Integer> secondResult = executor.submit(task);
            readyGate.await();
            startGate.countDown();

            int firstStatus = firstResult.get(10, TimeUnit.SECONDS);
            int secondStatus = secondResult.get(10, TimeUnit.SECONDS);

            List<Integer> statuses = List.of(firstStatus, secondStatus);

            assertAll(
                    () -> assertEquals(1, Collections.frequency(statuses, 201)), // считает, что в списке statuses количество 201 равняется одному
                    () -> assertEquals(1, Collections.frequency(statuses, 409)) // аналогично за подсчет отвечает именно frequency
            );
        }
        finally {
            executor.shutdownNow(); // executor обязательно должен быть закрытым
        }
    }
}
