package praktikum;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.OrderCreated;
import praktikum.model.UserCreated;

import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.*;

class UserOrdersTest {
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

        validIngredientId = ingredientsResponse.getString("data[0]._id");
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuthTest() {
        getUserOrdersWithAuthorization();
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    public void getUserOrdersWithoutAuthTest() {
        getUserOrdersWithoutAuthorization();
    }

    @Step("Получение заказов авторизованного пользователя")
    private void getUserOrdersWithAuthorization() {
        // Создаем заказ
        OrderCreated order = new OrderCreated(validIngredientId);
        orderClient.createOrder(order, accessToken)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true));

        // Получаем заказы пользователя
        orderClient.getUserOrders(accessToken)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("orders", not(empty())); // Проверяем что orders не пустой
    }

    @Step("Получение заказов неавторизованного пользователя")
    private void getUserOrdersWithoutAuthorization() {
        orderClient.getUserOrdersWithoutAuth()
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
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