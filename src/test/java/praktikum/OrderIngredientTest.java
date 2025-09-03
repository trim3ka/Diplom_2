package praktikum;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.OrderCreated;
import praktikum.model.UserCreated;

import java.net.HttpURLConnection;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;

public class OrderIngredientTest {
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
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWithIngredients() {
        createOrderWithValidIngredients();
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        createOrderWithoutAnyIngredients();
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientHash() {
        createOrderWithInvalidIngredient();
    }

    @Step("Создание заказа с валидными ингредиентами")
    private void createOrderWithValidIngredients() {
        OrderCreated order = new OrderCreated(validIngredientId);
        orderClient.createOrder(order, accessToken)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .body("order.ingredients.size()", equalTo(1));
    }

    @Step("Создание заказа без ингредиентов")
    private void createOrderWithoutAnyIngredients() {
        OrderCreated order = new OrderCreated(Collections.emptyList());
        orderClient.createOrder(order, accessToken)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Создание заказа с неверным хешем ингредиента")
    private void createOrderWithInvalidIngredient() {
        OrderCreated order = new OrderCreated("invalid_hash_12345");
        orderClient.createOrder(order, accessToken)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
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
