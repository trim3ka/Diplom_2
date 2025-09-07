package praktikum;

import io.qameta.allure.Step;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import praktikum.model.UserCreated;
import praktikum.model.UserLogin;

import java.net.HttpURLConnection;
import java.util.Random;

import static org.hamcrest.Matchers.equalTo;

class UserUpdateUnauthTest {
    private UserClient client;
    private String accessToken;
    private UserCreated user;
    private Random random = new Random();

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
    @DisplayName("Изменение email для не авторизованного пользователя")
    public void changeEmailForNotLoggedInUserTest() {
        changeEmailForNotLoggedInUser();
    }

    @Test
    @DisplayName("Изменение name для не авторизованного пользователя")
    public void changeNameForNotLoggedInUserTest() {
        changeNameForNotLoggedInUser();
    }

    @Test
    @DisplayName("Изменение password для не авторизованного пользователя")
    public void changePasswordForNotLoggedInUserTest() {
        changePasswordForNotLoggedInUser();
    }

    @Step("Смена email для не вторизованного пользователя")
    private void changeEmailForNotLoggedInUser() {
        String newEmail = "new_email_" + random.nextInt(200) + "@yandex.ru";

        // Пользователь с новым email
        UserLogin updatedUser = new UserLogin(
                newEmail,               // новый email
                user.getName(),         // старое имя
                user.getPassword()      // старый пароль
        );

        // Обновляем данные пользователя
        client.userUpdate(updatedUser, "")
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        }

    @Step("Смена name для не авторизованного пользователя")
    private void changeNameForNotLoggedInUser() {
        String newName = "new_name_" + random.nextInt(200);

        // Пользователь с новым email
        UserLogin updatedUser = new UserLogin(
                user.getEmail(),
                newName,
                user.getPassword()
        );

        // Обновляем данные пользователя
        client.userUpdate(updatedUser, "")
                .assertThat()
                .statusCode(HttpURLConnection.HTTP_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Step("Смена name для не авторизованного пользователя")
    private void changePasswordForNotLoggedInUser() {
        String newPassword = "Password_" + random.nextInt(200);

        // Пользователь с новым email
        UserLogin updatedUser = new UserLogin(
                user.getEmail(),
                user.getName(),
                newPassword
        );

        // Обновляем данные пользователя
        client.userUpdate(updatedUser, "")
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