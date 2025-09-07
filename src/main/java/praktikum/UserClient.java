package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import io.restassured.http.ContentType;
import praktikum.model.UserCreated;
import praktikum.model.UserLogin;
import static io.restassured.RestAssured.given;

public class UserClient {

    @Step("Создание нового пользователя")
    public ValidatableResponse getNewUser(UserCreated user) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(Constants.BASE_URL)
                .body(user)
                .when()
                .post(Constants.CREATE_USER)
                .then().log().all();
    }

    @Step("Логин пользователя в системе")
    public ValidatableResponse logInUser(UserLogin user) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(Constants.BASE_URL)
                .body(user)
                .when()
                .post(Constants.LOGIN_USER)
                .then().log().all();
    }

    @Step("Удаление пользователя из системы")
    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(Constants.BASE_URL)
                .header("Authorization", accessToken) // Токен в заголовке
                .when()
                .delete(Constants.USER_MANAGEMENT)
                .then().log().all();
    }

    @Step("Получение информации о пользователе")
    public ValidatableResponse getUser(String accessToken) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(Constants.BASE_URL)
                .header("Authorization", accessToken)
                .when()
                .get(Constants.USER_MANAGEMENT)
                .then().log().all();
    }

    @Step("Обновление информации о пользователе")
    public ValidatableResponse userUpdate(UserLogin user, String accessToken) {
        return given()
                .log().all()
                .contentType(ContentType.JSON)
                .baseUri(Constants.BASE_URL)
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch(Constants.USER_MANAGEMENT)
                .then().log().all();
    }

}
