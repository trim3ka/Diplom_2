package praktikum;

public class Constants {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";
    public static final String AUTH_ENDPOINT = "/api/auth";

    public static final String CREATE_USER = Constants.AUTH_ENDPOINT + "/register";
    public static final String LOGIN_USER = Constants.AUTH_ENDPOINT + "/login";
    public static final String USER_MANAGEMENT = Constants.AUTH_ENDPOINT + "/user"; // для GET, PATCH, DELETE
    public static final String CREATE_ORDER = "/api/orders";
    public static final String GET_INGREDIENTS = "/api/ingredients";
}