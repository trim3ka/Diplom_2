package praktikum;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.UserCreated;
import praktikum.model.UserLogin;

import java.net.HttpURLConnection;

import static org.hamcrest.Matchers.equalTo;

class UserLoginTest {

    private UserClient client;
    private String accessToken;
    private UserCreated user;

    @BeforeEach
    public void setUp() {
        client = new UserClient();
        user = UserCreated.random();

        // Создаем пользователя для авторизации и получаем токен
        var response = client.getNewUser(user)
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true))
                .extract().body().jsonPath();

        accessToken = response.getString("accessToken");
    }

    @Test
    @DisplayName("Авторизация пользователя под существующим пользователем")
    public void logInCorrectUser() {
        logInWithValidCredentials();
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным email")
    public void logInUserWithInvalidEmail() {
        logInWithInvalidEmail();
    }

    @Test
    @DisplayName("Авторизация пользователя с неверным password")
    public void logInUserWithInvalidPassword() {
        logInWithInvalidPassword();
    }

    @Test
    @DisplayName("Авторизация пользователя с не заполненным email")
    public void logInUserWithoutEmail() {
        logInWithoutEmail();
    }

    @Test
    @DisplayName("Авторизация пользователя с не заполненным password")
    public void logInUserWithoutPassword() {
        logInWithoutPassword();
    }

    @Step("Авторизация с корректными учетными данными")
    private void logInWithValidCredentials() {
        client.logInUser(UserLogin.from(user))
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_OK)
                .body("success", equalTo(true));
    }

    @Step("Авторизация с неверным email")
    private void logInWithInvalidEmail() {
        user.setEmail("wrong_email");
        client.logInUser(UserLogin.from(user))
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Авторизация с неверным паролем")
    private void logInWithInvalidPassword() {
        user.setPassword("wrong_password");
        client.logInUser(UserLogin.from(user))
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Авторизация без email")
    private void logInWithoutEmail() {
        user.setEmail(null);
        client.logInUser(UserLogin.from(user))
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Step("Авторизация без пароля")
    private void logInWithoutPassword() {
        user.setPassword(null);
        client.logInUser(UserLogin.from(user))
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("message", equalTo("email or password are incorrect"));
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