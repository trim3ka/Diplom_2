package praktikum;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.OrderCreated;
import praktikum.model.UserCreated;

import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.equalTo;

class OrderTest {
    private UserClient client;
    private OrderClient orderClient;
    private String accessToken;
    private UserCreated user;
    private String validIngredientId;

    @BeforeEach
    public void setUp() {
        client = new UserClient();
        orderClient = new OrderClient();
        user = UserCreated.random();

        // Создаем пользователя
        var response = client.getNewUser(user)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .extract().body().jsonPath();

        accessToken = response.getString("accessToken");

        // Получаем валидный ID ингредиента
        var ingredientsResponse = orderClient.getIngredients()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .extract().body().jsonPath();

        // Берем первый доступный ингредиент
        validIngredientId = ingredientsResponse.getString("data[0]._id");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuth() {
        createOrderWithAuthorization();
    }

    @Step("Создание заказа с авторизацией пользователя")
    private void createOrderWithAuthorization() {
        OrderCreated order = new OrderCreated(validIngredientId);
        orderClient.createOrder(order, accessToken)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("order.status", equalTo("done"));
    }

    @AfterEach
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken)
                    .assertThat()
                    .statusCode(202)
                    .body("success", equalTo(true))
                    .body("message", equalTo("User successfully removed"));
        }
    }
}