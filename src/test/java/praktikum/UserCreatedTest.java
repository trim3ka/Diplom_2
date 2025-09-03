package praktikum;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.UserCreated;

import java.net.HttpURLConnection;

import static io.qameta.allure.Allure.step;
import static org.hamcrest.Matchers.equalTo;

public class UserCreatedTest {
    final UserClient client = new UserClient();
    private String accessToken;

    @Test
    @DisplayName("Успешное создание пользователя")
    public void createUniqueUser() {
        var user = UserCreated.random();

        var response = client.getNewUser(user)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .extract().body().jsonPath();

        accessToken = response.getString("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя который уже зарегистрирован")
    public void createNotUniqueUser(){

        var user = UserCreated.random();

        step("Первое создание пользователя", () -> {
                    var firstResponse = client.getNewUser(user)
                            .assertThat()
                            .statusCode(HttpURLConnection.HTTP_OK)
                            .body("success", equalTo(true))
                            .extract().body().jsonPath();

                    accessToken = firstResponse.getString("accessToken");
                });

        step("Повторное создание такого же пользователя", () -> {
            client.getNewUser(user)
                    .assertThat()
                    .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                    .body("success", equalTo(false))
                    .body("message", equalTo("User already exists"));
        });
    }

    @Test
    @DisplayName("Создание пользователя без заполнения обяз поля")
    public void createUserWithoutRequiredFields() {
        createUserWithoutEmail();
        createUserWithoutName();
        createUserWithoutPassword();
    }

    @Step("Создание пользователя без email")
    public void createUserWithoutEmail() {
        var user = UserCreated.random();
        user.setEmail(null);

        client.getNewUser(user)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));

    }

    @Step("Создание пользователя без name")
    public void createUserWithoutName() {
        var user = UserCreated.random();
        user.setName(null);

        client.getNewUser(user)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Step("Создание пользователя без password")
    public void createUserWithoutPassword() {
        var user = UserCreated.random();
        user.setPassword(null);
        client.getNewUser(user)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
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